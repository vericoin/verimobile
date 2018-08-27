package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;


public class DecryptWalletActivity extends VeriActivity {

    private final static String VERI_TRANSACTION = "veriTransaction";
    private TextInputLayout passwordLayout;
    private Button unlockButton;
    private VeriTransaction veriTransaction;
    private PasswordManager passwordManager;

    public static Intent createIntent(Context context, VeriTransaction veriTransaction) {
        Intent intent = new Intent(context, DecryptWalletActivity.class);
        intent.putExtra(VERI_TRANSACTION, veriTransaction);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_wallet);
        passwordManager = ((VeriMobileApplication) getApplication()).getPasswordManager();

        veriTransaction = (VeriTransaction) getIntent().getSerializableExtra(VERI_TRANSACTION);

        unlockButton = findViewById(R.id.unlockButton);

        passwordLayout = findViewById(R.id.passwordLayout);

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordCorrect()) {
                    veriTransaction.setPassword(getPassword());
                    startActivity(AmountActivity.createIntent(DecryptWalletActivity.this, veriTransaction));
                    finish(); //Prevent app from going back to this activity after its finished.
                } else {
                    passwordLayout.setError(getString(R.string.password_is_incorrect));
                }
            }
        });

    }

    public String getPassword() {
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean isPasswordCorrect() {
        return passwordManager.checkPassword(getPassword());
    }

}
