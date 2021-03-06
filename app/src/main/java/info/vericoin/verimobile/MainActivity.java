package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.utils.ExchangeRate;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import info.vericoin.verimobile.Adapters.TransactionListAdapter;
import info.vericoin.verimobile.Managers.ExchangeManager;
import info.vericoin.verimobile.Managers.VeriNotificationManager;
import info.vericoin.verimobile.Managers.WalletManager;
import info.vericoin.verimobile.Util.RecyclerViewEmptySupport;
import info.vericoin.verimobile.ViewModules.Updaters.BlockchainUpdater;
import info.vericoin.verimobile.ViewModules.Updaters.PeerGroupUpdater;
import info.vericoin.verimobile.ViewModules.Updaters.TransactionListUpdater;
import info.vericoin.verimobile.ViewModules.Updaters.WalletValueUpdater;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends WalletAppKitActivity implements ExchangeManager.OnExchangeRateChange, WalletCoinsReceivedEventListener{

    private final static int RECENT_TRANSACTION_SIZE = 5;

    private TextView unconfirmedBalance;
    private TextView availableBalance;
    private TextView blockHeight;
    private TextView connectedPeers;
    private TextView emptyTextViewTXs;
    private CardView blockChainView;
    private Button sendButton;
    private Button receiveButton;
    private ConstraintLayout coinBalanceLayout;

    private ImageView coinIcon;

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

    private WalletManager walletManager;
    private ExchangeManager exchangeManager;
    private VeriNotificationManager veriNotificationManager;

    private ConstraintLayout fullBalanceLayout;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private boolean showVRC = true;

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_main);
        walletManager = ((VeriMobileApplication) getApplication()).getWalletManager();
        exchangeManager = ((VeriMobileApplication) getApplication()).getExchangeManager();
        veriNotificationManager = ((VeriMobileApplication) getApplication()).getVeriNotificationManager();
        veriNotificationManager.clearTransactions();

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
        coinBalanceLayout = findViewById(R.id.coinBalanceLayout);

        coinIcon = findViewById(R.id.coinIcon);
        fullBalanceLayout = findViewById(R.id.fullBalanceLayout);

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

        loadVRC();
        coinBalanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showVRC = !showVRC;

                if(showVRC){
                    loadVRC();
                }else {
                    loadVRM();
                }
            }
        });

        // specify an adapter (see also next example)
        if (mAdapter == null) {
            mAdapter = new TransactionListAdapter(kit, MainActivity.this, exchangeManager.getExchangeRate());
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
            blockchainUpdater = new BlockchainUpdater(walletManager, kit.chain(), syncingBlock, percentComplete, blockHeight, lastSeenBlockDate);
        }

        blockchainUpdater.updateBlockChainView();
        blockchainUpdater.listenForBlocks();

        if (peerGroupUpdater == null) {
            peerGroupUpdater = new PeerGroupUpdater(kit.peerGroup(), connectedPeers);
        }

        peerGroupUpdater.updatePeerView();
        peerGroupUpdater.listenForPeerConnections();

        exchangeManager.addExchangeRateChangeListener(this);
        kit.wallet().addCoinsReceivedEventListener(WalletManager.runInUIThread, this);
    }

    public void loadVRC(){
        coinIcon.setImageResource(R.drawable.vrc_icon);
        fullBalanceLayout.setBackgroundResource(R.color.vrc_color);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.vrc_color)));
    }

    public void loadVRM(){
        coinIcon.setImageResource(R.drawable.vrm_icon);
        fullBalanceLayout.setBackgroundResource(R.color.vrm_color);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.vrm_color)));
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
        exchangeManager.removeExchangeRateChangeListener(this);
        kit.wallet().removeCoinsReceivedEventListener(this);
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
            case R.id.showFiat:
                //Swap between Fiat and Coin
                walletValueUpdater.setExchangeRate(exchangeManager.getExchangeRate());
                walletValueUpdater.swapCurrency();
                mAdapter.swapCurrency();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void exchangeRateUpdated(ExchangeRate exchangeRate) {
        walletValueUpdater.setExchangeRate(exchangeRate);
        walletValueUpdater.updateWalletView();

        mAdapter.setExchangeRate(exchangeRate);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        veriNotificationManager.clearTransactions();
    }
}
