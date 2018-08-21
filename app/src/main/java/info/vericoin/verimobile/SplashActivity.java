package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.bitcoinj.kits.WalletAppKit;

import info.vericoin.verimobile.Listeners.OnConnectListener;

public class SplashActivity extends VeriActivity implements OnConnectListener {

    private VeriMobileApplication veriMobileApplication;

    public static Intent createIntent(Context context){
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

        if (WalletConnection.doesWalletExist(SplashActivity.this)) {
            WalletConnection.startWallet(SplashActivity.this);
            WalletConnection.addConnectListener(this);
        } else {
            startActivity(WelcomeActivity.createIntent(SplashActivity.this));
            finish();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        WalletConnection.removeConnectListener(this);
    }

    @Override
    public void OnSetUpComplete(WalletAppKit kit) {
        if (doesPasswordExist()) {
            startActivity(UnlockActivity.createIntent(SplashActivity.this)); //Ask for password;
        } else {
            startActivity(MainActivity.createIntent(SplashActivity.this));
        }
        finish(); //Prevent app from going back to this activity after its finished.
    }

    public boolean doesPasswordExist() {
        return veriMobileApplication.doesPasswordExist();
    }

}
