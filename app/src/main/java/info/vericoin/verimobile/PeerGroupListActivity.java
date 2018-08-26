package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private VeriMobileApplication veriMobileApplication;

    public static Intent createIntent(Context context) {
        return new Intent(context, PeerGroupListActivity.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_recycler_view);
        veriMobileApplication = (VeriMobileApplication) getApplication();

        emptyTextView = findViewById(R.id.emptyTextView);
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
    protected void onWalletKitResume() {
        if (!veriMobileApplication.getPeerManager().getCustomPeerAddressList().isEmpty()) {
            emptyTextView.setText(R.string.connected_peers_tip); //If list is empty and there's custom peers, then show this tip for user.
        } else {
            emptyTextView.setText(R.string.no_peers_connected);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.connected_peer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.modifyPeers:
                //Write your code
                startActivity(CustomPeerListActivity.createIntent(this));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
