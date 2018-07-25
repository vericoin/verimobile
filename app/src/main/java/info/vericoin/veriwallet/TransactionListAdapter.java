package info.vericoin.veriwallet;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewHolder> {
    private List<Transaction> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ConstraintLayout constraintLayout;
        public TextView txHash;
        public TextView date;
        public TextView value;

        public ViewHolder(ConstraintLayout v) {
            super(v);
            constraintLayout = v;
            txHash = v.findViewById(R.id.txHash);
            date = v.findViewById(R.id.date);
            value = v.findViewById(R.id.value);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TransactionListAdapter(List<Transaction> myDataset) {
        mDataset = myDataset;
    }

    public void setmDataset(List<Transaction> mDataset) {
        this.mDataset = mDataset;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Transaction tx = mDataset.get(position);

        WalletAppKit kit = WalletConnection.getKit();
        holder.txHash.setText(tx.getHashAsString());

        DateFormat format = DateFormat.getDateInstance();
        holder.date.setText(format.format(tx.getUpdateTime()));
        holder.value.setText(tx.getValue(kit.wallet()).toFriendlyString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
