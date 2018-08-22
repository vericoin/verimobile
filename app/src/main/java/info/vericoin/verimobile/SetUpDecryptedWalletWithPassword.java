package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.bitcoinj.wallet.Wallet;

public class SetUpDecryptedWalletWithPassword extends SetUpWalletWithPassword {

    private final static String URI_EXTRA = "uri";

    private Uri uri;

    public static Intent createIntent(Context context, Uri uri) {
        return new Intent(context, SetUpDecryptedWalletWithPassword.class).putExtra(URI_EXTRA, uri);
    }

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        uri = getIntent().getParcelableExtra(URI_EXTRA);

    }

    public void importWallet(final String password) {
        importing();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Wallet importWallet = Wallet.loadFromFileStream(getContentResolver().openInputStream(uri));

                    if (!isNoPasswordChecked()) {
                        veriMobileApplication.newPassword(password);
                        if (isEncryptWallet()) {
                            importWallet.encrypt(password);
                        }
                    }
                    WalletSingleton.importWallet(SetUpDecryptedWalletWithPassword.this, uri);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            importComplete();
                        }
                    });

                } catch (final Exception e) {
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
