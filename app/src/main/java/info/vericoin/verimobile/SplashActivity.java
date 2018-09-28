package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import info.vericoin.verimobile.Managers.WalletManager;

public class SplashActivity extends WalletAppKitActivity {

    private final static String BUNDLE_BACK_UP_WALLET= "backUp";

    private boolean showBackUpWalletActivity;

    public static Intent createIntent(Context context, boolean showBackUpWalletActivity) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra(BUNDLE_BACK_UP_WALLET, showBackUpWalletActivity);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        showBackUpWalletActivity = getIntent().getBooleanExtra(BUNDLE_BACK_UP_WALLET, false);

        if (walletManager.doesWalletExist(SplashActivity.this)) {
            walletManager.startWallet(SplashActivity.this);
        } else {
            startActivity(WelcomeActivity.createIntent(SplashActivity.this));
            finish();
        }
    }

    @Override
    protected void onWalletKitReady() {

        if(showBackUpWalletActivity) {
            startActivity(BackUpWalletInfoActivity.createIntent(this));
        }else if (doesPasswordExist()) {
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
