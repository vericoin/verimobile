package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import info.vericoin.verimobile.Util.UtilMethods;
import info.vericoin.verimobile.ViewModules.NewPasswordValidation;

public class ChangePasswordActivity extends WalletAppKitActivity {

    private TextInputLayout currentPasswordLayout;
    private TextInputLayout newPasswordLayout;
    private TextInputLayout reNewPasswordLayout;
    private Button changePasswordButton;
    private CheckBox encryptWalletBox;

    private ProgressBar progressBar;

    private CheckBox noPasswordBox;

    private PasswordManager passwordManager;

    private NewPasswordValidation newPasswordValidation;

    public static Intent createIntent(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    protected void onWalletKitStop() {
        changePasswordButton.setOnClickListener(null);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_change_password);
        passwordManager = ((VeriMobileApplication) getApplication()).getPasswordManager();

        currentPasswordLayout = findViewById(R.id.currentPasswordInputLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        reNewPasswordLayout = findViewById(R.id.reNewPasswordLayout);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        encryptWalletBox = findViewById(R.id.encryptWallet);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        noPasswordBox = findViewById(R.id.noPasswordBox);
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

        newPasswordValidation = new NewPasswordValidation(newPasswordLayout, reNewPasswordLayout);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPasswordValidation.resetErrors();
                currentPasswordLayout.setErrorEnabled(false);

                if(doesPasswordExist()){
                    if(isCurrentPasswordCorrect(getCurrentPassword())){
                        if(noPasswordBox.isChecked()){
                            updatePassword();
                        }else{
                            if(newPasswordValidation.checkValidity()){
                                updatePassword();
                            }
                        }
                    }else{
                        currentPasswordLayout.setError(getString(R.string.password_is_incorrect));
                    }
                }else{
                    updatePassword();
                }
            }
        });
    }

    public void disableNewPassword() {
        newPasswordLayout.setEnabled(false);
        reNewPasswordLayout.setEnabled(false);
        encryptWalletBox.setEnabled(false);
    }

    public void enableNewPassword() {
        newPasswordLayout.setEnabled(true);
        reNewPasswordLayout.setEnabled(true);
        encryptWalletBox.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (doesPasswordExist()) {
            currentPasswordLayout.setEnabled(true);
        } else {
            currentPasswordLayout.setEnabled(false);
        }

    }

    public boolean doesPasswordExist() {
        return passwordManager.doesPasswordExist();
    }

    public void updatePassword() {

        loading();

        final String password = newPasswordValidation.getPassword();

        if (noPasswordBox.isChecked()) {
            passwordManager.removePassword();
        } else {
            passwordManager.newPassword(password);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (kit.wallet().isEncrypted()) {
                        decryptWallet(getCurrentPassword());
                    }

                    if (encryptWalletBox.isChecked() && !noPasswordBox.isChecked()) {
                        encryptWallet(password);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.password_updated), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }catch(final Exception e){
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

    public void loading(){
        changePasswordButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        changePasswordButton.setText("");
    }

    public void failed(String error){
        changePasswordButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        changePasswordButton.setText(R.string.update_password_button);
        Toast.makeText(ChangePasswordActivity.this, error, Toast.LENGTH_LONG).show();
    }

    public String getCurrentPasswordHash() {
        return passwordManager.getPasswordHash();
    }

    public void decryptWallet(String password) {
        kit.wallet().decrypt(password);
    }

    public void encryptWallet(String password) {
        kit.wallet().encrypt(password);
    }

    public String getCurrentPassword() {
        return currentPasswordLayout.getEditText().getText().toString();
    }

    public boolean isCurrentPasswordCorrect(String oldPassword) {
        String passwordHash = getCurrentPasswordHash();
        return (passwordHash.isEmpty() || passwordHash.equals(UtilMethods.hashStringSHA256(oldPassword)));
    }

}
