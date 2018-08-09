package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_screen);

            changePasswordRow = findPreference(getString(R.string.change_password_button));
            changePasswordRow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(ChangePasswordActivity.createIntent(getActivity()));
                    return true;
                }
            });

            lockTransactions = (CheckBoxPreference) findPreference(getString(R.string.lock_transactions_key));
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
                    }else{
                        lockTransactions.setEnabled(true);
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
