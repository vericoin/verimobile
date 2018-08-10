package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.kits.WalletAppKit;


public class DecryptWalletActivity extends AppCompatActivity{

    private TextInputLayout passwordLayout;

    private Button unlockButton;

    private Address address;

    private Coin amount;

    private WalletAppKit kit;

    private BitcoinApplication bitcoinApplication;

    private final static String ADDRESS_EXTRA = "address";
    private final static String AMOUNT_EXTRA = "amount";

    public static Intent createIntent(Context context, Address toAddr, Coin amount){
        Intent intent = new Intent(context, DecryptWalletActivity.class);
        intent.putExtra(ADDRESS_EXTRA, toAddr);
        intent.putExtra(AMOUNT_EXTRA, amount);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_wallet);
        bitcoinApplication = (BitcoinApplication) getApplication();

        address = (Address) getIntent().getSerializableExtra(ADDRESS_EXTRA);
        amount = (Coin) getIntent().getSerializableExtra(AMOUNT_EXTRA);

        unlockButton = findViewById(R.id.unlockButton);

        passwordLayout = findViewById(R.id.passwordLayout);

    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(final WalletAppKit kit) {
                DecryptWalletActivity.this.kit = kit;
                unlockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPasswordCorrect()){
                            startActivity(ProcessTransactionActivity.createIntent(DecryptWalletActivity.this, address, amount, getPassword()));
                            finish(); //Prevent app from going back to this activity after its finished.
                        }else{
                            passwordLayout.setError("Password is incorrect");
                        }
                    }
                });
            }

            @Override
            public void OnSyncComplete() {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }

    public String getPassword(){
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean isPasswordCorrect(){
        return bitcoinApplication.checkPassword(getPassword());
    }

}
