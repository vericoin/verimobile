package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.bitcoinj.kits.WalletAppKit;

import java.io.IOException;

public class SettingsActivity extends VeriActivity {

    public final static String BTC_WALLET_FILE_NAME = "Bitcoin_Testnet3_Wallet.dat";

    public static Intent createIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private Preference changePasswordRow;

        private Preference exportWallet;

        private Preference viewSeed;

        private CheckBoxPreference lockTransactions;

        private CheckBoxPreference fingerPrint;

        private VeriMobileApplication veriMobileApplication;

        private FingerprintHelper fingerprintHelper;

        private PreferenceCategory categoryAccount;

        private CheckBoxPreference secureWindow;

        private WalletAppKit kit;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_screen);
            veriMobileApplication = (VeriMobileApplication) getActivity().getApplication();
            kit = WalletConnection.getKit();

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
                    createFile("text/plain", BTC_WALLET_FILE_NAME);
                    return true;
                }
            });

            categoryAccount = (PreferenceCategory) findPreference("account");

            lockTransactions = (CheckBoxPreference) findPreference(getString(R.string.lock_transactions_key));

            fingerPrint = (CheckBoxPreference) findPreference(getString(R.string.fingerprint_enabled_key));

            secureWindow = (CheckBoxPreference) findPreference(getString(R.string.secure_window_key));

            fingerprintHelper = new FingerprintHelper((AppCompatActivity) getActivity());

            if (!fingerprintHelper.isFingerPrintSupported()) { //Device doesn't support fingerprint remove preference
                categoryAccount.removePreference(fingerPrint);
            }

            viewSeed = findPreference(getString(R.string.view_seed_button));
        }

        public void changeCheckBoxUsingPassword(final CheckBoxPreference checkBoxPreference) {
            final boolean after = checkBoxPreference.isChecked();
            checkBoxPreference.setChecked(!after); //Prevent any change before password

            PasswordDialog passwordDialog = new PasswordDialog();
            passwordDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "PasswordDialog");

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

        public void openViewSeedActivity(String password){
            startActivity(ViewSeedActivity.createIntent(getActivity(), password));
        }

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
                        passwordDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "PasswordDialog");
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

        // Here are some examples of how you might call this method.
        // The first parameter is the MIME type, and the second parameter is the name
        // of the file you are creating:
        //
        // createFile("text/plain", "foobar.txt");
        // createFile("image/png", "mypicture.png");

        // Unique request code.
        private static final int WRITE_REQUEST_CODE = 43;

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
        public void onActivityResult(int requestCode, int resultCode, Intent data){
            // The ACTION_OPEN_DOCUMENT intent was sent with the request code
            // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
            // response to some other intent, and the code below shouldn't run at all.

            if (requestCode == WRITE_REQUEST_CODE && resultCode == RESULT_OK) {
                // The document selected by the user won't be returned in the intent.
                // Instead, a URI to that document will be contained in the return intent
                // provided to this method as a parameter.
                // Pull that URI using resultData.getData().
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    try {
                        kit.wallet().saveToFileStream(getActivity().getContentResolver().openOutputStream(uri));
                        Toast.makeText(getActivity(), "Wallet saved!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
