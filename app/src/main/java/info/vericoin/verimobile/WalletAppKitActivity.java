package info.vericoin.verimobile;

import android.os.Bundle;

import org.bitcoinj.kits.WalletAppKit;

import info.vericoin.verimobile.Listeners.OnConnectListener;

public abstract class WalletAppKitActivity extends VeriActivity implements OnConnectListener {

    protected WalletAppKit kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WalletConnection.addConnectListener(this);
    }

    @Override
    public void OnSetUpComplete(WalletAppKit walletAppKit) {
        WalletAppKitActivity.this.kit = walletAppKit;
        onWalletKitReady();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        WalletConnection.removeConnectListener(this);
    }

    protected abstract void onWalletKitReady();

}
