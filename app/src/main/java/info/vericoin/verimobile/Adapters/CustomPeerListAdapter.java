package info.vericoin.verimobile.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.PeerAddress;

import java.util.ArrayList;

import info.vericoin.verimobile.CustomPeerListActivity;
import info.vericoin.verimobile.R;
import info.vericoin.verimobile.VeriMobileApplication;

public class CustomPeerListAdapter extends RecyclerView.Adapter<CustomPeerListAdapter.ViewHolder> {

    private ArrayList<PeerAddress> peerList = new ArrayList<>();

    public void setPeerList(ArrayList<PeerAddress> peerList) {
        this.peerList = peerList;
        notifyDataSetChanged();
    }

    private CustomPeerListActivity activity;

    public CustomPeerListAdapter(CustomPeerListActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public CustomPeerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_peer_address, parent, false);

        CustomPeerListAdapter.ViewHolder vh = new CustomPeerListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomPeerListAdapter.ViewHolder holder, final int position) {

        final PeerAddress peerAddress = peerList.get(position);

        holder.hostNameView.setText(peerAddress.toString());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setPositiveButton(R.string.remove_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activity.getVeriMobileApplication().getPeerManager().removePeerAddress(peerAddress.getAddr().getHostAddress());
                                peerList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setTitle(R.string.remove_peer_title);

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView hostNameView;

        private ViewHolder(ConstraintLayout v) {
            super(v);
            hostNameView = v.findViewById(R.id.hostName);
        }
    }
}
