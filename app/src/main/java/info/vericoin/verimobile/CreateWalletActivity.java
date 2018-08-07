package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.bitcoinj.kits.WalletAppKit;

public class CreateWalletActivity extends AppCompatActivity {

    private Button createWalletButton;

    private ProgressBar progressBar;

    public static Intent createIntent(Context context){
        return new Intent(context, CreateWalletActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        createWalletButton = findViewById(R.id.createWalletButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWalletButton.setEnabled(false);
                createWalletButton.setText("");
                progressBar.setVisibility(View.VISIBLE);

                WalletConnection.startAsync(CreateWalletActivity.this);
                WalletConnection.connect(new WalletConnection.OnConnectListener() {

                    @Override
                    public void OnSetUpComplete(WalletAppKit kit) {
                        startActivity(MainActivity.createIntent(CreateWalletActivity.this));
                        ActivityCompat.finishAffinity(CreateWalletActivity.this); //Prevent app from going back to previous activities.
                    }

                    @Override
                    public void OnSyncComplete() {

                    }
                });
            }
        });

    }

}
