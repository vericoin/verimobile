package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.bitcoinj.core.Address;

import info.vericoin.verimobile.Managers.ContactManager;
import info.vericoin.verimobile.Managers.WalletManager;
import info.vericoin.verimobile.Models.Contact;

public class AddContactActivity extends WalletAppKitActivity {

    private ConstraintLayout scanButton;
    private TextInputLayout contactLayout;
    private TextInputLayout addressLayout;
    private Button addContactButton;

    private ContactManager contactManager;

    private VeriMobileApplication veriMobileApplication;

    private WalletManager walletManager;

    public static Intent createIntent(Context context){
        return new Intent(context, AddContactActivity.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_add_contact);

        veriMobileApplication = ((VeriMobileApplication) getApplication());
        walletManager = veriMobileApplication.getWalletManager();
        contactManager = veriMobileApplication.getContactManager();

        scanButton = findViewById(R.id.scanButton);
        contactLayout = findViewById(R.id.contactLayout);
        addressLayout = findViewById(R.id.addressLayout);
        addContactButton = findViewById(R.id.addContactButton);

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Address.fromString(walletManager.getParams(), getAddress()); //Check to see if Address is valid
                    if(isNameEmpty()){
                        contactLayout.setError(getString(R.string.invalid_input));
                    }else{
                        addContact(getName(), getAddress());
                        Toast.makeText(AddContactActivity.this, R.string.contact_added, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                }catch(Exception e){
                    addressLayout.setError(getString(R.string.invalid_address));
                }
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(AddContactActivity.this).initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                addressLayout.getEditText().setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getAddress(){
        return addressLayout.getEditText().getText().toString();
    }

    public String getName(){
        return contactLayout.getEditText().getText().toString();
    }

    public boolean isAddressEmpty(){
        return getAddress().isEmpty();
    }

    public boolean isNameEmpty(){
        return getName().isEmpty();
    }

    public void addContact(String name, String address){
        contactManager.addContact(new Contact(name, address));
    }
}
