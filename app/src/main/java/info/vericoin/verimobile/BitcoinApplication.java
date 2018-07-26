package info.vericoin.verimobile;

import android.support.multidex.MultiDexApplication;

public class BitcoinApplication extends MultiDexApplication {

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        WalletConnection.createConnection(this);
    }

}
