package info.vericoin.verimobile;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

public abstract class BackUpWalletActivity extends WalletAppKitActivity {

    public final static String BTC_WALLET_FILE_NAME = "Bitcoin_Testnet3_Wallet";

    public final static String MIME_TYPE = "application/x-bitcoin";

    // Unique request code.
    private static final int WRITE_REQUEST_CODE = 43;

    protected void backUpWallet(){
        createFile(MIME_TYPE, BTC_WALLET_FILE_NAME);
    }

    private void createFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (data != null) {
                final Uri uri = data.getData();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            kit.wallet().saveToFileStream(getContentResolver().openOutputStream(uri));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    walletBackedUp();
                                }
                            });
                        } catch (final IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BackUpWalletActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    protected void walletBackedUp(){
        Toast.makeText(this, getString(R.string.wallet_saved), Toast.LENGTH_LONG).show();
    }
}
