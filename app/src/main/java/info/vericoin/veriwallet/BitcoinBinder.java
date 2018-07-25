package info.vericoin.veriwallet;

import android.os.Binder;

import com.google.common.util.concurrent.MoreExecutors;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;

import java.util.concurrent.Executor;

import static info.vericoin.veriwallet.BitcoinService.params;


public class BitcoinBinder extends Binder {

    protected WalletAppKit kit;
    protected Executor executorService = MoreExecutors.newDirectExecutorService();

    public BitcoinBinder(WalletAppKit kit) {
        this.kit = kit;
    }

    public void sendTransaction(String amount, String toAddr){
        Coin amountToSend = Coin.parseCoin(amount);

        Address address = Address.fromString(params, toAddr);
        try {
            Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(), address, amountToSend);

            // Register a callback that is invoked when the transaction has propagated across the network.
            // This shows a second style of registering ListenableFuture callbacks, it works when you don't
            // need access to the object the future returns.
            sendResult.broadcastComplete.addListener(new Runnable() {
                @Override
                public void run() {

                }
            }, executorService);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
    }

    public WalletAppKit getKit(){
        return kit;
    }
}
