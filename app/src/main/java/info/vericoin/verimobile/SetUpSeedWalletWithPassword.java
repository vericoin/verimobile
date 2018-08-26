package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class SetUpSeedWalletWithPassword extends SetUpWalletWithPassword {

    private final static String MNEMONIC_LIST_EXTRA = "mnemonicList";
    private final static String CREATION_TIME_EXTRA = "creationTime";

    private List<String> mnemonicList;
    private long creationTime;

    public static Intent createIntent(Context context, ArrayList<String> mnemonicList, long creationTime) {
        return new Intent(context, SetUpSeedWalletWithPassword.class).putExtra(MNEMONIC_LIST_EXTRA, mnemonicList).putExtra(CREATION_TIME_EXTRA, creationTime);
    }

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        mnemonicList = getIntent().getStringArrayListExtra(MNEMONIC_LIST_EXTRA);
        creationTime = getIntent().getLongExtra(CREATION_TIME_EXTRA, 0);
    }

    @Override
    void importWallet(final String password) {
        importing();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (password == null) {
                        walletManager.createWalletFromSeed(SetUpSeedWalletWithPassword.this, mnemonicList, creationTime);
                    } else {
                        walletManager.createWalletFromSeed(SetUpSeedWalletWithPassword.this, mnemonicList, creationTime, isEncryptWallet(), password);
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
