package info.vericoin.verimobile;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(23)
public class FingerprintDialog extends DialogFragment {

    private ImageView imageView;
    private TextView message;

    public interface OnAuthListener{
        void onSuccess();
    }

    private OnAuthListener listener;

    public void setListener(OnAuthListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        FingerprintManager manager = getActivity().getSystemService(FingerprintManager.class);
        manager.authenticate(null, null, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if(errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
                    message.setText("Touch Sensor");
                    imageView.setImageResource(R.drawable.ic_baseline_fingerprint);
                }else{
                    message.setText(errString);
                    imageView.setImageResource(R.drawable.ic_error);
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
                message.setText(helpString);
                imageView.setImageResource(R.drawable.ic_baseline_fingerprint);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                message.setText("Authentication Success!");
                imageView.setImageResource(R.drawable.ic_baseline_fingerprint);

                if(listener != null){
                    listener.onSuccess();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                message.setText("Authentication Failed");
                imageView.setImageResource(R.drawable.ic_error);
            }
        }, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_fingerprint, null);

        imageView = view.findViewById(R.id.imageView);
        message = view.findViewById(R.id.message);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do Nothing
                    }
                });
        return builder.create();
    }

}
