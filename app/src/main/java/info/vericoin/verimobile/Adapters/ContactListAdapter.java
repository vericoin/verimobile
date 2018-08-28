package info.vericoin.verimobile.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.vericoin.verimobile.ContactListActivity;
import info.vericoin.verimobile.EditContactActivity;
import info.vericoin.verimobile.Models.Contact;
import info.vericoin.verimobile.R;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {


    private ContactListActivity activity;

    private ArrayList<Contact> contactList = new ArrayList<>();

    public void setContactList(ArrayList<Contact> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public ContactListAdapter(ContactListActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);

        ContactListAdapter.ViewHolder vh = new ContactListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactListAdapter.ViewHolder holder, final int position) {

        final Contact contact = contactList.get(position);

        holder.addressView.setText(contact.getAddress());

        String name = contact.getName();
        if(name == null){
            holder.contactView.setText(R.string.N_A);
        }else {
            holder.contactView.setText(name);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                // Add the buttons
                builder.setItems(R.array.contact_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        if(index == 0){ //Edit
                            activity.startActivityForResult(EditContactActivity.createIntent(activity, holder.getAdapterPosition(), contact), ContactListActivity.REQUEST_CODE);
                        }else{ //Send

                        }
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView contactView;
        private TextView addressView;


        private ViewHolder(ConstraintLayout v) {
            super(v);
            contactView = v.findViewById(R.id.contact);
            addressView = v.findViewById(R.id.address);
        }
    }
}
