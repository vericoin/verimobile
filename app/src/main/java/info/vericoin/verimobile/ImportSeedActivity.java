package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ImportSeedActivity extends VeriActivity {

    public static Intent createIntent(Context context){
        return new Intent(context, ImportSeedActivity.class);
    }

    private TextInputLayout seedLayout;

    private TextInputLayout creationTimeLayout;

    private Button importButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_seed);

        seedLayout = findViewById(R.id.seedLayout);
        creationTimeLayout = findViewById(R.id.creationTimeLayout);
        importButton = findViewById(R.id.importButton);

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WalletConnection.importFromSeed(ImportSeedActivity.this, "", Util.stringToMnemonic(getSeed()), getCreationTime());
                Toast.makeText(ImportSeedActivity.this, "Wallet imported!", Toast.LENGTH_LONG).show();
                startActivity(SplashActivity.createIntent(ImportSeedActivity.this));
            }
        });

    }

    public String getSeed(){
        return seedLayout.getEditText().getText().toString();
    }

    public long getCreationTime(){
        return Long.parseLong(creationTimeLayout.getEditText().getText().toString());
    }
}
