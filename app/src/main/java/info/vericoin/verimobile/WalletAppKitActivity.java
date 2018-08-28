package info.vericoin.verimobile;

import android.os.Bundle;

import org.bitcoinj.kits.WalletAppKit;

import info.vericoin.verimobile.Listeners.OnConnectListener;
import info.vericoin.verimobile.Managers.WalletManager;

public abstract class WalletAppKitActivity extends VeriActivity implements OnConnectListener {

    protected WalletAppKit kit;

    private boolean walletKitReady = false;

    private boolean resumeStateActive = false;

    private VeriMobileApplication veriMobileApplication;

    private WalletManager walletManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        veriMobileApplication = (VeriMobileApplication) getApplication();
        walletManager = veriMobileApplication.getWalletManager();

        walletManager.addConnectListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        resumeStateActive = true;

        if (walletKitReady) {
            onWalletKitResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumeStateActive = false;
    }

    @Override
    public void onSetUpComplete(WalletAppKit walletAppKit) {
        WalletAppKitActivity.this.kit = walletAppKit;
        onWalletKitReady();
        walletKitReady = true;

        if (resumeStateActive) {
            onWalletKitResume();
        }
    }

    @Override
    public void onStopAsync() {
        onWalletKitStop();
        walletKitReady = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        walletManager.removeConnectListener(this);
    }

    protected void onWalletKitResume() {
    }

    protected void onWalletKitStop() {
    }

    protected void onWalletKitReady() {
    }

}
