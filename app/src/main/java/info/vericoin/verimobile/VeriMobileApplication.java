package info.vericoin.verimobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import org.bitcoinj.utils.BriefLogFormatter;

import info.vericoin.verimobile.Util.UtilMethods;

public class VeriMobileApplication extends MultiDexApplication {

    private final static String PREFERENCE_FILE_KEY = "info.vericoin.verimobile.PREFERENCE_FILE_KEY";

    private SharedPreferences sharedPref;

    private SharedPreferences defaultPref;

    private CustomPeerManager peerManager;

    private WalletManager walletManager;

    private PasswordManager passwordManager;

    public CustomPeerManager getPeerManager() {
        return peerManager;
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();

        BriefLogFormatter.init();

        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        defaultPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if(passwordManager == null) {
            passwordManager = new PasswordManager(sharedPref);
        }
        if(walletManager == null) {
            walletManager = new WalletManager(this);
        }
        if(peerManager == null) {
            peerManager = new CustomPeerManager(sharedPref, walletManager);
        }

        UtilMethods.setContext(this);

        if (walletManager.doesWalletExist(this)) {
            walletManager.startWallet(this);
        }
    }

    public boolean isLockTransactions() {
        return defaultPref.getBoolean(getString(R.string.lock_transactions_key), false);
    }

    public boolean isFingerPrintEnabled() {
        return defaultPref.getBoolean(getString(R.string.fingerprint_enabled_key), true);
    }

    public boolean isSecureWindowEnabled() {
        return defaultPref.getBoolean(getString(R.string.secure_window_key), true);
    }

}
