package info.vericoin.veriwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.kits.WalletAppKit;

public class SendActivity extends AppCompatActivity {

    public static Intent createIntent(Context context){
        return new Intent(context, SendActivity.class);
    }

    private EditText sendAddr;
    private EditText sendAmount;
    private Button sendButton;
    private TextView txHash;
    private TextView receiveAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        sendAddr = findViewById(R.id.sendAddr);
        sendAmount = findViewById(R.id.sendAmount);
        sendButton = findViewById(R.id.sendButton);
        txHash = findViewById(R.id.txHash);
        receiveAddr = findViewById(R.id.receiveAddr);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {

            @Override
            public void OnSetUpComplete(WalletAppKit kit) {
                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String amount = sendAmount.getText().toString();
                        String addr = sendAddr.getText().toString();
                        sendTransaction(amount, addr);
                    }
                });
                receiveAddr.setText(kit.wallet().currentReceiveAddress().toString());
            }
        });
    }

    public void sendTransaction(String amount, String addr){
        Transaction transaction = new Transaction();
        transaction.setBroadcastListener(new Transaction.OnBroadcastListener() {
            @Override
            public void broadcastComplete(org.bitcoinj.core.Transaction tx) {
                Toast.makeText(SendActivity.this, "Transaction Sent!", Toast.LENGTH_LONG).show();
                txHash.setText(tx.getHashAsString());
            }
        });
        transaction.sendTransaction(amount, addr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }
}
