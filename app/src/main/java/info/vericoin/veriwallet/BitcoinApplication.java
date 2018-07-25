package info.vericoin.veriwallet;

import android.app.Application;

public class BitcoinApplication extends Application {

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        WalletConnection.createConnection(this);
    }

}
