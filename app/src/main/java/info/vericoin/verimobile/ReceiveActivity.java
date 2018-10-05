package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.bitcoinj.core.Address;

import info.vericoin.verimobile.Util.UtilMethods;

public class ReceiveActivity extends WalletAppKitActivity {

    private TextView receiveView;

    private ImageView qrImageView;

    private ShareActionProvider mShareActionProvider;

    private String receiveAddrString;

    public static Intent createIntent(Context context) {
        return new Intent(context, ReceiveActivity.class);
    }

    @Override
    protected void onWalletKitStop() {

    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_receive);

        receiveView = findViewById(R.id.receiveAddr);
        qrImageView = findViewById(R.id.qrImage);

        Address receiveAddr = kit.wallet().currentReceiveAddress();
        receiveAddrString = receiveAddr.toString();
        receiveView.setText(receiveAddrString);


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(receiveAddrString, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = UtilMethods.createBitmap(bitMatrix);
            qrImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receive_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_share:
                shareAddress();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareAddress() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, receiveAddrString);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.current_addr_share)));
    }

}
