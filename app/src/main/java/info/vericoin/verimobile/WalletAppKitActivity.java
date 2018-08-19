package info.vericoin.verimobile;

import android.os.Bundle;

import org.bitcoinj.kits.WalletAppKit;

public abstract class WalletAppKitActivity extends VeriActivity {

    protected WalletAppKit kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WalletConnection.connect(this, new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(WalletAppKit walletAppKit) {
                WalletAppKitActivity.this.kit = walletAppKit;
                onWalletKitReady();
            }
        });
    }

    protected abstract void onWalletKitReady();

}
