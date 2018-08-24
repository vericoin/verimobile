package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.kits.WalletAppKit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import info.vericoin.verimobile.Listeners.OnConnectListener;

public class SplashActivity extends VeriActivity implements OnConnectListener {

    private VeriMobileApplication veriMobileApplication;

    public static Intent createIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        veriMobileApplication = (VeriMobileApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (WalletSingleton.doesWalletExist(SplashActivity.this)) {
            WalletSingleton.startWallet(SplashActivity.this);
            WalletSingleton.addConnectListener(this);
        } else {
            startActivity(WelcomeActivity.createIntent(SplashActivity.this));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WalletSingleton.removeConnectListener(this);
    }

    @Override
    public void onSetUpComplete(WalletAppKit kit) {
        if (doesPasswordExist()) {
            startActivity(UnlockActivity.createIntent(SplashActivity.this)); //Ask for password;
        } else {
            startActivity(MainActivity.createIntent(SplashActivity.this));
        }
        finish(); //Prevent app from going back to this activity after its finished.
    }

    @Override
    public void onStopAsync() {

    }

    public boolean doesPasswordExist() {
        return veriMobileApplication.doesPasswordExist();
    }

}
