package info.vericoin.verimobile.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

import info.vericoin.verimobile.R;

public class DeleteContactDialog extends DialogFragment {

    private DialogInterface.OnClickListener clickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditNameDialogListener so we can send events to the host
            clickListener = (DialogInterface.OnClickListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement DialogInterface.OnClickListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme_Light));

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.delete_contact_msg)
                .setPositiveButton(R.string.delete_button, clickListener)
                .setTitle(R.string.delete_contact);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        return dialog;
    }

}
