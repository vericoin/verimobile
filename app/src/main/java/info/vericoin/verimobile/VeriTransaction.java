package info.vericoin.verimobile;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

public class VeriTransaction {

    private OnBroadcastListener listener;

    public interface OnBroadcastListener{
        void broadcastComplete(org.bitcoinj.core.Transaction tx);
    }

    public void setBroadcastListener(OnBroadcastListener listener){
        this.listener = listener;
    }

    public void sendTransaction(String amount, String toAddr){
        Coin value = Coin.parseCoin(amount);

        WalletAppKit kit = WalletConnection.getKit();
        try {
            SendRequest request = SendRequest.to(Address.fromString(kit.params() , toAddr), value);
            final Wallet.SendResult sendResult = kit.wallet().sendCoins(request);

            // Register a callback that is invoked when the transaction has propagated across the network.
            // This shows a second style of registering ListenableFuture callbacks, it works when you don't
            // need access to the object the future returns.
            sendResult.broadcastComplete.addListener(new Runnable() {
                @Override
                public void run() {
                    if(listener != null){
                        listener.broadcastComplete(sendResult.tx);
                    }
                }
            }, WalletConnection.getRunInUIThread());
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
    }
}
