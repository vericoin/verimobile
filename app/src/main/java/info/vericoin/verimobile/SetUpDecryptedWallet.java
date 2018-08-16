package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.bitcoinj.wallet.Wallet;

public class SetUpDecryptedWallet extends VeriActivity {

    private final static String URI_EXTRA = "uri";

    private Uri uri;

    private ProgressBar progressBar;

    private VeriMobileApplication veriMobileApplication;

    private TextInputLayout passwordLayout;

    private TextInputLayout rePasswordLayout;

    private Button importWalletButton;

    private CheckBox encryptWallet;

    private CheckBox noPassword;

    public static Intent createIntent(Context context, Uri uri){
        return new Intent(context, SetUpDecryptedWallet.class).putExtra(URI_EXTRA, uri);
    }

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.activity_set_up_decrypted_wallet);

        veriMobileApplication = (VeriMobileApplication) getApplication();

        uri = getIntent().getParcelableExtra(URI_EXTRA);

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
                if(arePaswordsEqual()) {
                    importWallet(getPassword());
                }else{
                    setRePasswordError("Passwords do not match.");
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

    public void importWallet(final String password){
        importing();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Wallet importWallet = Wallet.loadFromFileStream(getContentResolver().openInputStream(uri));

                    if(!isNoPassword()) {
                        veriMobileApplication.newPassword(password);
                        if(isEncryptWallet()){
                            importWallet.encrypt(password);
                        }
                    }
                    WalletConnection.importWallet(SetUpDecryptedWallet.this, uri);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            importComplete();
                        }
                    });

                }catch(Exception e){
                    e.printStackTrace();
                    importFailed(e.toString());
                }
            }
        }).start();
    }

    public void importComplete(){
        Toast.makeText(this, "Wallet has been imported!", Toast.LENGTH_LONG).show();
        startActivity(SplashActivity.createIntent(SetUpDecryptedWallet.this));
    }

    public boolean isEncryptWallet(){
        return encryptWallet.isChecked();
    }

    public boolean isNoPassword(){
        return noPassword.isChecked();
    }

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
