package info.vericoin.verimobile.Updaters;

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
import info.vericoin.verimobile.Util;
import info.vericoin.verimobile.WalletConnection;

public class BlockchainUpdater implements NewBestBlockListener, BlockDownloadListener {

    private ConstraintLayout syncingBlock;

    private TextView percentComplete;

    private TextView blockHeight;

    private TextView lastSeenBlockDate;

    private BlockChain blockChain;

    public BlockchainUpdater(BlockChain blockChain, ConstraintLayout syncingBlock, TextView percentComplete, TextView blockHeight, TextView lastSeenBlockDate) {
        this.syncingBlock = syncingBlock;
        this.percentComplete = percentComplete;
        this.blockHeight = blockHeight;
        this.lastSeenBlockDate = lastSeenBlockDate;
        this.blockChain = blockChain;
    }

    public void updateBlockChainView(){
        setPercentComplete(blockChain.getEstBlockchainPercentComplete());
        setBlockHeight(blockChain.getBestChainHeight());
        setLastSeenBlockDate(blockChain.getChainHead().getHeader().getTime());
    }

    public void listenForBlocks(){
        WalletConnection.addBlockDownloadListener(this);
    }

    public void setBlockHeight(int height) {
        blockHeight.setText(String.valueOf(height));
    }

    public void setPercentComplete(double percent) {
        percentComplete.setText(String.format(Locale.getDefault(), "%.2f %%", percent));
    }

    public void setLastSeenBlockDate(Date date) {
        lastSeenBlockDate.setText(Util.getDateTimeString(date));
    }

    @Override
    public void progress(double pct, int blocksSoFar, Date date) {
        updateBlockChainView();
    }

    @Override
    public void doneDownload() {
        updateBlockChainView();
        syncingBlock.setVisibility(View.INVISIBLE);
        blockChain.addNewBestBlockListener(WalletConnection.getRunInUIThread(),this);
    }

    @Override
    public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
        updateBlockChainView();
    }

    public void stopListening(){
        blockChain.removeNewBestBlockListener(this);
        WalletConnection.removeBlockDownloadListener(this);
    }

}
