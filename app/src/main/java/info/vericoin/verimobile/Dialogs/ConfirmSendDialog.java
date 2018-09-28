package info.vericoin.verimobile.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import info.vericoin.verimobile.Models.VeriTransaction;
import info.vericoin.verimobile.R;

public class ConfirmSendDialog extends DialogFragment {

    private String address;

    private String amount;

    private final static String BUNDLE_ADDRESS = "address";
    private final static String BUNDLE_AMOUNT = "amount";

    public interface OnClickListener{
        void OnConfirm();
    }

    private OnClickListener listener;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public static Bundle createBundle(VeriTransaction veriTransaction){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ADDRESS, veriTransaction.getContact().getAddress());
        bundle.putString(BUNDLE_AMOUNT, veriTransaction.getAmount().toFriendlyString());
        return bundle;
    }

    private TextView addressView;
    private TextView amountView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        address = getArguments().getString(BUNDLE_ADDRESS);
        amount = getArguments().getString(BUNDLE_AMOUNT);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_send_title);

        View view = inflater.inflate(R.layout.dialog_confirm_send, null);
        addressView = view.findViewById(R.id.addressText);
        amountView = view.findViewById(R.id.amountText);

        addressView.setText(address);
        amountView.setText(amount);
        builder.setView(view);

        builder.setPositiveButton(R.string.send_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(listener != null){
                            listener.OnConfirm();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
