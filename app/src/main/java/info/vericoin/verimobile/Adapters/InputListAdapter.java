package info.vericoin.verimobile.Adapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.utils.ExchangeRate;

import java.util.List;

import info.vericoin.verimobile.R;
import info.vericoin.verimobile.Util.UtilMethods;

public class InputListAdapter extends RecyclerView.Adapter<InputListAdapter.ViewHolder> {

    private List<TransactionInput> mDataset;

    private WalletAppKit kit;

    private ExchangeRate exchangeRate;

    private boolean showFiatAmount = false;

    public void swapCurrency(){
        showFiatAmount = !showFiatAmount;
        notifyDataSetChanged();
    }

    public void setExchangeRate(ExchangeRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public InputListAdapter(WalletAppKit kit, List<TransactionInput> myDataset, ExchangeRate exchangeRate) {
        this.kit = kit;
        mDataset = myDataset;
        this.exchangeRate = exchangeRate;
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
                .inflate(R.layout.item_transaction_put, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TransactionInput input = mDataset.get(position);

        Coin value = input.getValue();
        if (value == null) {
            holder.amount.setText(R.string.N_A);
        } else {
            if(showFiatAmount){
                holder.amount.setText(UtilMethods.roundFiat(exchangeRate.coinToFiat(value)).toFriendlyString());
            }else {
                holder.amount.setText(value.toFriendlyString());
            }
        }

        TransactionOutput output = input.getConnectedOutput();
        try {
            holder.address.setText(output.getAddressFromP2PKHScript(kit.params()).toBase58());
        } catch (Exception e) {
            holder.address.setText(R.string.N_A);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

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
}
