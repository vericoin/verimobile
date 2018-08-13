package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.NewBestBlockListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends VeriActivity {

    private final static int RECENT_TRANSACTION_SIZE = 5;

    private TextView unconfirmedBalance;
    private TextView availableBalance;
    private TextView blockHeight;
    private Button sendButton;
    private Button receiveButton;
    private WalletAppKit kit;

    private TextView percentComplete;

    private Button viewTransactionsButton;

    private ConstraintLayout syncingBlock;

    private TextView lastSeenBlockDate;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kit = WalletConnection.getKit();

        unconfirmedBalance = findViewById(R.id.unconfirmedBalance);
        availableBalance = findViewById(R.id.availableBalance);
        blockHeight = findViewById(R.id.blockHeight);
        sendButton = findViewById(R.id.sendButton);
        receiveButton = findViewById(R.id.receiveButton);
        viewTransactionsButton = findViewById(R.id.viewTransactionsButton);
        syncingBlock = findViewById(R.id.synchingBlock);
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

        setBalances(kit.wallet());
        updateBlockInformation();

        List<Transaction> myDataset = getDataSet();
        // specify an adapter (see also next example)
        if (mAdapter == null) {
            mAdapter = new TransactionListAdapter(MainActivity.this, myDataset);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmDataset(myDataset);
        }

        WalletConnection.setSyncCompleteListener(new WalletConnection.OnSyncCompleteListener() {
            @Override
            public void OnSyncComplete() {
                syncingBlock.setVisibility(View.INVISIBLE);
            }
        });
    }

    public ArrayList<Transaction> getDataSet() {
        List<Transaction> transactions = kit.wallet().getTransactionsByTime();
        return new ArrayList<>(transactions.subList(0, Math.min(RECENT_TRANSACTION_SIZE, transactions.size())));
    }

    public void updateBlockInformation(){
        setBlockHeight(kit.wallet().getLastBlockSeenHeight());
        setLastSeenBlockDate(kit.wallet().getLastBlockSeenTime());
        setPercentComplete(kit.wallet().getEstBlockchainPercentComplete());
    }

    @Override
    protected void onResume() {
        super.onResume();

        kit.wallet().addChangeEventListener(WalletConnection.getRunInUIThread(), new WalletChangeEventListener() {
            @Override
            public void onWalletChanged(Wallet wallet) {
                setBalances(wallet);
                mAdapter.setmDataset(getDataSet());
            }
        });

        kit.chain().addNewBestBlockListener(WalletConnection.getRunInUIThread(), new NewBestBlockListener() {
            @Override
            public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
                updateBlockInformation();
            }
        });

    }

    public void setBlockHeight(int height) {
        blockHeight.setText(String.valueOf(height));
    }

    public void setBalances(Wallet wallet) {
        Coin available = wallet.getBalance(Wallet.BalanceType.AVAILABLE);
        Coin estimated = wallet.getBalance(Wallet.BalanceType.ESTIMATED);
        Coin unconfirmed = estimated.subtract(available);
        setUnconfirmedBalance(unconfirmed);
        setAvailableBalance(available);
    }

    public void setPercentComplete(double percent) {
        percentComplete.setText(String.format(Locale.getDefault(), "%.2f %%", percent));
    }

    public void setLastSeenBlockDate(Date date) {
        lastSeenBlockDate.setText(Util.getDateTimeString(date));
    }

    public void setUnconfirmedBalance(Coin coin) {
        unconfirmedBalance.setText(coin.toFriendlyString());
    }

    public void setAvailableBalance(Coin coin) {
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
