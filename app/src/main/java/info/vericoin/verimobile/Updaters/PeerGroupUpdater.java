package info.vericoin.verimobile.Updaters;

import android.widget.TextView;

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.PeerConnectedEventListener;

import info.vericoin.verimobile.WalletConnection;

public class PeerGroupUpdater implements PeerConnectedEventListener {

    private PeerGroup peerGroup;

    private TextView connectedPeers;

    public PeerGroupUpdater(PeerGroup peerGroup, TextView connectedPeers) {
        this.peerGroup = peerGroup;
        this.connectedPeers = connectedPeers;
    }

    public void listenForPeerConnections(){
        peerGroup.addConnectedEventListener(WalletConnection.getRunInUIThread(), this);
    }

    @Override
    public void onPeerConnected(Peer peer, int peerCount) {
        updatePeerView();
    }

    public void updatePeerView(){
        connectedPeers.setText(Integer.toString(peerGroup.getConnectedPeers().size()));
    }

    public void stopListening(){
        peerGroup.removeConnectedEventListener(this);
    }

}
