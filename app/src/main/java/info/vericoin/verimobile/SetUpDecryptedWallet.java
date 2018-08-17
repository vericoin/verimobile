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

public class SetUpDecryptedWallet extends SetUpWallet {

    private final static String URI_EXTRA = "uri";

    private Uri uri;

    public static Intent createIntent(Context context, Uri uri){
        return new Intent(context, SetUpDecryptedWallet.class).putExtra(URI_EXTRA, uri);
    }

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        uri = getIntent().getParcelableExtra(URI_EXTRA);

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

                }catch(final Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            importFailed(e.toString());
                        }
                    });
                }
            }
        }).start();
    }

}
