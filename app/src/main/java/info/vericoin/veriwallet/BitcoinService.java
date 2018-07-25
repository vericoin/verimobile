package info.vericoin.veriwallet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.BitmapCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

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
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class BitcoinService extends Service {

    public final static String BROADCAST_ESTABLISHED_CONNECTION = "BitcoinService.Connection.Established";
    protected static int mStartMode = Service.START_STICKY;
    protected static NetworkParameters params = TestNet3Params.get();
    protected static String filePrefix = "forwarding-service-testnet";
    protected WalletAppKit kit;
    protected Executor executorService = MoreExecutors.newDirectExecutorService();
    protected Intent establishedConnectionIntent = new Intent().setAction(BROADCAST_ESTABLISHED_CONNECTION);

    public static Intent createIntent(Context context){
        return new Intent(context, BitcoinService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        BriefLogFormatter.init();

        // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
        kit = new WalletAppKit(params, getFilesDir(), filePrefix) {
            @Override
            protected void onSetupCompleted() {

                // Send broadcast to Service Receivers that the WalletAppKit set up is complete, and is ready to be used.
                LocalBroadcastManager.getInstance(BitcoinService.this).sendBroadcast(establishedConnectionIntent);

                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());

                final Executor runInUIThread = new Executor() {
                    @Override public void execute(Runnable runnable) {

                        Handler handler = new Handler(Looper.getMainLooper());
                        // For Android: handler was created in an Activity.onCreate method.
                        handler.post(runnable);
                    }
                };

                wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
                    @Override
                    public void onCoinsReceived(final Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                        // Runs in the dedicated "user thread".
                        //
                        // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).

                        // Wait until it's made it into the block chain (may run immediately if it's already there).
                        //
                        // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
                        // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
                        // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
                        // case of waiting for a block.

                        Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                            @Override
                            public void onSuccess(TransactionConfidence result) {
                                // "result" here is the same as "tx" above, but we use it anyway for clarity.

                            }

                            @Override
                            public void onFailure(Throwable t) {
                            }
                        }, executorService);
                    }
                });

                chain().addNewBestBlockListener(new NewBestBlockListener() {
                    @Override
                    public void notifyNewBestBlock(StoredBlock block) throws VerificationException {

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

        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
