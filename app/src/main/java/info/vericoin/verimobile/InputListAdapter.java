package info.vericoin.verimobile;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.PBKDF2SHA512;
import org.bitcoinj.kits.WalletAppKit;

import java.text.DateFormat;
import java.util.List;

public class InputListAdapter extends RecyclerView.Adapter<InputListAdapter.ViewHolder> {

    private List<TransactionInput> mDataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView address;
        public TextView amount;

        public ViewHolder(ConstraintLayout v) {
            super(v);
            address = v.findViewById(R.id.address);
            amount = v.findViewById(R.id.amount);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InputListAdapter(Context context, List<TransactionInput> myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    public void setmDataset(List<TransactionInput> mDataset) {
        this.mDataset = mDataset;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InputListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_put, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TransactionInput put = mDataset.get(position);

        WalletAppKit kit = WalletConnection.getKit();

        Coin value = put.getValue();
        if(value == null){
            holder.amount.setText("Unknown");
        }else {
            holder.amount.setText(value.toFriendlyString());
        }

        holder.address.setText("WIP");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
