package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.kits.WalletAppKit;

public class RecipientActivity extends VeriActivity {

    private TextInputLayout sendAddr;

    private Button nextButton;

    private WalletAppKit kit;

    private ConstraintLayout scanButton;

    public static Intent createIntent(Context context){
        return new Intent(context, RecipientActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);

        kit = WalletConnection.getKit();

        sendAddr = findViewById(R.id.sendAddr);

        sendAddr.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendAddr.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(RecipientActivity.this).initiateScan();
            }
        });

        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressString = sendAddr.getEditText().getText().toString();
                try {
                    Address address = Address.fromString(kit.params(), addressString);
                    startActivity(AmountActivity.createIntent(RecipientActivity.this, address));
                } catch (AddressFormatException e) {
                    e.printStackTrace();
                    sendAddr.setError("Invalid address");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                sendAddr.getEditText().setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
