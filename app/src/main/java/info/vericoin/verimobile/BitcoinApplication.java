package info.vericoin.verimobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.view.WindowManager;

public class BitcoinApplication extends MultiDexApplication {

    private final static String PASSWORD_HASH_PREF = "passwordHash";

    private final static String PREFERENCE_FILE_KEY = "info.vericoin.verimobile.PREFERENCE_FILE_KEY";

    private SharedPreferences sharedPref;

    private SharedPreferences defaultPref;

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        defaultPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    }

    public boolean checkPassword(String password){
        String passwordHash = sharedPref.getString(PASSWORD_HASH_PREF,"");
        if(passwordHash.isEmpty()){
            return true; //There is no password
        }else {
            return passwordHash.equals(Util.hashStringSHA256(password));
        }
    }

    public boolean isLockTransactions() {
        return defaultPref.getBoolean(getString(R.string.lock_transactions_key), false);
    }

    public boolean doesPasswordExist(){
        String password = sharedPref.getString(BitcoinApplication.PASSWORD_HASH_PREF, "");
        if(password.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public void removePassword(){
        sharedPref.edit().remove(BitcoinApplication.PASSWORD_HASH_PREF).apply();
    }

    public void newPassword(String newPassword){
        sharedPref.edit().putString(BitcoinApplication.PASSWORD_HASH_PREF, Util.hashStringSHA256(newPassword)).apply();
    }

    public String getPasswordHash(){
        return sharedPref.getString(BitcoinApplication.PASSWORD_HASH_PREF, "");
    }

    public boolean isFingerPrintEnabled(){
        return defaultPref.getBoolean(getString(R.string.fingerprint_enabled_key), true);
    }

}
