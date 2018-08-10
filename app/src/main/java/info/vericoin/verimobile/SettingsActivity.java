package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;


import org.bitcoinj.kits.WalletAppKit;

public class SettingsActivity extends AppCompatActivity {

    public static Intent createIntent(Context context){
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {

        private Preference changePasswordRow;

        private CheckBoxPreference lockTransactions;

        private CheckBoxPreference fingerPrintEnabled;

        private BitcoinApplication bitcoinApplication;

        private FingerprintHelper fingerprintHelper;

        private PreferenceCategory categoryAccount;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_screen);
            bitcoinApplication = (BitcoinApplication) getActivity().getApplication();

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

            fingerPrintEnabled = (CheckBoxPreference) findPreference(getString(R.string.fingerprint_enabled_key));

            fingerprintHelper = new FingerprintHelper((AppCompatActivity) getActivity());

            if(!fingerprintHelper.isFingerPrintSupported()){ //Device doesn't support fingerprint remove preference
                categoryAccount.removePreference(fingerPrintEnabled);
            }

            lockTransactions.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
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

                    return true;
                }
            });
        }

        public boolean doesPasswordExist(){
            return bitcoinApplication.doesPasswordExist();
        }

        @Override
        public void onResume() {
            super.onResume();

            WalletConnection.connect(new WalletConnection.OnConnectListener() {
                @Override
                public void OnSetUpComplete(WalletAppKit kit) {
                    if(kit.wallet().isEncrypted()){
                        lockTransactions.setEnabled(false);
                        lockTransactions.setChecked(true);
                    }else if(doesPasswordExist()){
                        lockTransactions.setEnabled(true);
                        fingerPrintEnabled.setEnabled(true);
                    }else{
                        lockTransactions.setEnabled(false);
                        lockTransactions.setChecked(false);
                        fingerPrintEnabled.setEnabled(false);
                    }

                }

                @Override
                public void OnSyncComplete() {

                }
            });
        }

        @Override
        public void onPause() {
            super.onPause();
            WalletConnection.disconnect();
        }

    }
}
