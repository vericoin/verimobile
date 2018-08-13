package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import org.bitcoinj.kits.WalletAppKit;

public class SettingsActivity extends VeriActivity {

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

            categoryAccount = (PreferenceCategory) findPreference("account");

            lockTransactions = (CheckBoxPreference) findPreference(getString(R.string.lock_transactions_key));

            fingerPrint = (CheckBoxPreference) findPreference(getString(R.string.fingerprint_enabled_key));

            secureWindow = (CheckBoxPreference) findPreference(getString(R.string.secure_window_key));

            fingerprintHelper = new FingerprintHelper((AppCompatActivity) getActivity());

            if (!fingerprintHelper.isFingerPrintSupported()) { //Device doesn't support fingerprint remove preference
                categoryAccount.removePreference(fingerPrint);
            }
        }

        public void changeCheckBoxUsingPassword(final CheckBoxPreference checkBoxPreference) {
            final boolean after = checkBoxPreference.isChecked();
            checkBoxPreference.setChecked(!after); //Prevent any change before password

            PasswordDialog passwordDialog = new PasswordDialog();
            passwordDialog.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "PasswordDialog");

            passwordDialog.setListener(new PasswordDialog.OnPasswordListener() {
                @Override
                public void onSuccess() {
                    checkBoxPreference.setChecked(after);
                }
            });
        }

        public boolean doesPasswordExist() {
            return veriMobileApplication.doesPasswordExist();
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
            } else {
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

    }
}
