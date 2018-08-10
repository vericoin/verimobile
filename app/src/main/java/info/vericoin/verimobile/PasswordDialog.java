package info.vericoin.verimobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class PasswordDialog extends DialogFragment {

    private TextInputLayout passwordLayout;

    public interface OnPasswordListener{
        void onSuccess();
    }

    private OnPasswordListener listener;

    public void setListener(OnPasswordListener listener) {
        this.listener = listener;
    }

    private BitcoinApplication bitcoinApplication;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        bitcoinApplication = (BitcoinApplication) getActivity().getApplication();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_password, null);

        passwordLayout = view.findViewById(R.id.passwordLayout);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle("Password Required")
                .setMessage("Please provide your password to modify this setting.")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isPasswordCorrect(getPasswordFromInput())){
                            if(listener != null){
                                listener.onSuccess();
                            }
                            dismiss();
                        }else{
                            Toast.makeText(getContext(), "Incorrect Password", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do Nothing
                    }
                });
        return builder.create();
    }

    public void setError(String error){
        if(error.isEmpty()){
            passwordLayout.setErrorEnabled(false);
        }else{
            passwordLayout.setError(error);
        }
    }

    public String getPasswordFromInput(){
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean isPasswordCorrect(String password){
        return bitcoinApplication.checkPassword(password);
    }
}
