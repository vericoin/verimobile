package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

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
                seedLayout.setErrorEnabled(false);
                creationTimeLayout.setErrorEnabled(false);
                try {
                    long creationTime = getCreationTime();
                    try{
                        ArrayList<String> mnemonicList = new ArrayList<>(Util.stringToMnemonic(getSeed()));
                        startActivity(SetUpSeedWallet.createIntent(ImportSeedActivity.this, mnemonicList, creationTime));
                    }catch(Exception e){
                        e.printStackTrace();
                        seedLayout.setError("Invalid input");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    creationTimeLayout.setError("Invalid input");
                }
            }
        });
    }

    public String getSeed(){
        return seedLayout.getEditText().getText().toString();
    }

    public long getCreationTime(){
        String creationString = creationTimeLayout.getEditText().getText().toString();
        if(creationString.isEmpty()){
            return 0;
        }else {
            return Long.parseLong(creationTimeLayout.getEditText().getText().toString());
        }
    }
}
