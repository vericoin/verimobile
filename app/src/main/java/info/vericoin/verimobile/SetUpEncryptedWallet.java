package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.bitcoinj.wallet.Wallet;

import java.io.FileNotFoundException;

public class SetUpEncryptedWallet extends VeriActivity {

    //Send URI to this activity.
    //Create Wallet object using URI.
    //Check if Wallet is encrypted.
    //If encrypted ask user for current password and check that its valid.
    //If not encrypted give user option to enter in password for wallet and if they want it encrypted, and a no password checkbox.
    //Write wallet to directory so bitcoinJ can access it.
    //Start Splash Activity.

    private final static String URI_EXTRA = "uri";

    private ProgressBar progressBar;

    private TextInputLayout passwordLayout;

    private Button setPasswordButton;

    private Uri uri;

    private VeriMobileApplication veriMobileApplication;

    public static Intent createIntent(Context context, Uri uri){
        return new Intent(context, SetUpEncryptedWallet.class).putExtra(URI_EXTRA, uri);
    }

    @Override
    protected void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.activity_set_up_encrypted_wallet);

        veriMobileApplication = (VeriMobileApplication) getApplication();

        uri = getIntent().getParcelableExtra(URI_EXTRA);

        try {
            final Wallet importWallet = Wallet.loadFromFileStream(getContentResolver().openInputStream(uri));

            passwordLayout = findViewById(R.id.walletPasswordInputLayout);
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            setPasswordButton = findViewById(R.id.setPasswordButton);

            setPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetError();
                    checkPassword(importWallet, getPassword());
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void checkPassword(final Wallet wallet, final String password){
        progressBar.setVisibility(View.VISIBLE);
        setPasswordButton.setText("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isPasswordCorrect = wallet.checkPassword(password);
                try {
                    if (isPasswordCorrect) {
                        WalletConnection.importWallet(SetUpEncryptedWallet.this, uri);
                        veriMobileApplication.newPassword(password);
                        passwordSetUpSuccess();
                    } else {
                        setError("Password is incorrect");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    setError(e.toString());
                }
            }
        }).start();
    }

    public void resetError(){
        passwordLayout.setErrorEnabled(false);
    }

    public void passwordSetUpSuccess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                setPasswordButton.setText("Set Password");
                Toast.makeText(SetUpEncryptedWallet.this, "Password success!", Toast.LENGTH_LONG).show();
                startActivity(SplashActivity.createIntent(SetUpEncryptedWallet.this));
            }
        });
    }

    public void setError(final String error){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                passwordLayout.setError(error);
                setPasswordButton.setText("Set Password");
            }
        });
    }

    public String getPassword(){
        return passwordLayout.getEditText().getText().toString();
    }

}
