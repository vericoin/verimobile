package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;

public class DeleteWalletActivity extends WalletAppKitActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, DeleteWalletActivity.class);
    }

    @Override
    protected void onWalletKitStop() {

    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_delete_wallet);
        WalletSingleton.deleteWallet(this);
    }

    @Override
    public void onBackPressed() {
        //Do nothing. (We don't want user to go back while a transaction is being processed.)
    }

}
