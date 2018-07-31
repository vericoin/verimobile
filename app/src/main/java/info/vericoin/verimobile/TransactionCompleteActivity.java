package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.bitcoinj.core.Transaction;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class TransactionCompleteActivity extends AppCompatActivity {

    private static final String TRANSACTION_EXTRA = "transaction";

    private TextView txHashView;

    private String txHash;

    private Button doneButton;

    public static Intent createIntent(Context context, String txHash){
        Intent intent = new Intent(context, TransactionCompleteActivity.class);
        intent.putExtra(TRANSACTION_EXTRA, txHash);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_complete);

        txHashView = findViewById(R.id.txHash);

        txHash = getIntent().getStringExtra(TRANSACTION_EXTRA);

        txHashView.setText(txHash);

        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.createIntent(TransactionCompleteActivity.this));
                finish();
            }
        });
    }
}
