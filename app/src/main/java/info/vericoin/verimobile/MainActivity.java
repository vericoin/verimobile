package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import info.vericoin.verimobile.Adapters.TransactionListAdapter;
import info.vericoin.verimobile.Util.RecyclerViewEmptySupport;
import info.vericoin.verimobile.ViewModules.Updaters.BlockchainUpdater;
import info.vericoin.verimobile.ViewModules.Updaters.PeerGroupUpdater;
import info.vericoin.verimobile.ViewModules.Updaters.TransactionListUpdater;
import info.vericoin.verimobile.ViewModules.Updaters.WalletValueUpdater;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends WalletAppKitActivity {

    private final static int RECENT_TRANSACTION_SIZE = 5;

    private TextView unconfirmedBalance;
    private TextView availableBalance;
    private TextView blockHeight;
    private TextView connectedPeers;
    private TextView emptyTextViewTXs;
    private CardView blockChainView;
    private Button sendButton;
    private Button receiveButton;

    private TextView percentComplete;

    private Button viewTransactionsButton;

    private ConstraintLayout syncingBlock;

    private TextView lastSeenBlockDate;

    private RecyclerViewEmptySupport mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;

    private TransactionListUpdater transactionListUpdater;
    private WalletValueUpdater walletValueUpdater;
    private BlockchainUpdater blockchainUpdater;
    private PeerGroupUpdater peerGroupUpdater;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_main);

        unconfirmedBalance = findViewById(R.id.unconfirmedBalance);
        availableBalance = findViewById(R.id.availableBalance);
        blockHeight = findViewById(R.id.blockHeight);
        sendButton = findViewById(R.id.sendButton);
        receiveButton = findViewById(R.id.receiveButton);
        viewTransactionsButton = findViewById(R.id.viewTransactionsButton);
        syncingBlock = findViewById(R.id.synchingBlock);
        connectedPeers = findViewById(R.id.connectedPeers);
        percentComplete = findViewById(R.id.percentComplete);
        lastSeenBlockDate = findViewById(R.id.lastSeenBlockDate);
        mRecyclerView = findViewById(R.id.recyclerView);
        blockChainView = findViewById(R.id.blockChainCard);
        emptyTextViewTXs = findViewById(R.id.emptyTextViewTXs);

        mRecyclerView.setEmptyView(emptyTextViewTXs);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setEmptyViewListener(new RecyclerViewEmptySupport.OnEmptyViewListener() {
            @Override
            public void emptyViewIsOn() {
                viewTransactionsButton.setVisibility(View.GONE);
            }

            @Override
            public void emptyViewIsOff() {
                viewTransactionsButton.setVisibility(View.VISIBLE);
            }
        });

        blockChainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PeerGroupListActivity.createIntent(MainActivity.this));
            }
        });

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

        // specify an adapter (see also next example)
        if (mAdapter == null) {
            mAdapter = new TransactionListAdapter(kit, MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }

        if (transactionListUpdater == null) {
            transactionListUpdater = new TransactionListUpdater(kit.wallet(), mAdapter, RECENT_TRANSACTION_SIZE);
        }

        transactionListUpdater.updateTransactionList();
        transactionListUpdater.listenForTransactions();

        if (walletValueUpdater == null) {
            walletValueUpdater = new WalletValueUpdater(kit.wallet(), availableBalance, unconfirmedBalance);
        }

        walletValueUpdater.updateWalletView();
        walletValueUpdater.listenForBalanceChanges();

        if (blockchainUpdater == null) {
            blockchainUpdater = new BlockchainUpdater(kit.chain(), syncingBlock, percentComplete, blockHeight, lastSeenBlockDate);
        }

        blockchainUpdater.updateBlockChainView();
        blockchainUpdater.listenForBlocks();

        if (peerGroupUpdater == null) {
            peerGroupUpdater = new PeerGroupUpdater(kit.peerGroup(), connectedPeers);
        }

        peerGroupUpdater.updatePeerView();
        peerGroupUpdater.listenForPeerConnections();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopListeners();
    }

    @Override
    protected void onWalletKitStop() {
        stopListeners();
    }

    public void stopListeners() {
        transactionListUpdater.stopListening();
        walletValueUpdater.stopListening();
        blockchainUpdater.stopListening();
        peerGroupUpdater.stopListening();
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
