package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import info.vericoin.verimobile.Managers.WalletManager;

public class SplashActivity extends WalletAppKitActivity {

    private VeriMobileApplication veriMobileApplication;

    private WalletManager walletManager;

    public static Intent createIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        veriMobileApplication = (VeriMobileApplication) getApplication();
        walletManager = veriMobileApplication.getWalletManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (walletManager.doesWalletExist(SplashActivity.this)) {
            walletManager.startWallet(SplashActivity.this);
        } else {
            startActivity(WelcomeActivity.createIntent(SplashActivity.this));
            finish();
        }
    }

    @Override
    protected void onWalletKitReady() {
        if (doesPasswordExist()) {
            startActivity(UnlockActivity.createIntent(SplashActivity.this)); //Ask for password;
        } else {
            startActivity(MainActivity.createIntent(SplashActivity.this));
        }
        finish(); //Prevent app from going back to this activity after its finished.
    }

    public boolean doesPasswordExist() {
        return veriMobileApplication.getPasswordManager().doesPasswordExist();
    }

}
