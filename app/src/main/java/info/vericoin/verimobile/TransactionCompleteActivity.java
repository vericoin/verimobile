package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.GONE;
import static info.vericoin.verimobile.VeriTransaction.BTC_TRANSACTION_FEE;

public class TransactionCompleteActivity extends AppCompatActivity {

    private final static String ADDRESS_EXTRA = "address";
    private final static String AMOUNT_EXTRA = "amount";

    private TextView txHashView;

    private ConstraintLayout txHashBox;

    private TextView statusView;

    private ImageView completeImage;

    private Button doneButton;

    private Address address;
    private Coin amount;
    private WalletAppKit kit;
    private ProgressBar progressBar;

    public static Intent createIntent(Context context, Address toAddr, Coin amount){
        Intent intent = new Intent(context, TransactionCompleteActivity.class);
        intent.putExtra(ADDRESS_EXTRA, toAddr);
        intent.putExtra(AMOUNT_EXTRA, amount);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_complete);

        address = (Address) getIntent().getSerializableExtra(ADDRESS_EXTRA);
        amount = (Coin) getIntent().getSerializableExtra(AMOUNT_EXTRA);

        txHashView = findViewById(R.id.txHash);
        txHashBox = findViewById(R.id.txHashBox);
        statusView = findViewById(R.id.statusView);
        completeImage = findViewById(R.id.completeImage);
        progressBar = findViewById(R.id.progressBar);

        doneButton = findViewById(R.id.doneButton);

        doneButton.setVisibility(GONE);
        completeImage.setVisibility(GONE);
        txHashBox.setVisibility(GONE);

        statusView.setText("Broadcasting Transaction...");

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.createIntent(TransactionCompleteActivity.this));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(final WalletAppKit kit) {
                TransactionCompleteActivity.this.kit = kit;
                sendTransaction();
            }

            @Override
            public void OnSyncComplete() {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }

    public void sendTransaction(){
        try {
            SendRequest request = SendRequest.to(address, amount);
            request.feeNeeded = BTC_TRANSACTION_FEE;

            final Wallet.SendResult sendResult = kit.wallet().sendCoins(request);

            // Register a callback that is invoked when the transaction has propagated across the network.
            // This shows a second style of registering ListenableFuture callbacks, it works when you don't
            // need access to the object the future returns.
            sendResult.broadcastComplete.addListener(new Runnable() {
                @Override
                public void run() {
                    broadcastComplete(sendResult.tx.getHashAsString());
                }
            }, WalletConnection.getRunInUIThread());
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            broadcastFailed(e.getMessage());
        }
    }

    public void broadcastComplete(String txHash){
        txHashView.setText(txHash);
        progressBar.setVisibility(GONE);
        doneButton.setVisibility(View.VISIBLE);
        completeImage.setVisibility(View.VISIBLE);
        txHashBox.setVisibility(View.VISIBLE);
        statusView.setText("Broadcast Complete!");
    }

    public void broadcastFailed(String message){
        Toast.makeText(TransactionCompleteActivity.this, message, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(GONE);
        doneButton.setVisibility(View.VISIBLE);
        statusView.setText("Broadcast Failed");
    }

}
