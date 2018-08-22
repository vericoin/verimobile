package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import org.bitcoinj.kits.WalletAppKit;

import info.vericoin.verimobile.Listeners.OnConnectListener;
import info.vericoin.verimobile.ViewModules.NewPasswordValidation;

public class CreateWalletActivity extends VeriActivity {

    private Button createWalletButton;

    private ProgressBar progressBar;

    private TextInputLayout passwordLayout;

    private TextInputLayout rePasswordLayout;

    private CheckBox encryptWallet;

    private CheckBox noPasswordBox;

    private VeriMobileApplication veriMobileApplication;

    private NewPasswordValidation newPasswordValidation;

    public static Intent createIntent(Context context) {
        return new Intent(context, CreateWalletActivity.class);
    }

    public void disableNewPassword() {
        passwordLayout.setEnabled(false);
        rePasswordLayout.setEnabled(false);
        encryptWallet.setEnabled(false);
    }

    public void enableNewPassword() {
        passwordLayout.setEnabled(true);
        rePasswordLayout.setEnabled(true);
        encryptWallet.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);
        veriMobileApplication = (VeriMobileApplication) getApplication();

        createWalletButton = findViewById(R.id.createWalletButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        passwordLayout = findViewById(R.id.passwordInputLayout);
        rePasswordLayout = findViewById(R.id.rePasswordInputLayout);
        encryptWallet = findViewById(R.id.encryptWallet);

        noPasswordBox = findViewById(R.id.noPasswordBox);

        newPasswordValidation = new NewPasswordValidation(passwordLayout, rePasswordLayout);

        noPasswordBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disableNewPassword();
                } else {
                    enableNewPassword();
                }
            }
        });

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPasswordValidation.resetErrors();
                if (noPasswordBox.isChecked()) {
                    WalletSingleton.startWallet(CreateWalletActivity.this);
                    startApp();
                } else if (newPasswordValidation.checkValidity()) {
                    String password = newPasswordValidation.getPassword();
                    createWalletButton.setEnabled(false);
                    createWalletButton.setText("");
                    progressBar.setVisibility(View.VISIBLE);
                    savePassword(password);
                    if (shouldEncryptWallet()) {
                        WalletSingleton.startWallet(CreateWalletActivity.this, password);
                    } else {
                        WalletSingleton.startWallet(CreateWalletActivity.this);
                    }
                    startApp();
                }
            }
        });
    }

    public void startApp(){
        startActivity(SplashActivity.createIntent(CreateWalletActivity.this));
        ActivityCompat.finishAffinity(CreateWalletActivity.this); //Prevent app from going back to previous activities.
    }

    public void savePassword(String password) {
        veriMobileApplication.newPassword(password);
    }

    public boolean shouldEncryptWallet() {
        return encryptWallet.isChecked();
    }

}
