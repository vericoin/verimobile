package info.vericoin.verimobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

public class BitcoinApplication extends MultiDexApplication {

    public final static String PASSWORD_HASH_PREF = "passwordHash";

    public final static String PREFERENCE_FILE_KEY = "info.vericoin.verimobile.PREFERENCE_FILE_KEY";

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();

    }

}
