package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletHistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TransactionListAdapter mAdapter;
    private WalletAppKit kit;

    public static Intent createIntent(Context context){
        return new Intent(context, WalletHistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public ArrayList<Transaction> getDataSet(){
        return new ArrayList<>(kit.wallet().getTransactionsByTime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        WalletConnection.connect(new WalletConnection.OnConnectListener() {

            @Override
            public void OnSetUpComplete(WalletAppKit kit) {
                WalletHistoryActivity.this.kit = kit;

                List<Transaction> myDataset = getDataSet();
                // specify an adapter (see also next example)
                mAdapter = new TransactionListAdapter(WalletHistoryActivity.this, myDataset);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void OnSyncComplete() {

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
}
