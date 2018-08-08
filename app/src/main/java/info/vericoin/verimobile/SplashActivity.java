package info.vericoin.verimobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.bitcoinj.kits.WalletAppKit;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = getSharedPreferences(BitcoinApplication.PREFERENCE_FILE_KEY, MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(WalletConnection.doesWalletExist(SplashActivity.this)){
            WalletConnection.startAsync(SplashActivity.this);
            WalletConnection.connect(new WalletConnection.OnConnectListener() {

                @Override
                public void OnSetUpComplete(WalletAppKit kit) {
                    String password = sharedPref.getString(BitcoinApplication.PASSWORD_HASH_PREF, "");
                    if(password.isEmpty()){
                        startActivity(MainActivity.createIntent(SplashActivity.this));
                    }else {
                        startActivity(UnlockActivity.createIntent(SplashActivity.this)); //Ask for password;
                    }
                    finish(); //Prevent app from going back to this activity after its finished.
                }

                @Override
                public void OnSyncComplete() {

                }
            });
        }else {
            startActivity(WelcomeActivity.createIntent(SplashActivity.this));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }
}
