package info.vericoin.verimobile.ViewModules.Updaters;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.NewBestBlockListener;

import java.util.Date;
import java.util.Locale;

import info.vericoin.verimobile.Listeners.BlockDownloadListener;
import info.vericoin.verimobile.Util.UtilMethods;
import info.vericoin.verimobile.WalletManager;

public class BlockchainUpdater implements NewBestBlockListener, BlockDownloadListener {

    private ConstraintLayout syncingBlock;

    private TextView percentComplete;

    private TextView blockHeight;

    private TextView lastSeenBlockDate;

    private BlockChain blockChain;

    private WalletManager walletManager;

    public BlockchainUpdater(WalletManager walletManager, BlockChain blockChain, ConstraintLayout syncingBlock, TextView percentComplete, TextView blockHeight, TextView lastSeenBlockDate) {
        this.walletManager = walletManager;
        this.syncingBlock = syncingBlock;
        this.percentComplete = percentComplete;
        this.blockHeight = blockHeight;
        this.lastSeenBlockDate = lastSeenBlockDate;
        this.blockChain = blockChain;
    }

    public void updateBlockChainView() {
        setPercentComplete(blockChain.getEstBlockchainPercentComplete());
        setBlockHeight(blockChain.getBestChainHeight());
        setLastSeenBlockDate(blockChain.getChainHead().getHeader().getTime());
    }

    public void listenForBlocks() {
        walletManager.addBlockDownloadListener(this);
    }

    public void setBlockHeight(int height) {
        blockHeight.setText(String.valueOf(height));
    }

    public void setPercentComplete(double percent) {
        percentComplete.setText(String.format(Locale.getDefault(), "%.2f %%", percent));
    }

    public void setLastSeenBlockDate(Date date) {
        lastSeenBlockDate.setText(UtilMethods.getDateTimeString(date));
    }

    @Override
    public void progress(double pct, int blocksSoFar, Date date) {
        updateBlockChainView();
    }

    @Override
    public void finishedDownload() {
        updateBlockChainView();
        syncingBlock.setVisibility(View.INVISIBLE);
        blockChain.addNewBestBlockListener(WalletManager.runInUIThread, this);
    }

    @Override
    public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
        updateBlockChainView();
    }

    public void stopListening() {
        blockChain.removeNewBestBlockListener(this);
        walletManager.removeBlockDownloadListener(this);
    }

}
