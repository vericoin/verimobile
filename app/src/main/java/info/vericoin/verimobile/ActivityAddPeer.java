package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;

public class ActivityAddPeer extends WalletAppKitActivity {

    private TextInputLayout hostNameLayout;
    private Button addPeerButton;
    private VeriMobileApplication veriMobileApplication;

    public static Intent createIntent(Context context) {
        return new Intent(context, ActivityAddPeer.class);
    }

    @Override
    protected void onWalletKitReady() {
        setContentView(R.layout.activity_add_peer);
        veriMobileApplication = (VeriMobileApplication) getApplication();

        hostNameLayout = findViewById(R.id.hostNameLayout);
        addPeerButton = findViewById(R.id.addPeerButon);

        addPeerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostNameLayout.setErrorEnabled(false);
                String hostName = hostNameLayout.getEditText().getText().toString();
                if (hostName.isEmpty()) {
                    hostNameLayout.setError(getString(R.string.invalid_input));
                } else {
                    try {
                        InetAddress.getByName(hostName); //Check to see if hostName is valid
                        veriMobileApplication.getPeerManager().addPeerAddress(hostName);
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        hostNameLayout.setError(getString(R.string.invalid_input));
                    }
                }
            }
        });
    }

    @Override
    protected void onWalletKitStop() {
        addPeerButton.setOnClickListener(null);
    }
}
