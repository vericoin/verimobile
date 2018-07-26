package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;

public class SendActivity extends AppCompatActivity {

    public static Intent createIntent(Context context){
        return new Intent(context, SendActivity.class);
    }

    private TextInputLayout sendAddr;
    private TextInputLayout sendAmount;
    private Button sendButton;
    private TextView txHash;
    private TextView receiveAddr;
    private WalletAppKit kit;
    private TextInputLayout feeInput;
    private TextView totalAmountText;
    private TextView availableBalanceText;

    private static String INSUFFICIENT_MONEY_STRING = "Not enough money.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        sendAddr = findViewById(R.id.sendAddr);
        sendAmount = findViewById(R.id.sendAmount);
        sendButton = findViewById(R.id.sendButton);
        txHash = findViewById(R.id.txHash);
        receiveAddr = findViewById(R.id.receiveAddr);
        feeInput = findViewById(R.id.fee);
        feeInput.setEnabled(false);
        totalAmountText = findViewById(R.id.totalAmount);
        availableBalanceText = findViewById(R.id.availableBalance);

        sendAddr.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFee();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendAmount.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFee();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void updateFee(){
        try {
            Coin feeCoin = estimateFee();
            Coin sendAmount = Coin.parseCoin(getAmount());
            setFeeText(feeCoin.toFriendlyString());
            setAmountError("");

            setTotalAmountText(feeCoin.add(sendAmount).toFriendlyString());
        }catch(InsufficientMoneyException e){
            Coin zero = Coin.ZERO;
            setAmountError(INSUFFICIENT_MONEY_STRING);
            setTotalAmountText(zero.toFriendlyString());
            setFeeText(zero.toFriendlyString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setTotalAmountText(String totalAmount){
        totalAmountText.setText(totalAmount);
    }

    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {

            @Override
            public void OnSetUpComplete(WalletAppKit kit) {
                SendActivity.this.kit = kit;

                updateAvailableBalance();
                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendTransaction(getAmount(), getSendAddress());
                    }
                });
                receiveAddr.setText(kit.wallet().currentReceiveAddress().toString());
            }
        });
    }

    public void updateAvailableBalance(){
        availableBalanceText.setText(kit.wallet().getBalance().toFriendlyString());
    }

    public void setAmountError(String err){
        sendAmount.setError(err);
        if(err.isEmpty()) {
            sendAmount.setErrorEnabled(false);
        }
    }

    public String getAmount(){
        return sendAmount.getEditText().getText().toString();
    }

    public String getSendAddress(){
        return sendAddr.getEditText().getText().toString();
    }

    public void setFeeText(String fee){
        feeInput.getEditText().setText(fee);
    }

    public Coin estimateFee() throws Exception{
        SendRequest request = SendRequest.to(Address.fromString(kit.params(), getSendAddress()), Coin.parseCoin(getAmount()));
        kit.wallet().completeTx(request);
        return request.tx.getFee();
    }

    public void setSendAddr(String addr){
        sendAddr.getEditText().setText(addr);
    }

    public void clearInputs(){
        setTotalAmountText("");
        setSendAddr("");
    }

    public void sendTransaction(String amount, String addr){
        VeriTransaction veriTransaction = new VeriTransaction();
        veriTransaction.setBroadcastListener(new VeriTransaction.OnBroadcastListener() {
            @Override
            public void broadcastComplete(org.bitcoinj.core.Transaction tx) {
                Toast.makeText(SendActivity.this, "Transaction Sent!", Toast.LENGTH_LONG).show();
                txHash.setText(tx.getHashAsString());

                updateAvailableBalance();
                clearInputs();
            }
        });
        veriTransaction.sendTransaction(amount, addr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }
}
