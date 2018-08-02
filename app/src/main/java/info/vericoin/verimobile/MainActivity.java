package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView unconfirmedBalance;
    private TextView availableBalance;
    private TextView blockHeight;
    private Button sendButton;
    private Button receiveButton;
    private WalletAppKit kit;

    private ConstraintLayout walletView;

    private ConstraintLayout synchingBlock;

    private TextView lastSeenBlockDate;

    public static Intent createIntent(Context context){
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unconfirmedBalance = findViewById(R.id.unconfirmedBalance);
        availableBalance = findViewById(R.id.availableBalance);
        blockHeight = findViewById(R.id.blockHeight);
        sendButton = findViewById(R.id.sendButton);
        receiveButton = findViewById(R.id.receiveButton);
        walletView = findViewById(R.id.wallet_constraint_view);
        synchingBlock = findViewById(R.id.synchingBlock);
        lastSeenBlockDate = findViewById(R.id.lastSeenBlockDate);

        walletView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WalletHistoryActivity.createIntent(MainActivity.this));
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(RecipientActivity.createIntent(MainActivity.this));
            }
        });

        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ReceiveActivity.createIntent(MainActivity.this));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        WalletConnection.connect(new WalletConnection.OnConnectListener() {

            @Override
            public void OnSetUpComplete(WalletAppKit kit) {
                MainActivity.this.kit = kit;

                setBalances(kit.wallet());
                setBlockHeight(kit.wallet().getLastBlockSeenHeight());
                setLastSeenBlockDate(kit.wallet().getLastBlockSeenTime());

            }

            @Override
            public void OnSyncComplete() {
                synchingBlock.setVisibility(View.GONE);
            }
        });

        WalletConnection.setOnCoinReceiveListener(new WalletConnection.OnCoinReceiveListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                setBalances(wallet);
            }

            @Override
            public void onSuccess(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance, TransactionConfidence result) {
                setBalances(wallet);
            }

            @Override
            public void onFailure(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                setBalances(wallet);
            }
        });

        WalletConnection.setOnNewBestBlockListener(new WalletConnection.OnNewBestBlockListener() {
            @Override
            public void newBlock(StoredBlock block) {
                setBlockHeight(block.getHeight());
                setLastSeenBlockDate(kit.wallet().getLastBlockSeenTime());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }

    public void setBlockHeight(int height){
        blockHeight.setText(String.valueOf(height));
    }

    public void setBalances(Wallet wallet){
        Coin available = wallet.getBalance(Wallet.BalanceType.AVAILABLE);
        Coin estimated = wallet.getBalance(Wallet.BalanceType.ESTIMATED);
        Coin unconfirmed = estimated.subtract(available);
        setUnconfirmedBalance(unconfirmed);
        setAvailableBalance(available);
    }

    public void setLastSeenBlockDate(Date date){
        lastSeenBlockDate.setText(Util.getDateTimeString(date));
    }

    public void setUnconfirmedBalance(Coin coin){
        unconfirmedBalance.setText(coin.toFriendlyString());
    }

    public void setAvailableBalance(Coin coin){
        availableBalance.setText(coin.toFriendlyString());
    }
}
