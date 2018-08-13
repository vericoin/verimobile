package info.vericoin.verimobile;

import android.os.Bundle;

import org.bitcoinj.kits.WalletAppKit;

public class SplashActivity extends VeriActivity {

    private BitcoinApplication bitcoinApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bitcoinApplication = (BitcoinApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(WalletConnection.doesWalletExist(SplashActivity.this)){
            WalletConnection.startAsync(SplashActivity.this);
            WalletConnection.connect(new WalletConnection.OnConnectListener() {

                @Override
                public void OnSetUpComplete(WalletAppKit kit) {
                    if(doesPasswordExist()){
                        startActivity(UnlockActivity.createIntent(SplashActivity.this)); //Ask for password;
                    }else {
                        startActivity(MainActivity.createIntent(SplashActivity.this));
                    }
                    finish(); //Prevent app from going back to this activity after its finished.
                }

            });
        }else {
            startActivity(WelcomeActivity.createIntent(SplashActivity.this));
            finish();
        }
    }

    public boolean doesPasswordExist(){
        return bitcoinApplication.doesPasswordExist();
    }

}
