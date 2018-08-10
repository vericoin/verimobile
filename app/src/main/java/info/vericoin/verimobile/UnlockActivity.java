package info.vericoin.verimobile;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;


@TargetApi(28)
public class UnlockActivity extends AppCompatActivity {

    public final static String COIN_EXTRA = "coin";
    public final static String ADDRESS_EXTRA = "address";

    private TextInputLayout passwordLayout;

    private Button unlockButton;

    private ConstraintLayout fingerPrintButton;

    private FingerprintHelper fingerprintHelper;

    private BitcoinApplication bitcoinApplication;

    private Coin coin;
    private Address address;

    public static Intent createIntent(Context context){
        return new Intent(context, UnlockActivity.class);
    }

    public static Intent createIntent(Context context, Coin coin, Address address){
        Intent intent = new Intent(context, UnlockActivity.class);
        intent.putExtra(COIN_EXTRA, coin);
        intent.putExtra(ADDRESS_EXTRA, address);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_wallet);
        bitcoinApplication = (BitcoinApplication) getApplication();

        coin = (Coin) getIntent().getSerializableExtra(COIN_EXTRA);
        address = (Address) getIntent().getSerializableExtra(ADDRESS_EXTRA);

        fingerprintHelper = new FingerprintHelper(this);

        unlockButton = findViewById(R.id.unlockButton);

        passwordLayout = findViewById(R.id.passwordLayout);

        fingerPrintButton = findViewById(R.id.fingerPrintButton);

        fingerPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fingerprintHelper.attemptToUnlock();
            }
        });

        fingerprintHelper.setListener(new FingerprintHelper.OnAuthListener() {
            @Override
            public void onSuccess() {
                unlockWallet();
            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordCorrect()){
                    unlockWallet();
                }else{
                    passwordLayout.setError("Password is incorrect");
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        boolean fingerPrintEnabled = bitcoinApplication.isFingerPrintEnabled();

        if(!showFingerPrintButton() || !fingerPrintEnabled){
            fingerPrintButton.setVisibility(View.GONE);
        }else{
            fingerPrintButton.setVisibility(View.VISIBLE);
            fingerprintHelper.attemptToUnlock(); //Try to use Biometric or Fingerprint Manager if device supports it.
        }
    }

    public boolean showFingerPrintButton(){
        if(fingerprintHelper.isFingerPrintSupported()){
            return true;
        }else{
            return false;
        }
    }

    public void unlockWallet(){
        if(coin == null || address == null) {
            startActivity(MainActivity.createIntent(UnlockActivity.this));
        }else{
            startActivity(ProcessTransactionActivity.createIntent(this, address, coin));
        }
        finish(); //Prevent app from going back to this activity after its finished.
    }

    public String getPassword(){
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean isPasswordCorrect(){
        return bitcoinApplication.checkPassword(getPassword());
    }

}
