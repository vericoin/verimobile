package info.vericoin.verimobile.ViewModules.Updaters;

import org.bitcoinj.core.PeerGroup;

import java.util.Timer;
import java.util.TimerTask;

import info.vericoin.verimobile.Adapters.PeerGroupListAdapter;
import info.vericoin.verimobile.Managers.WalletManager;

public class PeerGroupListUpdater {

    private PeerGroup peerGroup;

    private PeerGroupListAdapter adapter;

    private Timer timer = new Timer();

    public PeerGroupListUpdater(PeerGroup peerGroup, PeerGroupListAdapter adapter) {
        this.peerGroup = peerGroup;
        this.adapter = adapter;
    }

    public void updateListView() {
        adapter.setPeerList(peerGroup.getConnectedPeers());
    }

    public void startPeriodicUpdate() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                WalletManager.runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        updateListView();
                    }
                });
            }
        }, 0, 1_000);
    }

    public void stopPeriodicUpdate() {
        timer.cancel();
    }
}
