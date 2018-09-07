package info.vericoin.verimobile.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.bitcoinj.utils.ExchangeRate;
import org.bitcoinj.utils.Fiat;
import org.json.JSONObject;

import java.util.ArrayList;

import info.vericoin.verimobile.Util.UtilMethods;
import info.vericoin.verimobile.VolleySingleton;

public class ExchangeManager {

    private final String EXCHANGE_RATE_LIST = "exchangeRateList";

    private final String CURRENCY_CODE = "USD";

    private SharedPreferences sharedPref;

    private SharedPreferences defaultPref;

    private ExchangeRate exchangeRate;

    public String getCurrencyCode() {
        return exchangeRate.fiat.currencyCode;
    }

    public ExchangeManager(SharedPreferences sharedPref, SharedPreferences defaultPref) {
        this.sharedPref = sharedPref;
        this.defaultPref = defaultPref;
    }

    public interface OnExchangeRateChange{

        void exchangeRateUpdated(ExchangeRate exchangeRate);

    }

    private ArrayList<OnExchangeRateChange> listeners = new ArrayList<>();

    public void addExchangeRateChangeListener(OnExchangeRateChange listener) {
        listeners.add(listener);
    }

    public void removeExchangeRateChangeListener(OnExchangeRateChange listener){
        listeners.remove(listener);
    }

    public void downloadExchangeRateList(Context context){
        //get rate from CoinGecko
        String url = "https://api.coingecko.com/api/v3/coins/bitcoin?localization=false";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject exchangeList = response.getJSONObject("market_data").getJSONObject("current_price");
                            saveExchangeList(exchangeList);
                            updateExchangeRate();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void notifyExchangeRateUpdated(){
        for(OnExchangeRateChange listener: listeners){
            listener.exchangeRateUpdated(exchangeRate);
        }
    }

    private void saveExchangeList(JSONObject exchangeList){
        sharedPref.edit().putString(EXCHANGE_RATE_LIST, exchangeList.toString()).apply();
    }

    public void updateExchangeRate(){
        updateExchangeRate(defaultPref.getString("fiatType", "usd"));
    }

    public void updateExchangeRate(String fiatType){
        try {
            String exchangeListJson = sharedPref.getString(EXCHANGE_RATE_LIST, "");
            JSONObject exchangeList = new JSONObject(exchangeListJson);
            double fiatAmount = exchangeList.getDouble(fiatType);
            exchangeRate = new ExchangeRate(Fiat.parseFiat(fiatType.toUpperCase(), Double.toString(UtilMethods.round(fiatAmount, 2))));
        }catch (Exception e){
            exchangeRate = new ExchangeRate(Fiat.parseFiat("usd".toUpperCase(), "1.00")); //Don't use 0 for default. It will throw exception. (Can't divide by 0)
        }
        notifyExchangeRateUpdated();
    }

    public ExchangeRate getExchangeRate() {
        if(exchangeRate == null) {
            updateExchangeRate();
        }
        return exchangeRate;
    }
}
