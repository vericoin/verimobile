package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;

import static android.view.View.GONE;
import static info.vericoin.verimobile.VeriTransaction.BTC_TRANSACTION_FEE;

public class ReviewActivity extends AppCompatActivity {

    private final static String ADDRESS_EXTRA = "address";
    private final static String AMOUNT_EXTRA = "amount";

    public static Intent createIntent(Context context, Address toAddr, Coin amount){
        Intent intent = new Intent(context, ReviewActivity.class);
        intent.putExtra(ADDRESS_EXTRA, toAddr);
        intent.putExtra(AMOUNT_EXTRA, amount);
        return intent;
    }

    private Address address;
    private Coin amount;
    private WalletAppKit kit;

    private Button sendButton;
    private ProgressBar progressBar;

    private TextView totalView;
    private TextView feeView;
    private TextView amountView;
    private TextView addrView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_review);

        address = (Address) getIntent().getSerializableExtra(ADDRESS_EXTRA);
        amount = (Coin) getIntent().getSerializableExtra(AMOUNT_EXTRA);

        totalView = findViewById(R.id.totalAmount);
        feeView = findViewById(R.id.fee);
        amountView = findViewById(R.id.amount);
        addrView = findViewById(R.id.sendAddr);
        progressBar = findViewById(R.id.progressBar);
        sendButton = findViewById(R.id.sendButton);

        progressBar.setVisibility(GONE);
    }

    public Coin estimateFee() throws Exception{
        SendRequest request = SendRequest.to(address, amount);
        request.feeNeeded = BTC_TRANSACTION_FEE;
        kit.wallet().completeTx(request);
        return request.tx.getFee();
    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(final WalletAppKit kit) {
                ReviewActivity.this.kit = kit;
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
                        progressBar.setVisibility(View.VISIBLE);
                        sendButton.setText("");

                        sendTransaction();
                    }
                });
            }

            @Override
            public void OnSyncComplete() {

            }
        });

    }

    public void sendTransaction(){
        VeriTransaction veriTransaction = new VeriTransaction();
        veriTransaction.setBroadcastListener(new VeriTransaction.OnBroadcastListener() {
            @Override
            public void broadcastComplete(org.bitcoinj.core.Transaction tx) {
                progressBar.setVisibility(GONE);
                startActivity(TransactionCompleteActivity.createIntent(ReviewActivity.this, tx.getHashAsString()));
                finish();
            }
        });
        veriTransaction.sendTransaction(amount, address);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }

}
