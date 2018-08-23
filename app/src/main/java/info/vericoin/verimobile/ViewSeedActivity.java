package info.vericoin.verimobile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.Wallet;

import info.vericoin.verimobile.Util.UtilMethods;

public class ViewSeedActivity extends WalletAppKitActivity {

    private final static String PASSWORD_EXTRA = "password";

    private final static String TIME_CREATED_LABEL = "timeCreated";

    private final static String SEED_LABEL = "seed";

    private String password;

    private TextView seedText;

    private TextView timeCreated;

    private ConstraintLayout decryptingLayout;
    private ConstraintLayout seedLayout;

    private DeterministicKeyChain deterministicKeyChain;

    public static Intent createIntent(Context context, String password) {
        return new Intent(context, ViewSeedActivity.class).putExtra(PASSWORD_EXTRA, password);
    }

    @Override
    protected void onWalletKitStop() {

    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_view_seed);

        seedText = findViewById(R.id.seedTextView);
        timeCreated = findViewById(R.id.timeCreated);
        decryptingLayout = findViewById(R.id.decryptingLayout);
        seedLayout = findViewById(R.id.seedViewLayout);

        password = getIntent().getStringExtra(PASSWORD_EXTRA);

        final Wallet wallet = kit.wallet();

        deterministicKeyChain = wallet.getActiveKeyChain();
        if (wallet.isEncrypted()) {
            decryptingLayout.setVisibility(View.VISIBLE);
            seedLayout.setVisibility(View.GONE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    deterministicKeyChain = deterministicKeyChain.toDecrypted(password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displaySeed();
                            seedLayout.setVisibility(View.VISIBLE);
                            decryptingLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        }else{
            displaySeed();
            decryptingLayout.setVisibility(View.GONE);
        }

        seedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(SEED_LABEL, getSeedText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ViewSeedActivity.this, R.string.seed_copied, Toast.LENGTH_LONG).show();
            }
        });

        timeCreated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(TIME_CREATED_LABEL, getCreatedTime());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ViewSeedActivity.this, R.string.time_created_copied, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void displaySeed(){
        setSeedText(UtilMethods.mnemonicToString(deterministicKeyChain.getMnemonicCode()));
        setTimeCreated(deterministicKeyChain.getEarliestKeyCreationTime());
    }

    public String getSeedText() {
        return seedText.getText().toString();
    }

    public void setSeedText(String seed) {
        seedText.setText(seed);
    }

    public String getCreatedTime() {
        return timeCreated.getText().toString();
    }

    public void setTimeCreated(long time) {
        timeCreated.setText(Long.toString(time));
    }

}
