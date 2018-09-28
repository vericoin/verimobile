package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Coin;
import org.bitcoinj.utils.Fiat;

import info.vericoin.verimobile.Dialogs.ConfirmSendDialog;
import info.vericoin.verimobile.Managers.ExchangeManager;
import info.vericoin.verimobile.Models.VeriTransaction;
import info.vericoin.verimobile.Util.UtilMethods;

import static android.view.View.GONE;

public class ReviewActivity extends WalletAppKitActivity implements ConfirmSendDialog.OnClickListener{

    private final static String VERI_TRANSACTION = "veriTransaction";
    private final static String CONFIRM_DIALOG_TAG = "confirmDialog";
    private VeriMobileApplication veriMobileApplication;
    private ExchangeManager exchangeManager;
    private Button sendButton;
    private ProgressBar progressBar;
    private TextView totalView;
    private TextView feeView;
    private TextView amountView;
    private TextView addrView;
    private TextView contactView;

    private TextView totalFiatView;
    private TextView amountFiatView;
    private TextView feeFiatView;

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
        exchangeManager = veriMobileApplication.getExchangeManager();

        veriTransaction = (VeriTransaction) getIntent().getSerializableExtra(VERI_TRANSACTION);

        totalView = findViewById(R.id.totalAmount);
        feeView = findViewById(R.id.fee);
        amountView = findViewById(R.id.amount);
        addrView = findViewById(R.id.sendAddr);
        progressBar = findViewById(R.id.progressBar);
        sendButton = findViewById(R.id.sendButton);
        contactView = findViewById(R.id.contactName);
        totalFiatView = findViewById(R.id.totalFiat);
        feeFiatView = findViewById(R.id.feeFiat);
        amountFiatView = findViewById(R.id.amountFiat);

        progressBar.setVisibility(GONE);

        try {
            Coin total = veriTransaction.getTotal();
            Coin amount = veriTransaction.getAmount();
            Coin fee = veriTransaction.getFee();
            amountView.setText(amount.toFriendlyString());
            feeView.setText(fee.toFriendlyString());
            totalView.setText(total.toFriendlyString());
            addrView.setText(veriTransaction.getContact().getAddress());

            Fiat fiatTotal = exchangeManager.getExchangeRate().coinToFiat(total);
            Fiat fiatAmount = exchangeManager.getExchangeRate().coinToFiat(amount);
            Fiat fiatFee = exchangeManager.getExchangeRate().coinToFiat(fee);

            totalFiatView.setText(UtilMethods.roundFiat(fiatTotal).toFriendlyString());
            amountFiatView.setText(UtilMethods.roundFiat(fiatAmount).toFriendlyString());
            feeFiatView.setText(UtilMethods.roundFiat(fiatFee).toFriendlyString());

            String name = veriTransaction.getContact().getName();
            if(name == null || name.isEmpty()){
                contactView.setVisibility(GONE);
            }else{
                contactView.setText(name);
            }
        } catch (Exception e) {
            Toast.makeText(ReviewActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConfirmationDialog();
            }
        });
    }

    private void createConfirmationDialog(){
        ConfirmSendDialog confirmSendDialog = new ConfirmSendDialog();
        confirmSendDialog.setArguments(ConfirmSendDialog.createBundle(veriTransaction));
        confirmSendDialog.setListener(this);
        confirmSendDialog.show(getSupportFragmentManager(), CONFIRM_DIALOG_TAG);
    }

    @Override
    public void OnConfirm() {
        startActivity(ProcessTransactionActivity.createIntent(ReviewActivity.this, veriTransaction));
    }
}
