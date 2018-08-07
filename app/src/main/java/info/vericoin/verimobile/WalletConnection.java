package info.vericoin.verimobile;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

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
        void OnSyncComplete();
    }

    private static OnCoinReceiveListener onCoinReceiveListener;
    private static OnNewBestBlockListener onNewBestBlockListener;
    private static OnWalletChangeListener onWalletChangeListener;

    private static boolean startUpComplete = false;
    private static boolean syncComplete = false;

    public interface OnCoinReceiveListener{
        void onCoinsReceived(final Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance);
        void onSuccess(final Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance, TransactionConfidence result);
        void onFailure(final Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance);
    }

    public interface OnNewBestBlockListener{
        void newBlock(StoredBlock block);
    }

    public interface OnWalletChangeListener{
        void walletChanged(Wallet wallet);
    }

    public static void setOnWalletChangeListener(OnWalletChangeListener onWalletChangeListener) {
        WalletConnection.onWalletChangeListener = onWalletChangeListener;
    }

    public static void setOnNewBestBlockListener(OnNewBestBlockListener onNewBestBlockListener) {
        WalletConnection.onNewBestBlockListener = onNewBestBlockListener;
    }

    public static void setOnCoinReceiveListener(OnCoinReceiveListener onCoinReceiveListener) {
        WalletConnection.onCoinReceiveListener = onCoinReceiveListener;
    }

    private static OnConnectListener connectListener;

    private static NetworkParameters params = TestNet3Params.get();
    public static final String filePrefix = "forwarding-service-testnet";
    private static WalletAppKit kit;
    public static Executor runInUIThread = new Executor() {
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

                wallet().addCoinsReceivedEventListener(runInUIThread, new WalletCoinsReceivedEventListener() {
                    @Override
                    public void onCoinsReceived(final Wallet wallet, final Transaction tx, final Coin prevBalance, final Coin newBalance) {
                        // Runs in the dedicated "user thread".
                        //
                        // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).

                        // Wait until it's made it into the block chain (may run immediately if it's already there).
                        //
                        // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
                        // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
                        // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
                        // case of waiting for a block.

                        if(onCoinReceiveListener != null) {
                            onCoinReceiveListener.onCoinsReceived(wallet, tx, prevBalance, newBalance);
                        }

                        Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                            @Override
                            public void onSuccess(TransactionConfidence result) {
                                // "result" here is the same as "tx" above, but we use it anyway for clarity.
                                if(onCoinReceiveListener != null) {
                                    onCoinReceiveListener.onSuccess(wallet, tx, prevBalance, newBalance, result);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                if(onCoinReceiveListener != null) {
                                    onCoinReceiveListener.onFailure(wallet, tx, prevBalance, newBalance);
                                }
                            }
                        }, runInUIThread);
                    }
                });

                chain().addNewBestBlockListener(runInUIThread, new NewBestBlockListener() {
                    @Override
                    public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
                        if(onNewBestBlockListener != null){
                            onNewBestBlockListener.newBlock(block);
                        }
                    }
                });

                wallet().addChangeEventListener(runInUIThread, new WalletChangeEventListener() {
                    @Override
                    public void onWalletChanged(Wallet wallet) {
                        if(onWalletChangeListener != null){
                            onWalletChangeListener.walletChanged(wallet);
                        }
                    }
                });

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
                if(connectListener != null){
                    connectListener.OnSyncComplete();
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

        if(kit != null && syncComplete){
            connectListener.OnSyncComplete();
        }
    }

    public static WalletAppKit getKit(){
        return kit;
    }

    public static void disconnect(){
        onCoinReceiveListener = null;
        connectListener = null;
        onNewBestBlockListener = null;
        onWalletChangeListener = null;
    }
}
