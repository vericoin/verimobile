package info.vericoin.verimobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import info.vericoin.verimobile.Dialogs.DeleteContactDialog;
import info.vericoin.verimobile.Managers.ContactManager;
import info.vericoin.verimobile.Managers.WalletManager;
import info.vericoin.verimobile.Models.Contact;

public class EditContactActivity extends WalletAppKitActivity implements DialogInterface.OnClickListener{

    private final static String DELETE_CONTACT_DIALOG = "deleteContactDialog";
    private final static String INDEX_EXTRA = "index";
    private final static String CONTACT_EXTRA = "contact";

    private TextInputLayout contactLayout;
    private TextView address;
    private Button saveContactButton;
    private ConstraintLayout deleteButton;

    private ContactManager contactManager;

    private VeriMobileApplication veriMobileApplication;

    private WalletManager walletManager;

    private int index;
    private Contact contact;

    public static Intent createIntent(Context context, int index, Contact contact){
        Intent intent = new Intent(context, EditContactActivity.class);
        intent.putExtra(INDEX_EXTRA, index);
        intent.putExtra(CONTACT_EXTRA, contact);
        return intent;
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_edit_contact);

        index = getIntent().getIntExtra(INDEX_EXTRA, -1);
        contact = (Contact) getIntent().getSerializableExtra(CONTACT_EXTRA);

        veriMobileApplication = ((VeriMobileApplication) getApplication());
        walletManager = veriMobileApplication.getWalletManager();
        contactManager = veriMobileApplication.getContactManager();

        contactLayout = findViewById(R.id.contactLayout);
        address = findViewById(R.id.address);
        saveContactButton = findViewById(R.id.saveContactButton);
        deleteButton = findViewById(R.id.deleteButton);

        address.setText(contact.getAddress());
        contactLayout.setHint(contact.getName());

        saveContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateContact(getContactName());
                Toast.makeText(EditContactActivity.this, R.string.contact_updated, Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteContactDialog dialog = new DeleteContactDialog();
                dialog.show(getSupportFragmentManager(), DELETE_CONTACT_DIALOG);
            }
        });
    }

    public String getContactName(){
        return contactLayout.getEditText().getText().toString();
    }

    public void removeContact(){
        contactManager.removeContact(index);
    }

    public void updateContact(String name){
        if(!name.isEmpty()) {
            contact.setName(name);
        }
        contactManager.updateContact(index, contact);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        removeContact();
        setResult(RESULT_OK);
        finish();
    }
}
