package info.vericoin.verimobile;

import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class VeriActivity extends AppCompatActivity {

    private BitcoinApplication bitcoinApplication;

    @Override
    protected void onResume(){
        super.onResume();

        bitcoinApplication = (BitcoinApplication) getApplication();

        if(bitcoinApplication.isSecureWindowEnabled()){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

}
