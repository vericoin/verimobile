package info.vericoin.verimobile;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.NewBestBlockListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class WalletConnection {

    public interface OnConnectListener{
        void OnSetUpComplete(WalletAppKit kit);
    }

    public interface OnSyncCompleteListener{
        void OnSyncComplete();
    }

    private static boolean startUpComplete = false;
    private static boolean syncComplete = false;

    private static OnConnectListener connectListener;
    private static OnSyncCompleteListener syncCompleteListener;

    private static NetworkParameters params = TestNet3Params.get();
    public static final String filePrefix = "forwarding-service-testnet";
    private static WalletAppKit kit;
    private static Executor runInUIThread = new Executor() {
        @Override public void execute(Runnable runnable) {
            Handler handler = new Handler(Looper.getMainLooper());
            // For Android: handler was created in an Activity.onCreate method.
            handler.post(runnable);
        }
    };

    public static Executor getRunInUIThread() {
        return runInUIThread;
    }

    public static void startAsync(final Context context){
        startAsync(context, "");
    }

    public static void startAsync(final Context context, final String password){

        if(kit == null) { //Only start async if it has not already started.
            BriefLogFormatter.init();
            new Thread(new Runnable() {
                public void run() {
                    connectToWallet(context.getApplicationContext(), password);
                }
            }).start();
        }

    }

    public static boolean doesWalletExist(Context context){
        return new File( context.getFilesDir(), filePrefix + ".wallet").exists();
    }

    public static void connectToWallet(Context context, final String password){

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

                if(!password.isEmpty()){
                    wallet().encrypt(password);
                }

                //WalletAppKit is now ready to be used.
                runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        connectListener.OnSetUpComplete(kit);
                    }
                });
                startUpComplete = true;
            }
        };

        List<String> mnemonicCode = new ArrayList<String>();
        mnemonicCode.add("swallow");
        mnemonicCode.add("inject");
        mnemonicCode.add("snow");
        mnemonicCode.add("liberty");
        mnemonicCode.add("token");
        mnemonicCode.add("sample");
        mnemonicCode.add("zero");
        mnemonicCode.add("front");
        mnemonicCode.add("gas");
        mnemonicCode.add("common");
        mnemonicCode.add("daughter");
        mnemonicCode.add("door");

        long creationTimeSeconds = 1533600544;
        //kit.restoreWalletFromSeed(new DeterministicSeed(mnemonicCode, null, "", creationTimeSeconds));

        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

        // Download the block chain.
        kit.startAsync();
        kit.awaitRunning();

        runInUIThread.execute(new Runnable() {
            @Override
            public void run() {
                if(syncCompleteListener != null){
                    syncCompleteListener.OnSyncComplete();
                }
            }
        });
        syncComplete = true;

    }

    public static void connect(OnConnectListener newListener){

        connectListener = newListener;
        if(kit != null && startUpComplete){
            connectListener.OnSetUpComplete(kit);
        }

    }

    public static void setSyncCompleteListener(OnSyncCompleteListener syncCompleteListener) {
        WalletConnection.syncCompleteListener = syncCompleteListener;

        if(kit != null && syncComplete){
            syncCompleteListener.OnSyncComplete();
        }
    }

    public static WalletAppKit getKit(){
        return kit;
    }

}
