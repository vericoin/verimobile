package info.vericoin.verimobile.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import info.vericoin.verimobile.PasswordManager;
import info.vericoin.verimobile.R;
import info.vericoin.verimobile.VeriMobileApplication;

public class PasswordDialog extends DialogFragment {

    private TextInputLayout passwordLayout;
    private OnPasswordListener listener;
    private PasswordManager passwordManager;

    public void setListener(OnPasswordListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        passwordManager = ((VeriMobileApplication) getActivity().getApplication()).getPasswordManager();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_password, null);

        passwordLayout = view.findViewById(R.id.passwordLayout);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle(R.string.password_required)
                .setPositiveButton(R.string.enter_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isPasswordCorrect(getPasswordFromInput())) {
                            if (listener != null) {
                                listener.onSuccess(getPasswordFromInput());
                            }
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.password_is_incorrect, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do Nothing
                    }
                });
        return builder.create();
    }

    public void setError(String error) {
        if (error.isEmpty()) {
            passwordLayout.setErrorEnabled(false);
        } else {
            passwordLayout.setError(error);
        }
    }

    public String getPasswordFromInput() {
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean isPasswordCorrect(String password) {
        return passwordManager.checkPassword(password);
    }

    public interface OnPasswordListener {
        void onSuccess(String password);
    }
}
