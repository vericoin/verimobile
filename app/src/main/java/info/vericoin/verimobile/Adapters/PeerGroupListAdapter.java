package info.vericoin.verimobile.Adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bitcoinj.core.Peer;

import java.util.ArrayList;
import java.util.List;

import info.vericoin.verimobile.R;

public class PeerGroupListAdapter extends RecyclerView.Adapter<PeerGroupListAdapter.ViewHolder> {

    private List<Peer> peerList = new ArrayList<>();

    public void setPeerList(List<Peer> peerList) {
        this.peerList = peerList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PeerGroupListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.peer_item, parent, false);

        PeerGroupListAdapter.ViewHolder vh = new PeerGroupListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PeerGroupListAdapter.ViewHolder holder, int position) {
        Peer peer = peerList.get(position);

        long ping = peer.getPingTime();
        String address = peer.getAddress().getAddr().toString();
        int clientVersion = peer.getPeerVersionMessage().clientVersion;

        holder.pingView.setText(Long.toString(ping) + " ms");
        holder.addressView.setText(address);
        holder.clientVersionView.setText(Integer.toString(clientVersion));
    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView clientVersionView;
        public TextView addressView;
        public TextView pingView;

        public ViewHolder(ConstraintLayout v) {
            super(v);
            clientVersionView = v.findViewById(R.id.clientVersion);
            pingView = v.findViewById(R.id.ping);
            addressView = v.findViewById(R.id.address);
        }
    }
}
