package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SetUpSeedWallet extends SetUpWallet {

    private final static String MNEMONIC_LIST_EXTRA = "mnemonicList";
    private final static String CREATION_TIME_EXTRA = "creationTime";

    private List<String> mnemonicList;
    private long creationTime;

    public static Intent createIntent(Context context, ArrayList<String> mnemonicList, long creationTime){
        return new Intent(context, SetUpSeedWallet.class).putExtra(MNEMONIC_LIST_EXTRA, mnemonicList).putExtra(CREATION_TIME_EXTRA, creationTime);
    }

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        mnemonicList = getIntent().getStringArrayListExtra(MNEMONIC_LIST_EXTRA);
        creationTime = getIntent().getLongExtra(CREATION_TIME_EXTRA, 0);
    }

    @Override
    void importWallet(String password) {
        if(!isNoPassword()){
            veriMobileApplication.newPassword(password);
        }
        WalletConnection.importFromSeed(SetUpSeedWallet.this, password, mnemonicList, creationTime);
        Toast.makeText(SetUpSeedWallet.this, "Wallet imported!", Toast.LENGTH_LONG).show();
        startActivity(SplashActivity.createIntent(SetUpSeedWallet.this));
    }

}
