package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;

import info.vericoin.verimobile.Adapters.InputListAdapter;
import info.vericoin.verimobile.Adapters.OutputListAdapter;
import info.vericoin.verimobile.Util.UtilMethods;

public class TransactionDetailActivity extends WalletAppKitActivity {

    private final static String TX_EXTRA = "Transaction";

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

    public static Intent createIntent(Context context, String txString) {
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(TX_EXTRA, txString);
        return intent;
    }

    @Override
    protected void onWalletKitStop() {

    }

    @Override
    protected void onWalletKitReady() {
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
        inputRecyclerView.setNestedScrollingEnabled(false);
        outputRecyclerView.setNestedScrollingEnabled(false);

        inputRecyclerView.setLayoutManager(inputLayoutManager);
        outputRecyclerView.setLayoutManager(outputLayoutManager);

        DividerItemDecoration inputDivider = new DividerItemDecoration(inputRecyclerView.getContext(),
                inputLayoutManager.getOrientation());
        inputRecyclerView.addItemDecoration(inputDivider);

        DividerItemDecoration outputDivider = new DividerItemDecoration(outputRecyclerView.getContext(),
                outputLayoutManager.getOrientation());
        outputRecyclerView.addItemDecoration(outputDivider);

        txString = getIntent().getStringExtra(TX_EXTRA);

        setUpTransactionDetails();
    }

    public void setUpTransactionDetails() {
        tx = kit.wallet().getTransaction(Sha256Hash.wrap(txString));

        inputAdapter = new InputListAdapter(kit, tx.getInputs());
        outputAdapter = new OutputListAdapter(kit, tx.getOutputs());

        inputRecyclerView.setAdapter(inputAdapter);
        outputRecyclerView.setAdapter(outputAdapter);

        Coin amount = tx.getValue(kit.wallet());
        if (amount.isPositive()) {
            amountView.setTextColor(getResources().getColor(R.color.greenNumber));
        }

        amountView.setText(amount.toFriendlyString());

        txHashView.setText(tx.getHashAsString());

        dateView.setText(UtilMethods.getDateTimeString(tx.getUpdateTime()));
        confidenceTypeView.setText(UtilMethods.getConfidenceString(tx.getConfidence().getConfidenceType()));
        confidenceImageView.setImageResource(UtilMethods.getConfidenceResource(tx.getConfidence().getConfidenceType()));

        Coin fee = tx.getFee();
        if (fee == null) {
            feeView.setText(R.string.N_A); //If transaction wasn't sent by us we don't know fee.
        } else {
            feeView.setText(fee.toFriendlyString());
        }
        confirmationView.setText(String.valueOf(tx.getConfidence().getDepthInBlocks()));
    }
}
