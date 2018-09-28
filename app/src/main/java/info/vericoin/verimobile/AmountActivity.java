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
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.utils.Fiat;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import info.vericoin.verimobile.Managers.ExchangeManager;
import info.vericoin.verimobile.Models.VeriTransaction;
import info.vericoin.verimobile.Util.UtilMethods;

import static android.view.View.GONE;

public class AmountActivity extends WalletAppKitActivity implements View.OnClickListener {

    private final static String VERI_TRANSACTION = "veriTransaction";

    private VeriTransaction veriTransaction;

    private ExchangeManager exchangeManager;

    private TextView primaryAmount;
    private TextView secondaryAmount;

    private AmountParser amountParser;

    private Coin AMOUNT_DEFAULT = Coin.ZERO;

    private VeriMobileApplication veriMobileApplication;

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

    private ProgressBar progressBar;

    private Button nextButton;

    private ConstraintLayout swapAmountButton;

    private boolean fiatIsPrimary = false;

    private Coin coin;
    private Fiat fiat;

    public static Intent createIntent(Context context, VeriTransaction veriTransaction) {
        Intent intent = new Intent(context, AmountActivity.class);
        intent.putExtra(VERI_TRANSACTION, veriTransaction);
        return intent;
    }

    @Override
    protected void onWalletKitStop() {
        nextButton.setOnClickListener(null);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_amount);
        veriMobileApplication = (VeriMobileApplication) getApplication();
        exchangeManager = veriMobileApplication.getExchangeManager();

        veriTransaction = (VeriTransaction) getIntent().getSerializableExtra(VERI_TRANSACTION);

        amountParser = new AmountParser(AMOUNT_DEFAULT.toPlainString());

        primaryAmount = findViewById(R.id.primaryAmount);
        secondaryAmount = findViewById(R.id.secondaryAmount);
        setAmountText();

        swapAmountButton = findViewById(R.id.swapAmountButton);
        swapAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fiatIsPrimary = !fiatIsPrimary;
                if(fiatIsPrimary) {
                    amountParser.setAmount(UtilMethods.roundFiat(fiat).toPlainString());
                    amountParser.setMaxDecimalPlaces(2);
                    amountParser.setMaxIntegerPlaces(12);
                }else{
                    amountParser.setAmount(coin.toPlainString());
                    amountParser.setMaxDecimalPlaces(8);
                    amountParser.setMaxIntegerPlaces(8);
                }
                setAmountText();
            }
        });

        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(GONE);

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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String addressString = veriTransaction.getContact().getAddress();
                            SendRequest request = SendRequest.to(Address.fromString(kit.params(), addressString), coin);

                            //Remove static fee if you want to use feePerKb instead
                            request.staticFee = VeriTransaction.DEFAULT_STATIC_FEE;

                            if(kit.wallet().isEncrypted()){
                                request.aesKey = kit.wallet().getKeyCrypter().deriveKey(veriTransaction.getPassword());
                            }
                            kit.wallet().completeTx(request); //Complete TX to see if we have enough funds to cover the fee.

                            final Coin fee = request.tx.getFee();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    reviewTransaction(coin, fee);
                                }
                            });
                        } catch (InsufficientMoneyException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    failed(getString(R.string.insufficient_funds));
                                }
                            });
                        } catch (Wallet.DustySendRequested e){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    failed(getString(R.string.dusty_send_request_err));
                                }
                            });
                        } catch (final Exception e){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    failed(e.toString());
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    public void waiting(){
        progressBar.setVisibility(View.VISIBLE);
        nextButton.setText("");
        nextButton.setEnabled(false);
    }

    public void failed(String error){
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(GONE);
        nextButton.setEnabled(true);
        nextButton.setText(R.string.next_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        setAmountText();
    }

    public void setAmountText(){

        if(fiatIsPrimary){
            fiat = Fiat.parseFiat(exchangeManager.getCurrencyCode(), amountParser.getAmount());
            coin = exchangeManager.getExchangeRate().fiatToCoin(fiat);

            primaryAmount.setText(amountParser.getAmount() + " " + fiat.currencyCode.toUpperCase());
            secondaryAmount.setText(coin.toFriendlyString());

        }else {
            coin = Coin.parseCoin(amountParser.getAmount());
            fiat = exchangeManager.getExchangeRate().coinToFiat(coin);

            primaryAmount.setText(amountParser.getAmount() + " BTC");
            secondaryAmount.setText(UtilMethods.roundFiat(fiat).toFriendlyString());
        }
    }

    public void reviewTransaction(Coin amount, Coin fee) {
        veriTransaction.setAmount(amount);
        veriTransaction.setFee(fee);

        progressBar.setVisibility(GONE);
        nextButton.setEnabled(true);
        nextButton.setText(R.string.next_button);
        startActivity(ReviewActivity.createIntent(this, veriTransaction));
    }
}
