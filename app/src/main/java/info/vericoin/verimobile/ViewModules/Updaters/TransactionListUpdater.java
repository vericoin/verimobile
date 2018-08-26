package info.vericoin.verimobile.ViewModules.Updaters;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;

import java.util.List;

import info.vericoin.verimobile.Adapters.TransactionListAdapter;
import info.vericoin.verimobile.WalletManager;

public class TransactionListUpdater implements WalletChangeEventListener {

    private TransactionListAdapter adapter;

    private int size = Integer.MAX_VALUE;

    private Wallet wallet;

    public TransactionListUpdater(Wallet wallet, TransactionListAdapter adapter) {
        this.adapter = adapter;
        this.wallet = wallet;
    }

    public TransactionListUpdater(Wallet wallet, TransactionListAdapter adapter, int size) {
        this(wallet, adapter);
        this.size = size;
    }

    public void listenForTransactions() {
        wallet.addChangeEventListener(WalletManager.runInUIThread, this);
    }

    public void updateTransactionList() {
        List<Transaction> transactionList = wallet.getTransactionsByTime();
        adapter.setmDataset(transactionList.subList(0, Math.min(size, transactionList.size())));
    }

    @Override
    public void onWalletChanged(Wallet wallet) {
        updateTransactionList();
    }

    public void stopListening() {
        wallet.removeChangeEventListener(this);
    }
}
