package info.vericoin.verimobile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

public class ViewSeedActivity extends VeriActivity{

    private final static String PASSWORD_EXTRA = "password";

    private String password;

    private TextView seedText;

    private TextView timeCreated;

    private WalletAppKit kit;

    public static Intent createIntent(Context context, String password){
        return new Intent(context, ViewSeedActivity.class).putExtra(PASSWORD_EXTRA, password);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_seed);

        seedText = findViewById(R.id.seedTextView);
        timeCreated = findViewById(R.id.timeCreated);

        password = getIntent().getStringExtra(PASSWORD_EXTRA);

        kit = WalletConnection.getKit();

        Wallet wallet = kit.wallet();

        DeterministicKeyChain deterministicKeyChain = wallet.getActiveKeyChain();

        if(wallet.isEncrypted()){
            deterministicKeyChain = deterministicKeyChain.toDecrypted(password);
        }

        setSeedText(Util.mnemonicToString(deterministicKeyChain.getMnemonicCode()));
        setTimeCreated(deterministicKeyChain.getEarliestKeyCreationTime());

        seedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Seed", getSeedText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ViewSeedActivity.this, "Seed copied to clipboard!", Toast.LENGTH_LONG).show();
            }
        });

        timeCreated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Time Created", getCreatedTime());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ViewSeedActivity.this, "Time created copied to clipboard!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getSeedText(){
        return seedText.getText().toString();
    }

    public String getCreatedTime(){
        return timeCreated.getText().toString();
    }

    public void setSeedText(String seed){
        seedText.setText(seed);
    }

    public void setTimeCreated(long time){
        timeCreated.setText(Long.toString(time));
    }

}
