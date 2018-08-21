package info.vericoin.verimobile;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.DeterministicSeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class WalletConnection {

    private static VeriMobileApplication veriMobileApplication;

    public static void setVeriMobileApplication(VeriMobileApplication veriMobileApplication) {
        WalletConnection.veriMobileApplication = veriMobileApplication;
    }

    public static final String filePrefix = "forwarding-service-testnet";
    private static boolean startUpComplete = false;
    private static boolean starting = false;
    private static boolean finishedDownloading = false;
    private static OnConnectListener connectListener;
    private static NetworkParameters params = TestNet3Params.get();
    private static WalletAppKit kit;
    private static Executor runInUIThread = new Executor() {
        @Override
        public void execute(Runnable runnable) {
            Handler handler = new Handler(Looper.getMainLooper());
            // For Android: handler was created in an Activity.onCreate method.
            handler.post(runnable);
        }
    };
    /*
    private static HandlerThread backgroundHandler = new HandlerThread("backgroundHandler");
    private static Executor runInBackgroundThread = new Executor() {
        @Override
        public void execute(@NonNull Runnable runnable) {
            if(!backgroundHandler.isAlive()){
                backgroundHandler.start();
            }
            Handler handler = new Handler(backgroundHandler.getLooper());
            handler.post(runnable);
        }
    };*/

    public static Executor getRunInUIThread() {
        return runInUIThread;
    }

    public static boolean doesWalletExist(Context context) {
        return (new File(context.getFilesDir(), filePrefix + ".wallet").exists() || kit != null);
    }

    public static void importWallet(Context context, Uri uri) throws IOException, NullPointerException{
        File file = new File(context.getFilesDir(), filePrefix + ".wallet");

        if(file.exists()){
            file.delete();
        }

        file.createNewFile();

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        FileOutputStream fop = new FileOutputStream(file);

        int data = inputStream.read();
        while (data != -1) {
            fop.write(data);
            data = inputStream.read();
        }
        inputStream.close();
        fop.close();
    }

    public static void deleteWallet(final Context c){
        new Thread(new Runnable() {
            @Override
            public void run() {
                kit.wallet().clearAllListeners();
                kit.chain().clearAllListeners();
                kit.stopImmediately();

                veriMobileApplication.removePassword();

                kit = null;
                starting = false;
                startUpComplete = false;
                connectListener = null;
                finishedDownloading = false;
                blockDownloadListeners.clear();

                File walletFile = new File(c.getFilesDir(), filePrefix + ".wallet");
                walletFile.delete();

                File chainFile = new File(c.getFilesDir(), filePrefix + ".spvchain");
                chainFile.delete();

                c.startActivity(WelcomeActivity.createIntent(c));
            }
        }).start();
    }

    private static ArrayList<BlockDownloadListener> blockDownloadListeners = new ArrayList<>();

    public static void addBlockDownloadListener(BlockDownloadListener listener) {
        if(finishedDownloading){
            listener.doneDownload();
        }
        blockDownloadListeners.add(listener);
    }

    public interface BlockDownloadListener {
        void progress(double pct, int blocksSoFar, Date date);
        void doneDownload();
    }

    private static void initWalletAppKit(Context context, final String password){
        BriefLogFormatter.init();
        // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
        kit = new WalletAppKit(params, context.getFilesDir(), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeyChainGroupSize() < 1) {
                    wallet().importKey(new ECKey());
                }

                if (!password.isEmpty()) {
                    wallet().encrypt(password);
                }

                //WalletAppKit is now ready to be used.
                runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(connectListener != null) {
                            connectListener.OnSetUpComplete(kit);
                        }
                    }
                });
                startUpComplete = true;

            }
        };
        kit.setBlockingStartup(false);
        kit.setDownloadListener(new DownloadProgressTracker(){
            @Override
            protected void progress(double pct, int blocksSoFar, Date date) {
                for(BlockDownloadListener listener: blockDownloadListeners){
                    listener.progress(pct, blocksSoFar, date);
                }
            }
            @Override
            protected void doneDownload(){
                finishedDownloading = true;
                for(BlockDownloadListener listener: blockDownloadListeners){
                    listener.doneDownload();
                }
            }
        });
    }

    private static void startWalletAsync(){

        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

        // Download the block chain.
        try {
            kit.startAsync();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void startWallet(Context context) {
        startWallet(context, "");
    }

    public static void startWallet(Context context, final String password) {

        if (kit == null) { //Only init kit if it does not exist yet.
            initWalletAppKit(context, password);
        }

        if(!starting) {
            startWalletAsync();
            starting = true;
        }
    }

    public static void importFromSeed(final Context context, final String password, final List<String> mnemonicList, final long creationTime){

        if(kit == null) {
            initWalletAppKit(context, password);
        }

        kit.restoreWalletFromSeed(new DeterministicSeed(mnemonicList, null, "", creationTime));

        if(!starting) {
            startWalletAsync();
            starting = true;
        }
    }

    public static void setConnectListener(OnConnectListener newListener) {
        connectListener = newListener;
        if (kit != null && startUpComplete) { //Check to see if Wallet is already running.
            connectListener.OnSetUpComplete(kit);
        }
    }

    public interface OnConnectListener {
        void OnSetUpComplete(WalletAppKit kit);
    }

}
