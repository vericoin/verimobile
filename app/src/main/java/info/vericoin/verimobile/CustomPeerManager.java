package info.vericoin.verimobile;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bitcoinj.core.PeerAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CustomPeerManager {

    private final static String CUSTOM_PEER_LIST = "customPeerList";

    private SharedPreferences sharedPref;

    public CustomPeerManager(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
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

    private ArrayList<String> getCustomPeerStringList(){
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

    private void saveCustomPeerList(ArrayList<String> peerStringList){
        Gson gson = new Gson();
        String peerListJson = gson.toJson(peerStringList);
        sharedPref.edit().putString(CUSTOM_PEER_LIST, peerListJson).apply();
    }

}
