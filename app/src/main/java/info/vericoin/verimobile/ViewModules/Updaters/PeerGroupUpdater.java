package info.vericoin.verimobile.ViewModules.Updaters;

import android.widget.TextView;

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.PeerConnectedEventListener;
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener;

import info.vericoin.verimobile.Managers.WalletManager;

public class PeerGroupUpdater implements PeerConnectedEventListener, PeerDisconnectedEventListener {

    private PeerGroup peerGroup;

    private TextView connectedPeers;

    public PeerGroupUpdater(PeerGroup peerGroup, TextView connectedPeers) {
        this.peerGroup = peerGroup;
        this.connectedPeers = connectedPeers;
    }

    public void listenForPeerConnections() {
        peerGroup.addConnectedEventListener(WalletManager.runInUIThread, this);
        peerGroup.addDisconnectedEventListener(WalletManager.runInUIThread, this);
    }

    @Override
    public void onPeerConnected(Peer peer, int peerCount) {
        updatePeerView();
    }

    public void updatePeerView() {
        connectedPeers.setText(Integer.toString(peerGroup.getConnectedPeers().size()));
    }

    public void stopListening() {
        peerGroup.removeConnectedEventListener(this);
        peerGroup.removeDisconnectedEventListener(this);
    }

    @Override
    public void onPeerDisconnected(Peer peer, int peerCount) {
        updatePeerView();
    }
}
