package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class BackUpWalletInfoActivity extends BackUpWalletActivity {

    private Button backUpWalletButton;

    private Button doItLaterButton;

    private CheckBox iReadCheckBox;

    public static Intent createIntent(Context context) {
        return new Intent(context, BackUpWalletInfoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_export_wallet);

        iReadCheckBox = findViewById(R.id.iRead);
        iReadCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if(check){
                    enableButtons();
                }else{
                    disableButtons();
                }
            }
        });

        backUpWalletButton = findViewById(R.id.backUpWalletButton);
        backUpWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backUpWallet();
            }
        });

        doItLaterButton = findViewById(R.id.doItLaterButton);
        doItLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startApp();
            }
        });

        disableButtons();

    }

    private void startApp(){
        startActivity(MainActivity.createIntent(BackUpWalletInfoActivity.this));
        finish();
    }

    private void enableButtons(){
        doItLaterButton.setEnabled(true);
        backUpWalletButton.setEnabled(true);
    }

    private void disableButtons(){
        doItLaterButton.setEnabled(false);
        backUpWalletButton.setEnabled(false);
    }

    @Override
    protected void walletBackedUp(){
        super.walletBackedUp();
        startApp();
    }
}
