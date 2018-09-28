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
import android.widget.Toast;

import info.vericoin.verimobile.Managers.WalletManager;
import info.vericoin.verimobile.ViewModules.NewPasswordValidation;

public class CreateWalletActivity extends VeriActivity {

    private Button createWalletButton;

    private ProgressBar progressBar;

    private TextInputLayout passwordLayout;

    private TextInputLayout rePasswordLayout;

    private CheckBox encryptWallet;

    private CheckBox noPasswordBox;

    private WalletManager walletManager;

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
        walletManager = ((VeriMobileApplication) getApplication()).getWalletManager();

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
                loading();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (noPasswordBox.isChecked()) {
                                walletManager.createNewWallet(CreateWalletActivity.this);
                            } else if (newPasswordValidation.checkValidity()) {
                                String password = newPasswordValidation.getPassword();
                                walletManager.createNewWallet(CreateWalletActivity.this, password, shouldEncryptWallet());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startApp();
                                }
                            });
                        } catch (final Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    failed(e.toString());
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    public void loading() {
        createWalletButton.setEnabled(false);
        createWalletButton.setText("");
        progressBar.setVisibility(View.VISIBLE);
    }

    public void failed(String error) {
        createWalletButton.setEnabled(false);
        createWalletButton.setText("");
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(CreateWalletActivity.this, error, Toast.LENGTH_LONG).show();
    }

    public void startApp() {
        startActivity(SplashActivity.createIntent(CreateWalletActivity.this, true));
        ActivityCompat.finishAffinity(CreateWalletActivity.this); //Prevent app from going back to previous activities.
    }

    public boolean shouldEncryptWallet() {
        return encryptWallet.isChecked();
    }

}
