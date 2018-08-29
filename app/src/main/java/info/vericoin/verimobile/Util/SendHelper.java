package info.vericoin.verimobile.Util;

import android.app.Activity;

import org.bitcoinj.kits.WalletAppKit;

import info.vericoin.verimobile.AmountActivity;
import info.vericoin.verimobile.DecryptWalletActivity;
import info.vericoin.verimobile.Models.VeriTransaction;
import info.vericoin.verimobile.UnlockActivity;
import info.vericoin.verimobile.VeriMobileApplication;

public class SendHelper {

    private WalletAppKit kit;
    private Activity activity;
    private VeriMobileApplication veriMobileApplication;
    private VeriTransaction veriTransaction;

    public SendHelper(WalletAppKit kit, Activity activity, VeriTransaction veriTransaction) {
        this.kit = kit;
        this.activity = activity;
        this.veriMobileApplication = ((VeriMobileApplication) activity.getApplication());
        this.veriTransaction = veriTransaction;
    }

    public void startNextActivity(){
        if(isWalletEncrypted()){
            activity.startActivity(DecryptWalletActivity.createIntent(activity, veriTransaction));
        }else if(isLockTransactions() && passwordExist()){
            activity.startActivity(UnlockActivity.createIntent(activity, veriTransaction));
        }else {
            activity.startActivity(AmountActivity.createIntent(activity, veriTransaction));
        }
    }

    private boolean isWalletEncrypted() {
        return kit.wallet().isEncrypted();
    }

    private boolean isLockTransactions() {
        return veriMobileApplication.isLockTransactions();
    }

    private boolean passwordExist(){ return veriMobileApplication.getPasswordManager().doesPasswordExist(); }
}
