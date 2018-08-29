package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;

import java.util.ArrayList;

import info.vericoin.verimobile.Adapters.ContactListAdapter;
import info.vericoin.verimobile.Managers.ContactManager;
import info.vericoin.verimobile.Models.Contact;
import info.vericoin.verimobile.Models.VeriTransaction;
import info.vericoin.verimobile.Util.SendHelper;

public class RecipientActivity extends WalletAppKitActivity implements ContactListAdapter.OnContactListener {

    private TextInputLayout sendAddr;

    private Button nextButton;

    private ConstraintLayout scanButton;

    private VeriMobileApplication veriMobileApplication;

    private ContactManager contactManager;

    private RecyclerView recyclerView;

    private ContactListAdapter adapter;

    private LinearLayoutManager layoutManager;

    public static Intent createIntent(Context context) {
        return new Intent(context, RecipientActivity.class);
    }

    @Override
    protected void onWalletKitStop() {
        nextButton.setOnClickListener(null);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_recipient);

        veriMobileApplication = (VeriMobileApplication) getApplication();
        contactManager = veriMobileApplication.getContactManager();

        recyclerView = findViewById(R.id.recyclerView);

        if(adapter == null){
            adapter = new ContactListAdapter(this, contactManager, true);
        }

        layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Contact> contactList = contactManager.getContactList();

        if(contactList.isEmpty()){
            recyclerView.setVisibility(View.GONE);
        }else {
            adapter.setContactList(contactList);
        }

        sendAddr = findViewById(R.id.sendAddr);

        sendAddr.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendAddr.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(RecipientActivity.this).initiateScan();
            }
        });

        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddr.setErrorEnabled(false);
                String addressString = sendAddr.getEditText().getText().toString();
                try {
                    Address.fromString(kit.params(), addressString); //Make sure Address is valid
                    verifyUser(addressString);
                } catch (AddressFormatException e) {
                    e.printStackTrace();
                    sendAddr.setError(getString(R.string.invalid_address));
                }
            }
        });
    }

    public void verifyUser(String address){
        VeriTransaction veriTransaction = new VeriTransaction();
        veriTransaction.setContact(new Contact(address));

        SendHelper sendHelper = new SendHelper(kit, this, veriTransaction);
        sendHelper.startNextActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                sendAddr.getEditText().setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(Contact contact, int index) {
        VeriTransaction veriTransaction = new VeriTransaction();
        veriTransaction.setContact(contact);
        SendHelper sendHelper = new SendHelper(kit, this, veriTransaction);
        sendHelper.startNextActivity();
    }
}
