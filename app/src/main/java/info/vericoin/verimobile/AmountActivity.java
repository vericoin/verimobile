package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.kits.WalletAppKit;

import static info.vericoin.verimobile.VeriTransaction.BTC_TRANSACTION_FEE;

public class AmountActivity extends VeriActivity implements View.OnClickListener{

    private final static String ADDRESS_EXTRA = "address";

    private Address address;

    private TextView amount;

    private AmountParser amountParser;

    private Coin AMOUNT_DEFAULT = Coin.ZERO;

    private TextView button1;
    private TextView button2;
    private TextView button3;
    private TextView button4;
    private TextView button5;
    private TextView button6;
    private TextView button7;
    private TextView button8;
    private TextView button9;
    private TextView button0;
    private TextView dotButton;
    private ImageView backSpace;

    private Button nextButton;

    private WalletAppKit kit;

    public static Intent createIntent(Context context, Address address){
        Intent intent = new Intent(context, AmountActivity.class);
        intent.putExtra(ADDRESS_EXTRA, address);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        address = (Address) getIntent().getSerializableExtra(ADDRESS_EXTRA);

        amountParser = new AmountParser(AMOUNT_DEFAULT.toPlainString());

        amount = findViewById(R.id.amount);
        amount.setText(amountParser.getAmount() + " BTC");

        nextButton = findViewById(R.id.nextButton);

        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        dotButton = findViewById(R.id.dotbutton);
        backSpace = findViewById(R.id.backSpace);

        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        dotButton.setOnClickListener(this);
        backSpace.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(final WalletAppKit kit) {
                AmountActivity.this.kit = kit;

                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Coin amount = Coin.parseCoin(amountParser.getAmount());

                        if(amount.isGreaterThan(kit.wallet().getBalance().add(BTC_TRANSACTION_FEE))){
                            Toast.makeText(AmountActivity.this, "Wallet does not have enough funds", Toast.LENGTH_LONG).show();
                        }else {
                            startActivity(ReviewActivity.createIntent(AmountActivity.this, address, amount));
                        }
                    }
                });
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button0:
                amountParser.addDigit("0");
                break;
            case R.id.button1:
                amountParser.addDigit("1");
                break;
            case R.id.button2:
                amountParser.addDigit("2");
                break;
            case R.id.button3:
                amountParser.addDigit("3");
                break;
            case R.id.button4:
                amountParser.addDigit("4");
                break;
            case R.id.button5:
                amountParser.addDigit("5");
                break;
            case R.id.button6:
                amountParser.addDigit("6");
                break;
            case R.id.button7:
                amountParser.addDigit("7");
                break;
            case R.id.button8:
                amountParser.addDigit("8");
                break;
            case R.id.button9:
                amountParser.addDigit("9");
                break;
            case R.id.dotbutton:
                amountParser.dot();
                break;
            case R.id.backSpace:
                amountParser.backspace();
                break;
        }
        amount.setText(amountParser.getAmount() + " BTC");
    }
}
