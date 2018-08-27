package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;

import static android.view.View.GONE;
import static info.vericoin.verimobile.VeriTransaction.BTC_TRANSACTION_FEE;

public class ReviewActivity extends WalletAppKitActivity {

    private final static String VERI_TRANSACTION = "veriTransaction";
    private VeriMobileApplication veriMobileApplication;
    private Button sendButton;
    private ProgressBar progressBar;
    private TextView totalView;
    private TextView feeView;
    private TextView amountView;
    private TextView addrView;
    private VeriTransaction veriTransaction;

    public static Intent createIntent(Context context, VeriTransaction veriTransaction) {
        Intent intent = new Intent(context, ReviewActivity.class);
        intent.putExtra(VERI_TRANSACTION, veriTransaction);
        return intent;
    }

    @Override
    protected void onWalletKitStop() {
        sendButton.setOnClickListener(null);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_transaction_review);
        veriMobileApplication = (VeriMobileApplication) getApplication();

        veriTransaction = (VeriTransaction) getIntent().getSerializableExtra(VERI_TRANSACTION);

        totalView = findViewById(R.id.totalAmount);
        feeView = findViewById(R.id.fee);
        amountView = findViewById(R.id.amount);
        addrView = findViewById(R.id.sendAddr);
        progressBar = findViewById(R.id.progressBar);
        sendButton = findViewById(R.id.sendButton);

        progressBar.setVisibility(GONE);

        try {
            Coin total = veriTransaction.getTotal();
            amountView.setText(veriTransaction.getAmount().toFriendlyString());
            feeView.setText(veriTransaction.getFee().toFriendlyString());
            totalView.setText(total.toFriendlyString());
            addrView.setText(veriTransaction.getAddress().toString());
        } catch (Exception e) {
            Toast.makeText(ReviewActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ProcessTransactionActivity.createIntent(ReviewActivity.this, veriTransaction));
            }
        });
    }

}
