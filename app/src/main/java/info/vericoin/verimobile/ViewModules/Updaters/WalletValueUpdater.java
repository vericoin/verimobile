package info.vericoin.verimobile.ViewModules.Updaters;

import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.utils.ExchangeRate;
import org.bitcoinj.utils.Fiat;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;

import info.vericoin.verimobile.Managers.WalletManager;
import info.vericoin.verimobile.Util.UtilMethods;

public class WalletValueUpdater implements WalletChangeEventListener {

    private TextView confirmedTextView;

    private TextView unconfirmedTextView;

    private Wallet wallet;

    private ExchangeRate exchangeRate;

    private boolean showFiatAmount = false;

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public WalletValueUpdater(Wallet wallet, TextView confirmedTextView, TextView unconfirmedTextView) {
        this.wallet = wallet;
        this.confirmedTextView = confirmedTextView;
        this.unconfirmedTextView = unconfirmedTextView;
    }

    public void swapCurrency(){
        showFiatAmount = !showFiatAmount;
        updateWalletView();
    }

    public void listenForBalanceChanges() {
        wallet.addChangeEventListener(WalletManager.runInUIThread, this);
    }

    public void updateWalletView() {
        Coin confirmed = wallet.getBalance(Wallet.BalanceType.AVAILABLE);
        Coin unconfirmed = wallet.getBalance(Wallet.BalanceType.ESTIMATED).subtract(confirmed);

        if(showFiatAmount && exchangeRate != null){
            Fiat confirmedFiat = exchangeRate.coinToFiat(confirmed);
            Fiat unconfirmedFiat = exchangeRate.coinToFiat(unconfirmed);
            confirmedTextView.setText(UtilMethods.roundFiat(confirmedFiat).toFriendlyString());
            unconfirmedTextView.setText(UtilMethods.roundFiat(unconfirmedFiat).toFriendlyString());
        }else {
            confirmedTextView.setText(confirmed.toFriendlyString());
            unconfirmedTextView.setText(unconfirmed.toFriendlyString());
        }
    }

    @Override
    public void onWalletChanged(Wallet wallet) {
        updateWalletView();
    }

    public void stopListening() {
        wallet.removeChangeEventListener(this);
    }
}