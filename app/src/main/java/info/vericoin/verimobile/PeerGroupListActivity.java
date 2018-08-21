package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import info.vericoin.verimobile.Updaters.PeerGroupListUpdater;

public class PeerGroupListActivity extends WalletAppKitActivity {

    private RecyclerView recyclerView;
    private PeerGroupListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private PeerGroupListUpdater peerGroupListUpdater;

    public static Intent createIntent(Context context){
        return new Intent(context, PeerGroupListActivity.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_recycler_view);

        recyclerView = findViewById(R.id.recyclerView);
        if(adapter == null){
            adapter = new PeerGroupListAdapter();
        }
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        recyclerView.setAdapter(adapter);

        if(peerGroupListUpdater == null){
            peerGroupListUpdater = new PeerGroupListUpdater(kit.peerGroup(), adapter);
        }
        peerGroupListUpdater.updateListView();
        peerGroupListUpdater.startPeriodicUpdate();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        peerGroupListUpdater.stopPeriodicUpdate();
    }

}
