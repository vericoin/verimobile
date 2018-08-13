package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.bitcoinj.core.Address;
import org.bitcoinj.kits.WalletAppKit;

public class ReceiveActivity extends VeriActivity {

    private WalletAppKit kit;

    private TextView receiveView;

    private ImageView qrImageView;

    public static Intent createIntent(Context context) {
        return new Intent(context, ReceiveActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        kit = WalletConnection.getKit();

        receiveView = findViewById(R.id.receiveAddr);
        qrImageView = findViewById(R.id.qrImage);

        Address receiveAddr = kit.wallet().currentReceiveAddress();
        String receiveAddrString = receiveAddr.toString();
        receiveView.setText(receiveAddrString);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(receiveAddrString, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = Util.createBitmap(bitMatrix);
            qrImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
