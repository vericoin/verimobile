package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.bitcoinj.core.Address;
import org.bitcoinj.kits.WalletAppKit;

public class ReceiveActivity extends AppCompatActivity {

    private WalletAppKit kit;

    private TextView receiveView;

    public static Intent createIntent(Context context){
        return new Intent(context, ReceiveActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        receiveView = findViewById(R.id.receiveAddr);
    }


    @Override
    protected void onResume() {
        super.onResume();

        WalletConnection.connect(new WalletConnection.OnConnectListener() {
            @Override
            public void OnSetUpComplete(final WalletAppKit kit) {
                ReceiveActivity.this.kit = kit;
                Address receiveAddr = kit.wallet().currentReceiveAddress();
                receiveView.setText(receiveAddr.toString());
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        WalletConnection.disconnect();
    }
}
