package info.vericoin.verimobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.bitcoinj.kits.WalletAppKit;

import java.io.IOException;

import info.vericoin.verimobile.Dialogs.PasswordDialog;
import info.vericoin.verimobile.Util.FingerprintHelper;

public class SettingsActivity extends WalletAppKitActivity {

    public final static String BTC_WALLET_FILE_NAME = "Bitcoin_Testnet3_Wallet";

    public final static String MIME_TYPE = "application/x-bitcoin";

    private static String PASSWORD_DIALOG_TAG = "passwordDialog";

    private MyPreferenceFragment fragment;

    public static Intent createIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onWalletKitReady() {

        if (fragment == null) {
            fragment = new MyPreferenceFragment();
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    @Override
    protected void onWalletKitStop() {
        getFragmentManager().beginTransaction().remove(fragment);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        // Unique request code.
        private static final int WRITE_REQUEST_CODE = 43;
        private Preference changePasswordRow;
        private Preference exportWallet;
        private Preference viewSeed;
        private Preference deleteWallet;
        private CheckBoxPreference lockTransactions;
        private CheckBoxPreference fingerPrint;
        private VeriMobileApplication veriMobileApplication;
        private FingerprintHelper fingerprintHelper;
        private PreferenceCategory securityCategory;
        private CheckBoxPreference secureWindow;
        private WalletAppKit kit;
        private SettingsActivity settingsActivity;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_screen);
            veriMobileApplication = (VeriMobileApplication) getActivity().getApplication();
            settingsActivity = (SettingsActivity) getActivity();
            kit = settingsActivity.kit;

            changePasswordRow = findPreference(getString(R.string.change_password_button));
            changePasswordRow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(ChangePasswordActivity.createIntent(getActivity()));
                    return true;
                }
            });

            exportWallet = findPreference(getString(R.string.export_wallet_button));
            exportWallet.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    PasswordDialog dialog = new PasswordDialog();
                    dialog.setListener(new PasswordDialog.OnPasswordListener() {
                        @Override
                        public void onSuccess(String password) {
                            createFile(MIME_TYPE, BTC_WALLET_FILE_NAME);
                            ;
                        }
                    });
                    if (veriMobileApplication.doesPasswordExist()) {
                        dialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), PASSWORD_DIALOG_TAG);
                    } else {
                        createFile(MIME_TYPE, BTC_WALLET_FILE_NAME);
                    }
                    return true;
                }
            });

            deleteWallet = findPreference(getString(R.string.delete_wallet_button));
            deleteWallet.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    PasswordDialog dialog = new PasswordDialog();
                    dialog.setListener(new PasswordDialog.OnPasswordListener() {
                        @Override
                        public void onSuccess(String password) {
                            deleteWallet();
                        }
                    });
                    if (veriMobileApplication.doesPasswordExist()) {
                        dialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), PASSWORD_DIALOG_TAG);
                    } else {
                        deleteWallet();
                    }
                    return true;
                }
            });

            securityCategory = (PreferenceCategory) findPreference(getString(R.string.security_key));

            lockTransactions = (CheckBoxPreference) findPreference(getString(R.string.lock_transactions_key));

            fingerPrint = (CheckBoxPreference) findPreference(getString(R.string.fingerprint_enabled_key));

            secureWindow = (CheckBoxPreference) findPreference(getString(R.string.secure_window_key));

            fingerprintHelper = new FingerprintHelper((AppCompatActivity) getActivity());

            if (!fingerprintHelper.isFingerPrintSupported()) { //Device doesn't support fingerprint remove preference
                securityCategory.removePreference(fingerPrint);
            }

            viewSeed = findPreference(getString(R.string.view_seed_button));
        }

        public void deleteWallet() {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Are you sure you want to delete your wallet? This action can not be undone.")
                    .setTitle("Delete Wallet")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Do nothing
                        }
                    })
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(DeleteWalletActivity.createIntent(getActivity()));
                        }
                    });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public void changeCheckBoxUsingPassword(final CheckBoxPreference checkBoxPreference) {
            final boolean after = checkBoxPreference.isChecked();
            checkBoxPreference.setChecked(!after); //Prevent any change before password

            PasswordDialog passwordDialog = new PasswordDialog();
            passwordDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), PASSWORD_DIALOG_TAG);

            passwordDialog.setListener(new PasswordDialog.OnPasswordListener() {
                @Override
                public void onSuccess(String password) {
                    checkBoxPreference.setChecked(after);
                }
            });
        }

        public boolean doesPasswordExist() {
            return veriMobileApplication.doesPasswordExist();
        }

        public void openViewSeedActivity(String password) {
            startActivity(ViewSeedActivity.createIntent(getActivity(), password));
        }

        // Here are some examples of how you might call this method.
        // The first parameter is the MIME type, and the second parameter is the name
        // of the file you are creating:
        //
        // createFile("text/plain", "foobar.txt");
        // createFile("image/png", "mypicture.png");

        @Override
        public void onResume() {
            super.onResume();

            if (veriMobileApplication.doesPasswordExist()) { //Require password before changing these settings.
                lockTransactions.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                        changeCheckBoxUsingPassword(checkBoxPreference);
                        return true;
                    }
                });
                secureWindow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                        changeCheckBoxUsingPassword(checkBoxPreference);
                        return true;
                    }
                });
                fingerPrint.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                        changeCheckBoxUsingPassword(checkBoxPreference);
                        return true;
                    }
                });
                viewSeed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PasswordDialog passwordDialog = new PasswordDialog();
                        passwordDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), PASSWORD_DIALOG_TAG);
                        passwordDialog.setListener(new PasswordDialog.OnPasswordListener() {
                            @Override
                            public void onSuccess(String password) {
                                openViewSeedActivity(password);
                            }

                        });
                        return true;
                    }
                });
            } else {
                viewSeed.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        openViewSeedActivity("");
                        return true;
                    }
                });
                lockTransactions.setOnPreferenceClickListener(null);
                secureWindow.setOnPreferenceClickListener(null);
                fingerPrint.setOnPreferenceClickListener(null);
            }

            if (kit.wallet().isEncrypted()) { //Wallet is encrypted and there is a password.
                lockTransactions.setEnabled(false);
                fingerPrint.setEnabled(true);
            } else if (doesPasswordExist()) { //Wallet is NOT encrypted and there is a password.
                lockTransactions.setEnabled(true);
                fingerPrint.setEnabled(true);
            } else {                          //Wallet is NOT encrypted and there is NO password.
                lockTransactions.setEnabled(false);
                fingerPrint.setEnabled(false);
            }
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
                    Uri uri = data.getData();
                    try {
                        kit.wallet().saveToFileStream(getActivity().getContentResolver().openOutputStream(uri));
                        Toast.makeText(getActivity(), getString(R.string.wallet_saved), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
