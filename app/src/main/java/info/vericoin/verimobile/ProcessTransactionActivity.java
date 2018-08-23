package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.GONE;
import static info.vericoin.verimobile.VeriTransaction.BTC_TRANSACTION_FEE;

public class ProcessTransactionActivity extends WalletAppKitActivity {

    private final static String ADDRESS_EXTRA = "address";
    private final static String AMOUNT_EXTRA = "amount";
    private final static String PASSWORD_EXTRA = "password";

    private TextView txHashView;

    private ConstraintLayout txHashBox;

    private TextView statusView;

    private ImageView completeImage;

    private Button doneButton;

    private Address address;
    private Coin amount;
    private ProgressBar progressBar;

    private String password;

    public static Intent createIntent(Context context, Address toAddr, Coin amount) {
        return createIntent(context, toAddr, amount, "");
    }

    public static Intent createIntent(Context context, Address toAddr, Coin amount, String password) {
        Intent intent = new Intent(context, ProcessTransactionActivity.class);
        intent.putExtra(ADDRESS_EXTRA, toAddr);
        intent.putExtra(AMOUNT_EXTRA, amount);
        intent.putExtra(PASSWORD_EXTRA, password);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onWalletKitStop() {

    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_process_transaction);

        address = (Address) getIntent().getSerializableExtra(ADDRESS_EXTRA);
        amount = (Coin) getIntent().getSerializableExtra(AMOUNT_EXTRA);
        password = getIntent().getStringExtra(PASSWORD_EXTRA);

        txHashView = findViewById(R.id.txHash);
        txHashBox = findViewById(R.id.txHashBox);
        statusView = findViewById(R.id.statusView);
        completeImage = findViewById(R.id.completeImage);
        progressBar = findViewById(R.id.progressBar);

        doneButton = findViewById(R.id.doneButton);

        doneButton.setVisibility(GONE);
        completeImage.setVisibility(GONE);
        txHashBox.setVisibility(GONE);

        statusView.setText(getString(R.string.creating_transaction));

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.createIntent(ProcessTransactionActivity.this));
                finish();
            }
        });

        sendTransaction();
    }

    public void sendTransaction() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    SendRequest request = SendRequest.to(address, amount);
                    request.feeNeeded = BTC_TRANSACTION_FEE;

                    if (!password.isEmpty()) { //If password is required to decrypt wallet add it to request.
                        request.aesKey = kit.wallet().getKeyCrypter().deriveKey(password);
                    }

                    final Wallet.SendResult sendResult = kit.wallet().sendCoins(request);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            broadcastWaiting(); //Transaction sent. Wait for broadcast to complete.
                        }
                    });

                    // Register a callback that is invoked when the transaction has propagated across the network.
                    // This shows a second style of registering ListenableFuture callbacks, it works when you don't
                    // need access to the object the future returns.
                    sendResult.broadcastComplete.addListener(new Runnable() {
                        @Override
                        public void run() {
                            broadcastComplete(sendResult.tx.getHashAsString()); //Broadcast complete show user TX hash.
                        }
                    }, WalletSingleton.getRunInUIThread());
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            broadcastFailed(e.toString());
                        }
                    });
                }
            }
        }).start();
    }

    public void broadcastWaiting() {
        statusView.setText(getString(R.string.broadcasting_transaction));
    }

    public void broadcastComplete(String txHash) {
        txHashView.setText(txHash);
        progressBar.setVisibility(GONE);
        doneButton.setVisibility(View.VISIBLE);
        completeImage.setVisibility(View.VISIBLE);
        txHashBox.setVisibility(View.VISIBLE);
        statusView.setText(getString(R.string.broadcast_complete));
    }

    public void broadcastFailed(String message) {
        Toast.makeText(ProcessTransactionActivity.this, message, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(GONE);
        doneButton.setVisibility(View.VISIBLE);
        statusView.setText(getString(R.string.broadcast_failed));
    }

    @Override
    public void onBackPressed() {
        //Do nothing. (We don't want user to go back while a transaction is being processed.)
    }

}
