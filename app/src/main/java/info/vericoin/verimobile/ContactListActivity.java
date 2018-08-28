package info.vericoin.verimobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import info.vericoin.verimobile.Adapters.ContactListAdapter;
import info.vericoin.verimobile.Managers.ContactManager;
import info.vericoin.verimobile.Util.RecyclerViewEmptySupport;

public class ContactListActivity extends WalletAppKitActivity{

    public final static int REQUEST_CODE = 2;

    private FloatingActionButton addFab;
    private RecyclerViewEmptySupport recyclerView;
    private ContactListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView emptyView;

    private ContactManager contactManager;

    public static Intent getIntent(Context context){
        return new Intent(context, ContactListActivity.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.recycler_view_add_button);
        contactManager = ((VeriMobileApplication) getApplication()).getContactManager();

        emptyView = findViewById(R.id.emptyTextView);
        emptyView.setText(R.string.no_contacts);
        addFab = findViewById(R.id.addFab);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setEmptyView(emptyView);

        if(adapter == null){
            adapter = new ContactListAdapter(this);
        }

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        adapter.setContactList(contactManager.getContactList());

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AddContactActivity.createIntent(ContactListActivity.this), REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onWalletKitStop() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            adapter.setContactList(contactManager.getContactList()); //Update list if new contact was added.
        }
    }

}
