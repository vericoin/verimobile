package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import info.vericoin.verimobile.Adapters.CustomPeerListAdapter;
import info.vericoin.verimobile.Util.RecyclerViewEmptySupport;

public class CustomPeerListActivity extends WalletAppKitActivity {

    private final static int REQUEST_CODE = 1;

    private RecyclerViewEmptySupport recyclerView;
    private CustomPeerListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView emptyTextView;

    private FloatingActionButton addPeerfab;

    private VeriMobileApplication veriMobileApplication;

    public static Intent createIntent(Context context){
        return new Intent(context, CustomPeerListActivity.class);
    }

    public VeriMobileApplication getVeriMobileApplication() {
        return veriMobileApplication;
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_edit_peers);
        veriMobileApplication = (VeriMobileApplication) getApplication();

        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText(R.string.edit_peers_desc);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setEmptyView(emptyTextView
        );
        addPeerfab = findViewById(R.id.addPeerfab);

        if(adapter == null){
            adapter = new CustomPeerListAdapter(this);
        }

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        recyclerView.setAdapter(adapter);

        addPeerfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(ActivityAddPeer.createIntent(CustomPeerListActivity.this), REQUEST_CODE);
            }
        });

        adapter.setPeerList(veriMobileApplication.getPeerManager().getCustomPeerAddressList());
    }

    @Override
    protected void onWalletKitStop() {
        addPeerfab.setOnClickListener(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE  && resultCode  == RESULT_OK) {
            adapter.setPeerList(veriMobileApplication.getPeerManager().getCustomPeerAddressList()); //Update list if new peer was added.
        }
    }
}
