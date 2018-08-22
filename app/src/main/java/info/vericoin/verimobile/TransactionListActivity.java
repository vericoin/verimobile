package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import info.vericoin.verimobile.Adapters.TransactionListAdapter;
import info.vericoin.verimobile.Util.RecyclerViewEmptySupport;
import info.vericoin.verimobile.ViewModules.Updaters.TransactionListUpdater;

public class TransactionListActivity extends WalletAppKitActivity {

    private RecyclerViewEmptySupport mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;

    private TransactionListUpdater transactionListUpdater;

    private TextView emptyTextView;

    public static Intent createIntent(Context context) {
        return new Intent(context, TransactionListActivity.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_recycler_view);

        mRecyclerView = findViewById(R.id.recyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText("No transactions yet.");
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
            mAdapter = new TransactionListAdapter(kit, TransactionListActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }

        if (transactionListUpdater == null) {
            transactionListUpdater = new TransactionListUpdater(kit.wallet(), mAdapter);
        }

        transactionListUpdater.updateTransactionList();
        transactionListUpdater.listenForTransactions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transactionListUpdater.stopListening();
    }

    @Override
    protected void onWalletKitStop() {
        transactionListUpdater.stopListening();
    }

}
