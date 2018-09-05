package info.vericoin.verimobile.Managers;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import info.vericoin.verimobile.Listeners.BlockDownloadListener;
import info.vericoin.verimobile.Listeners.OnConnectListener;
import info.vericoin.verimobile.VeriMobileApplication;
import info.vericoin.verimobile.WelcomeActivity;

public class WalletManager {

    private static final String FILE_PREFIX = "forwarding-service-testnet";
    private static final String WALLET_FILE_NAME = FILE_PREFIX + ".wallet";
    private static final String CHAIN_FILE_NAME = FILE_PREFIX + ".spvchain";
    public static Executor runInUIThread = new Executor() {
        @Override
        public void execute(Runnable runnable) {
            Handler handler = new Handler(Looper.getMainLooper());
            // For Android: handler was created in an Activity.onCreate method.
            handler.post(runnable);
        }
    };
    private boolean startUpComplete = false;
    private boolean finishedDownloadingBlockchain = false;
    private List<OnConnectListener> connectListeners = new ArrayList<>();
    private NetworkParameters params = TestNet3Params.get();
    private WalletAppKit kit;
    private VeriMobileApplication application;
    private ArrayList<BlockDownloadListener> blockDownloadListeners = new ArrayList<>();

    public WalletManager(VeriMobileApplication application) {
        this.application = application;
    }

    private static void deleteChainFile(Context context) {
        File chainFile = new File(context.getFilesDir(), CHAIN_FILE_NAME);
        chainFile.delete();
    }

    public NetworkParameters getParams() {
        return params;
    }

    public boolean doesWalletExist(Context context) {
        return (new File(context.getFilesDir(), WALLET_FILE_NAME).exists() || kit != null);
    }

    private void saveWallet(Context context, Wallet wallet) throws IOException {
        File file = new File(context.getFilesDir(), WALLET_FILE_NAME);
        wallet.saveToFile(file);
    }

    private void saveWalletFile(Context context, Uri uri) throws IOException, NullPointerException {
        File file = new File(context.getFilesDir(), WALLET_FILE_NAME);

        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        FileOutputStream fop = new FileOutputStream(file);

        int data = inputStream.read();
        while (data != -1) {
            fop.write(data);
            data = inputStream.read();
        }
        inputStream.close();
        fop.close();
    }

    public void deleteWallet(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                stopWalletAsync();

                application.getPasswordManager().removePassword();

                deleteWalletFile(context);
                deleteChainFile(context);

                context.startActivity(WelcomeActivity.createIntent(context));
            }
        }).start();
    }

    private void stopWalletAsync() {

        for (final OnConnectListener listener : connectListeners) {
            runInUIThread.execute(new Runnable() {
                @Override
                public void run() {
                    listener.onStopAsync();
                }
            });
        }

        clearListeners();

        kit.stopImmediately();

        resetVariables();
    }

    private void clearListeners() {
        connectListeners.clear();
        blockDownloadListeners.clear();
    }

    private void resetVariables() {
        kit = null;
        startUpComplete = false;
        finishedDownloadingBlockchain = false;
    }

    private void deleteWalletFile(Context context) {
        File walletFile = new File(context.getFilesDir(), WALLET_FILE_NAME);
        walletFile.delete();
    }

    public void addBlockDownloadListener(BlockDownloadListener listener) {
        if (finishedDownloadingBlockchain) {
            listener.finishedDownload();
        }
        blockDownloadListeners.add(listener);
    }

    public void removeBlockDownloadListener(BlockDownloadListener listener) {
        blockDownloadListeners.remove(listener);
    }

    private void initWalletAppKit(Context context) {
        // Start up a basic app using a class that automates some boilerplate.
        kit = new WalletAppKit(params, context.getFilesDir(), FILE_PREFIX) {
            @Override
            protected void onSetupCompleted() {

                //WalletAppKit is now ready to be used.
                runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (OnConnectListener onConnectListener : connectListeners) {
                            onConnectListener.onSetUpComplete(kit);
                        }
                    }
                });
                startUpComplete = true;
            }
        };

        try {
            ArrayList<PeerAddress> peerAddresses = application.getPeerManager().getCustomPeerAddressList();
            if (peerAddresses != null && !peerAddresses.isEmpty()) {
                kit.setPeerNodes(peerAddresses.toArray(new PeerAddress[peerAddresses.size()]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        kit.setBlockingStartup(false);
        kit.setDownloadListener(new DownloadProgressTracker() {
            @Override
            protected void progress(final double pct, final int blocksSoFar, final Date date) {
                runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (BlockDownloadListener listener : blockDownloadListeners) {
                            listener.progress(pct, blocksSoFar, date);
                        }
                    }
                });
            }

            @Override
            protected void doneDownload() {
                runInUIThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        finishedDownloadingBlockchain = true;
                        for (BlockDownloadListener listener : blockDownloadListeners) {
                            listener.finishedDownload();
                        }
                    }
                });
            }
        });
    }

    private void startWalletAsync() {
        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

        // Download the block chain.
        try {
            kit.startAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startWallet(Context context) { //Start wallet
        if (kit == null) {
            initWalletAppKit(context);
            startWalletAsync();
        }
    }

    public void createNewWallet(Context context) throws IOException {
        createNewWallet(context, null, false);
    }

    public void createNewWallet(Context context, @Nullable final String password, final boolean encryptWallet) throws IOException {
        if (kit == null) {
            KeyChainGroup kcg = new KeyChainGroup(params);
            Wallet wallet = new Wallet(params, kcg);
            if (password != null) {
                application.getPasswordManager().newPassword(password);
                if (encryptWallet) {
                    wallet.encrypt(password);
                }
            }
            saveWallet(context, wallet);
            startWalletAsync();
        }
    }

    public void createWalletFromSeed(Context context, final List<String> mnemonicList, final long creationTime) throws IOException {
        createWalletFromSeed(context, mnemonicList, creationTime, false, null);
    }

    public void createWalletFromSeed(Context context, final List<String> mnemonicList, final long creationTime, final boolean encryptWallet, @Nullable final String password) throws IOException {
        if (kit == null) {
            Wallet wallet = Wallet.fromSeed(params, new DeterministicSeed(mnemonicList, null, "", creationTime));
            if (password != null) {
                application.getPasswordManager().newPassword(password);
                if (encryptWallet) {
                    wallet.encrypt(password);
                }
            }
            saveWallet(context, wallet);
            startWalletAsync();
        }
    }

    public void createWalletFromFile(Context context, Uri uri) throws IOException, UnreadableWalletException {
        createWalletFromFile(context, uri, null, false);
    }

    public void createWalletFromFile(Context context, Uri uri, @Nullable String password, boolean encryptWallet) throws IOException, UnreadableWalletException {
        if (kit == null) {
            if (password == null) {
                saveWalletFile(context, uri);
            } else {
                application.getPasswordManager().newPassword(password);
                if (encryptWallet) {
                    Wallet encryptedWallet = Wallet.loadFromFileStream(context.getContentResolver().openInputStream(uri));
                    encryptedWallet.encrypt(password);
                    saveWallet(context, encryptedWallet);
                } else {
                    saveWalletFile(context, uri);
                }
            }
            startWalletAsync();
        }
    }

    public void addConnectListener(OnConnectListener listener) {
        if (kit != null && startUpComplete) { //Check to see if Wallet is already running.
            listener.onSetUpComplete(kit);
        }
        connectListeners.add(listener);
    }

    public void removeConnectListener(OnConnectListener listener) {
        connectListeners.remove(listener);
    }

}
