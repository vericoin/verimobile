package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import info.vericoin.verimobile.Updaters.TransactionListUpdater;

public class TransactionListActivity extends WalletAppKitActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;

    private TransactionListUpdater transactionListUpdater;

    public static Intent createIntent(Context context) {
        return new Intent(context, TransactionListActivity.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_transaction_list);

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

        // specify an adapter (see also next example)
        if (mAdapter == null) {
            mAdapter = new TransactionListAdapter(kit,TransactionListActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }

        if(transactionListUpdater == null){
            transactionListUpdater = new TransactionListUpdater(kit.wallet(), mAdapter);
        }

        transactionListUpdater.updateTransactionList();
        transactionListUpdater.listenForTransactions();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        transactionListUpdater.stopListening();
    }

}
