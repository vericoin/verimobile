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

    private final static String ADDRESS_EXTRA = "address";
    private final static String AMOUNT_EXTRA = "amount";
    private VeriMobileApplication veriMobileApplication;
    private Address address;
    private Coin amount;
    private Button sendButton;
    private ProgressBar progressBar;
    private TextView totalView;
    private TextView feeView;
    private TextView amountView;
    private TextView addrView;

    public static Intent createIntent(Context context, Address toAddr, Coin amount) {
        Intent intent = new Intent(context, ReviewActivity.class);
        intent.putExtra(ADDRESS_EXTRA, toAddr);
        intent.putExtra(AMOUNT_EXTRA, amount);
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

        address = (Address) getIntent().getSerializableExtra(ADDRESS_EXTRA);
        amount = (Coin) getIntent().getSerializableExtra(AMOUNT_EXTRA);

        totalView = findViewById(R.id.totalAmount);
        feeView = findViewById(R.id.fee);
        amountView = findViewById(R.id.amount);
        addrView = findViewById(R.id.sendAddr);
        progressBar = findViewById(R.id.progressBar);
        sendButton = findViewById(R.id.sendButton);

        progressBar.setVisibility(GONE);

        try {
            Coin fee = estimateFee();
            Coin total = amount.add(fee);
            amountView.setText(amount.toFriendlyString());
            feeView.setText(estimateFee().toFriendlyString());
            totalView.setText(total.toFriendlyString());
            addrView.setText(address.toString());
        } catch (Exception e) {
            Toast.makeText(ReviewActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTransaction();
            }
        });
    }

    public Coin estimateFee() {
        return BTC_TRANSACTION_FEE;
    }

    public boolean isWalletEncrypted() {
        return kit.wallet().isEncrypted();
    }

    public boolean isLockTransactions() {
        return veriMobileApplication.isLockTransactions();
    }

    public void sendTransaction() {
        if (isWalletEncrypted()) {
            startActivity(DecryptWalletActivity.createIntent(this, address, amount));
        } else if (isLockTransactions()) {
            startActivity(UnlockActivity.createIntent(this, amount, address));
        } else {
            startActivity(ProcessTransactionActivity.createIntent(this, address, amount));
        }
    }

}
