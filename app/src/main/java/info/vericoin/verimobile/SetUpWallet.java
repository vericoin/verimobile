package info.vericoin.verimobile;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public abstract class SetUpWallet extends VeriActivity {

    private ProgressBar progressBar;

    protected VeriMobileApplication veriMobileApplication;

    private TextInputLayout passwordLayout;

    private TextInputLayout rePasswordLayout;

    private Button importWalletButton;

    private CheckBox encryptWallet;

    private CheckBox noPassword;

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.activity_set_up_decrypted_wallet);
        veriMobileApplication = (VeriMobileApplication) getApplication();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        passwordLayout = findViewById(R.id.passwordInputLayout);
        rePasswordLayout = findViewById(R.id.rePasswordInputLayout);
        importWalletButton = findViewById(R.id.importWalletButton);
        encryptWallet = findViewById(R.id.encryptWallet);
        noPassword = findViewById(R.id.noPasswordBox);

        importWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetError();
                if(isNoPassword()){
                    importWallet("");
                }else{
                    if(isPasswordEmpty()){
                        setPasswordError("Password can not be empty");
                    }else if(arePaswordsEqual()) {
                        importWallet(getPassword());
                    }else{
                        setRePasswordError("Passwords do not match.");
                    }
                }
            }
        });

        noPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    passwordLayout.setEnabled(false);
                    rePasswordLayout.setEnabled(false);
                    encryptWallet.setEnabled(false);
                }else{
                    passwordLayout.setEnabled(true);
                    rePasswordLayout.setEnabled(true);
                    encryptWallet.setEnabled(true);
                }
            }
        });

    }

    public boolean isPasswordEmpty(){
        return passwordLayout.getEditText().getText().toString().isEmpty();
    }

    abstract void importWallet(String password);

    public void resetError(){
        passwordLayout.setErrorEnabled(false);
        rePasswordLayout.setErrorEnabled(false);
    }

    public void importing(){
        progressBar.setVisibility(View.VISIBLE);
        importWalletButton.setText("");
        importWalletButton.setEnabled(false);
    }

    public void importFailed(String error){
        progressBar.setVisibility(View.GONE);
        importWalletButton.setText("Import");
        importWalletButton.setEnabled(true);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    public void importComplete(){
        Toast.makeText(this, "Wallet has been imported!", Toast.LENGTH_LONG).show();
        startActivity(SplashActivity.createIntent(SetUpWallet.this));
    }

    public boolean isEncryptWallet(){
        return encryptWallet.isChecked();
    }

    public boolean isNoPassword(){
        return noPassword.isChecked();
    }

    public void setPasswordError(String error){ passwordLayout.setError(error);}

    public void setRePasswordError(String error){
        rePasswordLayout.setError(error);
    }

    public boolean arePaswordsEqual(){
        return getPassword().equals(getRePassword());
    }

    public String getPassword(){
        return passwordLayout.getEditText().getText().toString();
    }

    public String getRePassword(){
        return rePasswordLayout.getEditText().getText().toString();
    }

}
