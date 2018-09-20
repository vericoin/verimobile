package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import info.vericoin.verimobile.ViewModules.Updaters.TransactionListUpdater;

public class TransactionListActivity extends WalletAppKitActivity implements ExchangeManager.OnExchangeRateChange, WalletCoinsReceivedEventListener {

    private RecyclerViewEmptySupport mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;

    private TransactionListUpdater transactionListUpdater;

    private TextView emptyTextView;

    private VeriNotificationManager veriNotificationManager;

    public static Intent createIntent(Context context) {
        return new Intent(context, TransactionListActivity.class);
    }

    private VeriMobileApplication veriMobileApplication;
    private ExchangeManager exchangeManager;

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.recycler_view);

        veriMobileApplication = (VeriMobileApplication) getApplication();
        exchangeManager = veriMobileApplication.getExchangeManager();
        exchangeManager.addExchangeRateChangeListener(this);

        veriNotificationManager = veriMobileApplication.getVeriNotificationManager();
        veriNotificationManager.clearTransactions();

        mRecyclerView = findViewById(R.id.recyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText(R.string.no_transactions);
        mRecyclerView.setEmptyView(emptyTextView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        // specify an adapter (see also next example)
        if (mAdapter == null) {
            mAdapter = new TransactionListAdapter(kit, TransactionListActivity.this, exchangeManager.getExchangeRate());
            mRecyclerView.setAdapter(mAdapter);
        }

        if (transactionListUpdater == null) {
            transactionListUpdater = new TransactionListUpdater(kit.wallet(), mAdapter);
        }

        transactionListUpdater.updateTransactionList();
        transactionListUpdater.listenForTransactions();

        kit.wallet().addCoinsReceivedEventListener(WalletManager.runInUIThread, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transactionListUpdater.stopListening();
        kit.wallet().removeCoinsReceivedEventListener(this);
    }

    @Override
    protected void onWalletKitStop() {
        transactionListUpdater.stopListening();
        exchangeManager.removeExchangeRateChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.transaction_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.swapCurrency:
                //Write your code
                mAdapter.swapCurrency();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void exchangeRateUpdated(ExchangeRate exchangeRate) {
        mAdapter.setExchangeRate(exchangeRate);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        veriNotificationManager.clearTransactions();
    }
}
