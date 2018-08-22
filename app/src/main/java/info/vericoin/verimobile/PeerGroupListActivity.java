package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import info.vericoin.verimobile.Adapters.PeerGroupListAdapter;
import info.vericoin.verimobile.Util.RecyclerViewEmptySupport;
import info.vericoin.verimobile.ViewModules.Updaters.PeerGroupListUpdater;

public class PeerGroupListActivity extends WalletAppKitActivity {

    private RecyclerViewEmptySupport recyclerView;
    private PeerGroupListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private PeerGroupListUpdater peerGroupListUpdater;
    private TextView emptyTextView;

    public static Intent createIntent(Context context) {
        return new Intent(context, PeerGroupListActivity.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_recycler_view);

        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText("No peers connected yet.");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setEmptyView(emptyTextView);

        if (adapter == null) {
            adapter = new PeerGroupListAdapter();
        }
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        recyclerView.setAdapter(adapter);

        if (peerGroupListUpdater == null) {
            peerGroupListUpdater = new PeerGroupListUpdater(kit.peerGroup(), adapter);
        }
        peerGroupListUpdater.updateListView();
        peerGroupListUpdater.startPeriodicUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        peerGroupListUpdater.stopPeriodicUpdate();
    }

    @Override
    protected void onWalletKitStop() {
        peerGroupListUpdater.stopPeriodicUpdate();
    }

}
