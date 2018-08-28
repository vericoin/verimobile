package info.vericoin.verimobile;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import info.vericoin.verimobile.Managers.PasswordManager;
import info.vericoin.verimobile.Models.VeriTransaction;
import info.vericoin.verimobile.Util.FingerprintHelper;


@TargetApi(28)
public class UnlockActivity extends VeriActivity {

    public final static String VERI_TRANSACTION = "veriTransaction";

    private TextInputLayout passwordLayout;

    private Button unlockButton;

    private ConstraintLayout fingerPrintButton;

    private FingerprintHelper fingerprintHelper;

    private VeriMobileApplication veriMobileApplication;

    private PasswordManager passwordManager;

    private VeriTransaction veriTransaction;

    public static Intent createIntent(Context context) {
        return new Intent(context, UnlockActivity.class);
    }

    public static Intent createIntent(Context context, VeriTransaction veriTransaction) {
        Intent intent = new Intent(context, UnlockActivity.class);
        intent.putExtra(VERI_TRANSACTION, veriTransaction);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_wallet);

        veriMobileApplication = (VeriMobileApplication) getApplication();
        passwordManager = veriMobileApplication.getPasswordManager();

        veriTransaction = (VeriTransaction) getIntent().getSerializableExtra(VERI_TRANSACTION);

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
                passwordLayout.setErrorEnabled(false);
                if (isPasswordCorrect()) {
                    unlockWallet();
                } else {
                    passwordLayout.setError(getString(R.string.password_is_incorrect));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean fingerPrintEnabled = veriMobileApplication.isFingerPrintEnabled();

        if (!showFingerPrintButton() || !fingerPrintEnabled) {
            fingerPrintButton.setVisibility(View.GONE);
        } else {
            fingerPrintButton.setVisibility(View.VISIBLE);
            fingerprintHelper.attemptToUnlock(); //Try to use Biometric or Fingerprint Manager if device supports it.
        }
    }

    public boolean showFingerPrintButton() {
        if (fingerprintHelper.isFingerPrintSupported()) {
            return true;
        } else {
            return false;
        }
    }

    public void unlockWallet() {
        if (veriTransaction == null) {
            startActivity(MainActivity.createIntent(UnlockActivity.this));
        } else {
            startActivity(AmountActivity.createIntent(this, veriTransaction));
        }
        finish(); //Prevent app from going back to this activity after its finished.
    }

    public String getPassword() {
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean isPasswordCorrect() {
        return passwordManager.checkPassword(getPassword());
    }

}
