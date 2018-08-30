package info.vericoin.verimobile.Adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.vericoin.verimobile.DragItemTouchHelperAdapter;
import info.vericoin.verimobile.Managers.ContactManager;
import info.vericoin.verimobile.Models.Contact;
import info.vericoin.verimobile.R;

public class ContactListAdapterDrag extends RecyclerView.Adapter<ContactListAdapterDrag.ViewHolder> implements DragItemTouchHelperAdapter {

    private ArrayList<Contact> contactList = new ArrayList<>();

    private OnContactListener listener;

    private ContactManager contactManager;

    private boolean removeBackground;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Contact contact = contactList.get(fromPosition);
        contactList.remove(fromPosition);
        contactList.add(toPosition, contact);
        notifyItemMoved(fromPosition, toPosition);
        contactManager.moveContact(fromPosition, toPosition);
    }

    public interface OnContactListener{
        void onClick(Contact contact, int index);
    }

    public void setContactList(ArrayList<Contact> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public ContactListAdapterDrag(OnContactListener listener, ContactManager contactManager, boolean removeBackground) {
        this.listener = listener;
        this.contactManager = contactManager;
        this.removeBackground = removeBackground;
    }

    @NonNull
    @Override
    public ContactListAdapterDrag.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);

        if(removeBackground){
            v.setBackgroundColor(0);
        }

        ContactListAdapterDrag.ViewHolder vh = new ContactListAdapterDrag.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactListAdapterDrag.ViewHolder holder, final int position) {

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
                listener.onClick(contact, holder.getAdapterPosition());
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView contactView;
        private TextView addressView;

        private ViewHolder(ConstraintLayout v) {
            super(v);
            contactView = v.findViewById(R.id.contact);
            addressView = v.findViewById(R.id.address);
        }

        public void onItemSelected() {
            itemView.setBackgroundResource(R.color.selectLTGray);
        }

        public void onItemClear() {
            itemView.setBackgroundResource(R.color.itemLTGray);
        }
    }
}
