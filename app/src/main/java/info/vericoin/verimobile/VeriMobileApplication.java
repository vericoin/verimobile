package info.vericoin.verimobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import org.bitcoinj.utils.BriefLogFormatter;

import info.vericoin.verimobile.Managers.ContactManager;
import info.vericoin.verimobile.Managers.CustomPeerManager;
import info.vericoin.verimobile.Managers.ExchangeManager;
import info.vericoin.verimobile.Managers.PasswordManager;
import info.vericoin.verimobile.Managers.WalletManager;
import info.vericoin.verimobile.Util.UtilMethods;

public class VeriMobileApplication extends MultiDexApplication {

    private final static String PREFERENCE_FILE_KEY = "info.vericoin.verimobile.PREFERENCE_FILE_KEY";

    private SharedPreferences sharedPref;

    private SharedPreferences defaultPref;

    private CustomPeerManager peerManager;

    private WalletManager walletManager;

    private PasswordManager passwordManager;

    private ContactManager contactManager;

    private ExchangeManager exchangeManager;

    public ContactManager getContactManager() {
        return contactManager;
    }

    public CustomPeerManager getPeerManager() {
        return peerManager;
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }

    public ExchangeManager getExchangeManager() {
        return exchangeManager;
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
        if(contactManager == null){
            contactManager = new ContactManager(sharedPref);
        }
        if(exchangeManager == null){
            exchangeManager = new ExchangeManager(sharedPref);
            exchangeManager.updateExchangeRate(this);
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
