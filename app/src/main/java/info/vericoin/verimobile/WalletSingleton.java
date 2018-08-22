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

import info.vericoin.verimobile.Listeners.BlockDownloadListener;
import info.vericoin.verimobile.Listeners.OnConnectListener;

public class WalletSingleton {

    private static final String FILE_PREFIX = "forwarding-service-testnet";
    private static final String WALLET_FILE_NAME = FILE_PREFIX + ".wallet";
    private static final String CHAIN_FILE_NAME = FILE_PREFIX + ".spvchain";
    private static VeriMobileApplication veriMobileApplication;
    private static boolean startUpComplete = false;
    private static boolean starting = false;
    private static boolean finishedDownloadingBlockchain = false;
    private static List<OnConnectListener> connectListeners = new ArrayList<>();
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
    private static ArrayList<BlockDownloadListener> blockDownloadListeners = new ArrayList<>();
    private WalletSingleton() { //Prevent singleton from being constructed.
    }

    public static void setVeriMobileApplication(VeriMobileApplication veriMobileApplication) {
        WalletSingleton.veriMobileApplication = veriMobileApplication;
    }

    public static Executor getRunInUIThread() {
        return runInUIThread;
    }

    public static boolean doesWalletExist(Context context) {
        return (new File(context.getFilesDir(), WALLET_FILE_NAME).exists() || kit != null);
    }

    public static void importWallet(Context context, Uri uri) throws IOException, NullPointerException {
        File file = new File(context.getFilesDir(), WALLET_FILE_NAME);

        if (file.exists()) {
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

    public static void deleteWallet(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                stopWalletAsync();

                veriMobileApplication.removePassword();

                deleteWalletFile(context);
                deleteChainFile(context);

                context.startActivity(WelcomeActivity.createIntent(context));
            }
        }).start();
    }

    public static void stopWalletAsync() {

        for (final OnConnectListener listener : connectListeners) {
            runInUIThread.execute(new Runnable() {
                @Override
                public void run() {
                    listener.onStopAsync();
                }
            });
        }

        clearListeners();

        kit.stopImmediately();

        resetVariables();
    }

    private static void clearListeners() {
        connectListeners.clear();
        blockDownloadListeners.clear();
    }

    private static void resetVariables() {
        kit = null;
        starting = false;
        startUpComplete = false;
        finishedDownloadingBlockchain = false;
    }

    private static void deleteWalletFile(Context context) {
        File walletFile = new File(context.getFilesDir(), WALLET_FILE_NAME);
        walletFile.delete();
    }

    private static void deleteChainFile(Context context) {
        File chainFile = new File(context.getFilesDir(), CHAIN_FILE_NAME);
        chainFile.delete();
    }

    public static void addBlockDownloadListener(BlockDownloadListener listener) {
        if (finishedDownloadingBlockchain) {
            listener.finishedDownload();
        }
        blockDownloadListeners.add(listener);
    }

    public static void removeBlockDownloadListener(BlockDownloadListener listener) {
        blockDownloadListeners.remove(listener);
    }

    private static void initWalletAppKit(Context context, final String password) {
        BriefLogFormatter.init();
        // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
        kit = new WalletAppKit(params, context.getFilesDir(), FILE_PREFIX) {
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
                        for (OnConnectListener onConnectListener : connectListeners) {
                            onConnectListener.onSetUpComplete(kit);
                        }
                    }
                });
                startUpComplete = true;

            }
        };
        kit.setBlockingStartup(false);
        kit.setDownloadListener(new DownloadProgressTracker() {
            @Override
            protected void progress(final double pct, final int blocksSoFar, final Date date) {
                runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (BlockDownloadListener listener : blockDownloadListeners) {
                            listener.progress(pct, blocksSoFar, date);
                        }
                    }
                });
            }

            @Override
            protected void doneDownload() {
                runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        finishedDownloadingBlockchain = true;
                        for (BlockDownloadListener listener : blockDownloadListeners) {
                            listener.finishedDownload();
                        }
                    }
                });
            }
        });
    }

    private static void startWalletAsync() {

        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

        // Download the block chain.
        try {
            kit.startAsync();
        } catch (Exception e) {
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

        if (!starting) {
            startWalletAsync();
            starting = true;
        }
    }

    public static void importFromSeed(final Context context, final String password, final List<String> mnemonicList, final long creationTime) {

        if (kit == null) {
            initWalletAppKit(context, password);
        }

        kit.restoreWalletFromSeed(new DeterministicSeed(mnemonicList, null, "", creationTime));

        if (!starting) {
            startWalletAsync();
            starting = true;
        }
    }

    public static void addConnectListener(OnConnectListener listener) {
        if (kit != null && startUpComplete) { //Check to see if Wallet is already running.
            listener.onSetUpComplete(kit);
        }
        connectListeners.add(listener);
    }

    public static void removeConnectListener(OnConnectListener listener) {
        connectListeners.remove(listener);
    }

}