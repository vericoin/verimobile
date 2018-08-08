package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import org.bitcoinj.kits.WalletAppKit;

import static info.vericoin.verimobile.BitcoinApplication.PASSWORD_HASH_PREF;
import static info.vericoin.verimobile.BitcoinApplication.PREFERENCE_FILE_KEY;

public class CreateWalletActivity extends AppCompatActivity {

    private Button createWalletButton;

    private ProgressBar progressBar;

    private TextInputLayout passwordLayout;

    private TextInputLayout rePasswordLayout;

    private CheckBox encryptWallet;

    private SharedPreferences sharedPref;

    public static Intent createIntent(Context context){
        return new Intent(context, CreateWalletActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        createWalletButton = findViewById(R.id.createWalletButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        passwordLayout = findViewById(R.id.passwordInputLayout);
        rePasswordLayout = findViewById(R.id.rePasswordInputLayout);
        encryptWallet = findViewById(R.id.encryptWallet);

        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!arePasswordsEqual()) {
                    rePasswordLayout.setError("Passwords do not match");
                }else if(passwordsAreEmpty()) {
                    passwordLayout.setError("Password can not be empty");
                }else{
                    passwordLayout.setErrorEnabled(false);
                    rePasswordLayout.setErrorEnabled(false);

                    createWalletButton.setEnabled(false);
                    createWalletButton.setText("");
                    progressBar.setVisibility(View.VISIBLE);

                    savePasswordHash(getPassword());

                    String password = "";
                    if(shouldEncryptWallet()){
                        password = getPassword();
                    }

                    WalletConnection.startAsync(CreateWalletActivity.this, password);
                    WalletConnection.connect(new WalletConnection.OnConnectListener() {

                        @Override
                        public void OnSetUpComplete(WalletAppKit kit) {
                            startActivity(MainActivity.createIntent(CreateWalletActivity.this));
                            ActivityCompat.finishAffinity(CreateWalletActivity.this); //Prevent app from going back to previous activities.
                        }

                        @Override
                        public void OnSyncComplete() {

                        }
                    });
                }
            }
        });
    }

    public void savePasswordHash(String password){
        sharedPref.edit().putString(PASSWORD_HASH_PREF, Util.hashStringSHA256(password)).apply();
    }

    public String getPassword(){
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean shouldEncryptWallet(){
        return encryptWallet.isChecked();
    }

    public boolean passwordsAreEmpty(){
        String password = passwordLayout.getEditText().getText().toString();
        String rePassword = rePasswordLayout.getEditText().getText().toString();
        return (password.isEmpty() || rePassword.isEmpty());
    }

    public boolean arePasswordsEqual(){
        String password = passwordLayout.getEditText().getText().toString();
        String rePassword = rePasswordLayout.getEditText().getText().toString();
        return(password.equals(rePassword));
    }

}
