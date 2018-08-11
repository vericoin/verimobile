package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {

    private final static int RECENT_TRANSACTION_SIZE = 5;

    private TextView unconfirmedBalance;
    private TextView availableBalance;
    private TextView blockHeight;
    private Button sendButton;
    private Button receiveButton;
    private WalletAppKit kit;

    private TextView percentComplete;

    private Button viewTransactionsButton;

    private ConstraintLayout synchingBlock;

    private TextView lastSeenBlockDate;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;

    public static Intent createIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        unconfirmedBalance = findViewById(R.id.unconfirmedBalance);
        availableBalance = findViewById(R.id.availableBalance);
        blockHeight = findViewById(R.id.blockHeight);
        sendButton = findViewById(R.id.sendButton);
        receiveButton = findViewById(R.id.receiveButton);
        viewTransactionsButton = findViewById(R.id.viewTransactionsButton);
        synchingBlock = findViewById(R.id.synchingBlock);
        percentComplete = findViewById(R.id.percentComplete);
        lastSeenBlockDate = findViewById(R.id.lastSeenBlockDate);

        mRecyclerView = findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        viewTransactionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TransactionListActivity.createIntent(MainActivity.this));
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

    public ArrayList<Transaction> getDataSet(){
        List<Transaction> transactions = kit.wallet().getTransactionsByTime();
        return new ArrayList<>(transactions.subList(0, Math.min(RECENT_TRANSACTION_SIZE, transactions.size())));
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
                setPercentComplete(kit.wallet().getEstBlockchainPercentComplete());

                List<Transaction> myDataset = getDataSet();
                // specify an adapter (see also next example)
                if(mAdapter == null) {
                    mAdapter = new TransactionListAdapter(MainActivity.this, myDataset);
                    mRecyclerView.setAdapter(mAdapter);
                }else{
                    mAdapter.setmDataset(myDataset);
                }
            }

            @Override
            public void OnSyncComplete() {
                synchingBlock.setVisibility(View.INVISIBLE);
            }
        });

        WalletConnection.setOnWalletChangeListener(new WalletConnection.OnWalletChangeListener() {
            @Override
            public void walletChanged(Wallet wallet) {
                setBalances(wallet);
            }
        });

        WalletConnection.setOnNewBestBlockListener(new WalletConnection.OnNewBestBlockListener() {
            @Override
            public void newBlock(StoredBlock block) {
                setBlockHeight(block.getHeight());
                setLastSeenBlockDate(kit.wallet().getLastBlockSeenTime());
                setPercentComplete(kit.wallet().getEstBlockchainPercentComplete());
            }
        });

        WalletConnection.setOnCoinReceiveListener(new WalletConnection.OnCoinReceiveListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                mAdapter.setmDataset(getDataSet());
            }

            @Override
            public void onSuccess(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance, TransactionConfidence result) {
                mAdapter.setmDataset(getDataSet());
            }

            @Override
            public void onFailure(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                mAdapter.setmDataset(getDataSet());
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

    public void setPercentComplete(double percent){
        percentComplete.setText(String.valueOf(percent) + " %");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                //Write your code
                startActivity(SettingsActivity.createIntent(this));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
