package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import javax.annotation.Nullable;

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

    public void importWallet(@Nullable final String password) {
        importing();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (password == null) {
                        walletManager.createWalletFromFile(SetUpDecryptedWalletWithPassword.this, uri);
                    } else {
                        walletManager.createWalletFromFile(SetUpDecryptedWalletWithPassword.this, uri, password, isEncryptWallet());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            importComplete();
                        }
                    });
                } catch (final Exception e) {
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
