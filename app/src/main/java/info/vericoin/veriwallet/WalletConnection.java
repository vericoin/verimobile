package info.vericoin.veriwallet;

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
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.util.concurrent.Executor;

public class WalletConnection {

    public interface OnConnectListener{
        void OnSetUpComplete(WalletAppKit kit);
    }

    private static OnCoinReceiveListener onCoinReceiveListener;
    private static OnNewBestBlockListener onNewBestBlockListener;

    private static boolean startUpComplete = false;

    public interface OnCoinReceiveListener{
        void onCoinsReceived(final Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance);
        void onSuccess(final Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance, TransactionConfidence result);
        void onFailure(final Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance);
    }

    public interface OnNewBestBlockListener{
        void newBlock(StoredBlock block);
    }

    public static void setOnNewBestBlockListener(OnNewBestBlockListener onNewBestBlockListener) {
        WalletConnection.onNewBestBlockListener = onNewBestBlockListener;
    }

    public static void setOnCoinReceiveListener(OnCoinReceiveListener onCoinReceiveListener) {
        WalletConnection.onCoinReceiveListener = onCoinReceiveListener;
    }

    private static OnConnectListener connectListener;

    private static NetworkParameters params = TestNet3Params.get();
    private static String filePrefix = "forwarding-service-testnet";
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

    public static void createConnection(final Context context){

        BriefLogFormatter.init();
        new Thread(new Runnable() {
            public void run() {
                connectToWallet(context);
            }
        }).start();

    }

    public static void connectToWallet(Context context){
        // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
        kit = new WalletAppKit(params, context.getFilesDir(), filePrefix) {
            @Override
            protected void onSetupCompleted() {

                //WalletAppKit is now ready to be used.
                connectListener.OnSetUpComplete(kit);
                startUpComplete = true;

                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());

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

            }
        };

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
                    connectListener.OnSetUpComplete(kit);
                }
            }
        });
    }

    /*/
    private ServiceReceiver serviceReceiver = new ServiceReceiver(new ServiceReceiver.OnConnectionReceivedListener() {
        @Override
        public void ConnectionReceived() {
            if(kit != null) {
                connectListener.OnWalletRunning(kit);
            }
        }
    });
    /*/

    /*/
    private android.content.ServiceConnection serviceConnection = new android.content.ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BitcoinBinder bitcoinBinder = (BitcoinBinder) iBinder;

            kit = bitcoinBinder.getKit();
            if(kit.isRunning()){
                connectListener.OnWalletRunning(kit);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    /*/

    public static void connect(OnConnectListener newListener){
        /*/
        Intent serviceIntent = BitcoinService.createIntent(context);
        context.startService(serviceIntent);
        context.bindService(serviceIntent, serviceConnection, 0);


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(BitcoinService.BROADCAST_ESTABLISHED_CONNECTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(serviceReceiver, filter);
        /*/

        connectListener = newListener;
        if(kit != null && startUpComplete){
            connectListener.OnSetUpComplete(kit);
        }
    }

    public static WalletAppKit getKit(){
        return kit;
    }

    public static void disconnect(){
        onCoinReceiveListener = null;
        connectListener = null;
        onNewBestBlockListener = null;
        /*/
        LocalBroadcastManager.getInstance(context).unregisterReceiver(serviceReceiver);
        context.unbindService(serviceConnection);
        /*/
    }
}
