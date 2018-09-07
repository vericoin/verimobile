package info.vericoin.verimobile.Managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.bitcoinj.utils.ExchangeRate;
import org.bitcoinj.utils.Fiat;
import org.json.JSONObject;

import java.util.ArrayList;

import info.vericoin.verimobile.Util.UtilMethods;
import info.vericoin.verimobile.VolleySingleton;

public class ExchangeManager {

    private final String EXCHANGE_RATE = "exchangeRate";

    private final String CURRENCY_CODE = "USD";

    private SharedPreferences sharedPref;

    private ExchangeRate exchangeRate;

    public String getCurrencyCode() {
        return CURRENCY_CODE;
    }

    public ExchangeManager(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
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

    public void updateExchangeRate(Context context){
        //get rate from CoinGecko
        String url = "https://api.coingecko.com/api/v3/coins/bitcoin?localization=false";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            double usd = response.getJSONObject("market_data").getJSONObject("current_price").getDouble("usd");
                            saveExchangeRate(Double.toString(usd));
                            notifyExchangeRateUpdated();
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

    private void saveExchangeRate(String usd){
        Double roundedFiat = UtilMethods.round(Double.parseDouble(usd), 2);
        Gson gson = new Gson();
        exchangeRate = new ExchangeRate(Fiat.parseFiat(CURRENCY_CODE, roundedFiat.toString()));
        sharedPref.edit().putString(EXCHANGE_RATE, gson.toJson(exchangeRate)).apply();
    }

    public ExchangeRate getExchangeRate() {
        if(exchangeRate == null) {
            Gson gson = new Gson();
            String exchangeRateJson = sharedPref.getString(EXCHANGE_RATE, "");
            if(exchangeRateJson.isEmpty()){
                exchangeRate = new ExchangeRate(Fiat.parseFiat(CURRENCY_CODE, "0")); //Default 0
            }else {
                exchangeRate = gson.fromJson(exchangeRateJson, ExchangeRate.class);
            }
        }
        return exchangeRate;
    }
}
