package info.vericoin.verimobile;

import android.os.Bundle;

import org.bitcoinj.kits.WalletAppKit;

import info.vericoin.verimobile.Listeners.OnConnectListener;

public abstract class WalletAppKitActivity extends VeriActivity implements OnConnectListener {

    protected WalletAppKit kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WalletSingleton.addConnectListener(this);
    }

    @Override
    public void onSetUpComplete(WalletAppKit walletAppKit) {
        WalletAppKitActivity.this.kit = walletAppKit;
        onWalletKitReady();
    }

    @Override
    public void onStopAsync() {
        onWalletKitStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WalletSingleton.removeConnectListener(this);
    }

    protected abstract void onWalletKitStop();

    protected abstract void onWalletKitReady();

}
