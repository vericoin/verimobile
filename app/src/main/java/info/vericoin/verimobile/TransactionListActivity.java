package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends WalletAppKitActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;

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

        List<Transaction> myDataset = getDataSet();
        // specify an adapter (see also next example)
        if (mAdapter == null) {
            mAdapter = new TransactionListAdapter(kit,TransactionListActivity.this, myDataset);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setmDataset(myDataset);
        }

        kit.wallet().addChangeEventListener(WalletConnection.getRunInUIThread(), new WalletChangeEventListener() {
            @Override
            public void onWalletChanged(Wallet wallet) {
                mAdapter.setmDataset(getDataSet());
            }
        });
    }

    public ArrayList<Transaction> getDataSet() {
        return new ArrayList<>(kit.wallet().getTransactionsByTime());
    }

}
