package info.vericoin.verimobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bitcoinj.core.PeerAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import info.vericoin.verimobile.Util.UtilMethods;

public class VeriMobileApplication extends MultiDexApplication {

    private final static String PASSWORD_HASH_PREF = "passwordHash";

    private final static String CUSTOM_PEER_LIST = "customPeerList";

    private final static String PREFERENCE_FILE_KEY = "info.vericoin.verimobile.PREFERENCE_FILE_KEY";

    private SharedPreferences sharedPref;

    private SharedPreferences defaultPref;

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        defaultPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        WalletSingleton.setVeriMobileApplication(this);
        UtilMethods.setContext(this);

        if (WalletSingleton.doesWalletExist(this)) {
            WalletSingleton.startWallet(this);
        }
    }

    public boolean checkPassword(String password) {
        String passwordHash = sharedPref.getString(PASSWORD_HASH_PREF, "");
        if (passwordHash.isEmpty()) {
            return true; //There is no password
        } else {
            return passwordHash.equals(UtilMethods.hashStringSHA256(password));
        }
    }

    public boolean isLockTransactions() {
        return defaultPref.getBoolean(getString(R.string.lock_transactions_key), false);
    }

    public boolean doesPasswordExist() {
        String password = sharedPref.getString(VeriMobileApplication.PASSWORD_HASH_PREF, "");
        if (password.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public void removePassword() {
        sharedPref.edit().remove(VeriMobileApplication.PASSWORD_HASH_PREF).apply();
    }

    public void newPassword(String newPassword) {
        sharedPref.edit().putString(VeriMobileApplication.PASSWORD_HASH_PREF, UtilMethods.hashStringSHA256(newPassword)).apply();
    }

    public String getPasswordHash() {
        return sharedPref.getString(VeriMobileApplication.PASSWORD_HASH_PREF, "");
    }

    public boolean isFingerPrintEnabled() {
        return defaultPref.getBoolean(getString(R.string.fingerprint_enabled_key), true);
    }

    public boolean isSecureWindowEnabled() {
        return defaultPref.getBoolean(getString(R.string.secure_window_key), true);
    }

    public ArrayList<PeerAddress> getCustomPeerAddressList(){
        ArrayList<PeerAddress> peerAddressList = new ArrayList<>();
        ArrayList<String> peerList = getCustomPeerStringList();
        for(int i = 0; i < peerList.size(); i++){
            try {
                PeerAddress peerAddress = new PeerAddress(WalletSingleton.getParams(), InetAddress.getByName(peerList.get(i)));
                peerAddressList.add(peerAddress);
            }catch(UnknownHostException e){
                e.printStackTrace();
            }
        }
        return peerAddressList;
    }

    public ArrayList<String> getCustomPeerStringList(){
        String peerListJson = sharedPref.getString(CUSTOM_PEER_LIST, "");
        if(peerListJson.isEmpty()){
            return new ArrayList<>(); //Return empty list
        }else {
            Gson gson = new Gson();
            return gson.fromJson(peerListJson, new TypeToken<ArrayList<String>>() {}.getType());
        }
    }

    public void addPeerAddress(String hostName){
        ArrayList<String> peerStringList = getCustomPeerStringList();
        peerStringList.add(hostName);
        saveCustomPeerList(peerStringList);
    }

    public void removePeerAddress(String hostName){
        ArrayList<String> peerStringList = getCustomPeerStringList();
        peerStringList.remove(hostName);
        saveCustomPeerList(peerStringList);
    }

    public void saveCustomPeerList(ArrayList<String> peerStringList){
        Gson gson = new Gson();
        String peerListJson = gson.toJson(peerStringList);
        sharedPref.edit().putString(CUSTOM_PEER_LIST, peerListJson).apply();
    }

}
