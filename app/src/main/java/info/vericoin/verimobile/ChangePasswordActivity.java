package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.bitcoinj.kits.WalletAppKit;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout currentPasswordLayout;
    private TextInputLayout newPasswordLayout;
    private TextInputLayout reNewPasswordLayout;
    private Button changePasswordButton;
    private CheckBox encryptWalletBox;

    private WalletAppKit kit;

    private SharedPreferences sharedPref;

    private ProgressBar progressBar;

    private CheckBox noPasswordBox;

    public static Intent createIntent(Context context){
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordLayout = findViewById(R.id.currentPasswordInputLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        reNewPasswordLayout = findViewById(R.id.reNewPasswordLayout);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        encryptWalletBox = findViewById(R.id.encryptWallet);

        sharedPref = getSharedPreferences(BitcoinApplication.PREFERENCE_FILE_KEY, MODE_PRIVATE);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        noPasswordBox = findViewById(R.id.noPasswordBox);
        noPasswordBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    disableNewPassword();
                }else{
                    enableNewPassword();
                }
            }
        });
    }

    public void disableNewPassword(){
        newPasswordLayout.setEnabled(false);
        reNewPasswordLayout.setEnabled(false);
        encryptWalletBox.setEnabled(false);
    }

    public void enableNewPassword(){
        newPasswordLayout.setEnabled(true);
        reNewPasswordLayout.setEnabled(true);
        encryptWalletBox.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(final WalletAppKit kit) {
                ChangePasswordActivity.this.kit = kit;
                changePasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetErrors();

                        if(!isCurrentPasswordCorrect(getCurrentPassword())){
                            currentPasswordLayout.setError("Current password is not correct");
                        }else if(!doPasswordsMatch() && !noPasswordBox.isChecked()) {
                            newPasswordLayout.setError("Passwords do not match");
                        }else if(isNewPasswordEmpty() && !noPasswordBox.isChecked()){
                            newPasswordLayout.setError("Password can not be empty");
                        }else{
                            updatePassword(getNewPassword());
                        }
                    }
                });
            }

            @Override
            public void OnSyncComplete() {

            }
        });
    }

    public void updatePassword(String password){

        if(noPasswordBox.isChecked()){
            sharedPref.edit().remove(BitcoinApplication.PASSWORD_HASH_PREF).apply();
        }else {
            sharedPref.edit().putString(BitcoinApplication.PASSWORD_HASH_PREF, Util.hashStringSHA256(password)).apply();
        }

        changePasswordButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        changePasswordButton.setText("");

        new Thread(new Runnable() {
            @Override
            public void run() {

                if(kit.wallet().isEncrypted()){
                    decryptWallet(getCurrentPassword());
                }

                if (encryptWalletBox.isChecked() && !noPasswordBox.isChecked()) {
                    encryptWallet(getNewPassword());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        changePasswordButton.setText("Update Password");
                        changePasswordButton.setEnabled(true);
                        Toast.makeText(ChangePasswordActivity.this, "Password updated!", Toast.LENGTH_LONG).show();
                        clearInputs();
                    }
                });
            }
        }).start();
    }

    public boolean isNewPasswordEmpty(){
        return newPasswordLayout.getEditText().getText().toString().isEmpty();
    }

    public String getCurrentPasswordHash(){
        return sharedPref.getString(BitcoinApplication.PASSWORD_HASH_PREF, "");
    }

    public void clearInputs(){
        currentPasswordLayout.getEditText().setText("");
        newPasswordLayout.getEditText().setText("");
        reNewPasswordLayout.getEditText().setText("");
    }

    public void decryptWallet(String password){
        kit.wallet().decrypt(password);
    }

    public void encryptWallet(String password){
        kit.wallet().encrypt(password);
    }

    public void resetErrors(){
        currentPasswordLayout.setErrorEnabled(false);
        newPasswordLayout.setErrorEnabled(false);
        reNewPasswordLayout.setErrorEnabled(false);
    }

    public String getCurrentPassword(){
        return currentPasswordLayout.getEditText().getText().toString();
    }

    public String getNewPassword(){
        return newPasswordLayout.getEditText().getText().toString();
    }

    public String getReNewPassword(){
        return reNewPasswordLayout.getEditText().getText().toString();
    }

    public boolean doPasswordsMatch(){
        return getNewPassword().equals(getReNewPassword());
    }

    public boolean isCurrentPasswordCorrect(String oldPassword){
        if(getCurrentPasswordHash().isEmpty()){
            return true;
        }else{
            return getCurrentPasswordHash().equals(Util.hashStringSHA256(oldPassword));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }
}
