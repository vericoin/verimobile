package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;

public class TransactionDetailActivity extends VeriActivity{

    private final static String TX_EXTRA = "Transaction";

    private WalletAppKit kit;

    private Transaction tx;

    private String txString;

    private RecyclerView inputRecyclerView;
    private InputListAdapter inputAdapter;

    private RecyclerView outputRecyclerView;
    private OutputListAdapter outputAdapter;

    private TextView amountView;
    private TextView txHashView;
    private TextView confirmationView;
    private TextView feeView;
    private TextView dateView;
    private TextView confidenceTypeView;
    private ImageView confidenceImageView;

    private LinearLayoutManager inputLayoutManager;
    private LinearLayoutManager outputLayoutManager;

    public static Intent createIntent(Context context, String txString){
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(TX_EXTRA, txString);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        amountView = findViewById(R.id.amount);
        txHashView = findViewById(R.id.txHash);
        feeView = findViewById(R.id.fee);
        confirmationView = findViewById(R.id.confirmations);
        dateView = findViewById(R.id.date);
        confidenceTypeView = findViewById(R.id.confidenceType);
        confidenceImageView = findViewById(R.id.confidenceImage);

        inputLayoutManager = new LinearLayoutManager(this);
        outputLayoutManager = new LinearLayoutManager(this);
        inputRecyclerView = findViewById(R.id.inputRecyclerView);
        outputRecyclerView = findViewById(R.id.outputRecyclerView);

        inputRecyclerView.setLayoutManager(inputLayoutManager);
        outputRecyclerView.setLayoutManager(outputLayoutManager);

        DividerItemDecoration inputDivider = new DividerItemDecoration(inputRecyclerView.getContext(),
                inputLayoutManager.getOrientation());
        inputRecyclerView.addItemDecoration(inputDivider);

        DividerItemDecoration outputDivider = new DividerItemDecoration(outputRecyclerView.getContext(),
                outputLayoutManager.getOrientation());
        outputRecyclerView.addItemDecoration(outputDivider);

        txString = getIntent().getStringExtra(TX_EXTRA);

    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(final WalletAppKit kit) {
                TransactionDetailActivity.this.kit = kit;

                tx = kit.wallet().getTransaction(Sha256Hash.wrap(txString));

                inputAdapter = new InputListAdapter(TransactionDetailActivity.this, tx.getInputs());
                outputAdapter = new OutputListAdapter(TransactionDetailActivity.this, tx.getOutputs());

                inputRecyclerView.setAdapter(inputAdapter);
                outputRecyclerView.setAdapter(outputAdapter);

                Coin amount = tx.getValue(kit.wallet());
                if(amount.isPositive()){
                    amountView.setTextColor(getResources().getColor(R.color.greenNumber));
                }

                amountView.setText(amount.toFriendlyString());

                txHashView.setText(tx.getHashAsString());

                dateView.setText(Util.getDateTimeString(tx.getUpdateTime()));
                confidenceTypeView.setText(Util.getConfidenceString(tx.getConfidence().getConfidenceType()));
                confidenceImageView.setImageResource(Util.getConfidenceResource(tx.getConfidence().getConfidenceType()));

                Coin fee = tx.getFee();
                if(fee == null){
                    feeView.setText("N/A"); //If transaction wasn't sent by us we don't know fee.
                }else {
                    feeView.setText(fee.toFriendlyString());
                }
                confirmationView.setText(String.valueOf(tx.getConfidence().getDepthInBlocks()));
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
}
