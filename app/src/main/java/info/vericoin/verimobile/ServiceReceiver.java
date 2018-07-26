package info.vericoin.verimobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceReceiver extends BroadcastReceiver {

    public interface OnConnectionReceivedListener{
        void ConnectionReceived();
    }

    private OnConnectionReceivedListener listener;

    public ServiceReceiver(OnConnectionReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(BitcoinService.BROADCAST_ESTABLISHED_CONNECTION)){
            listener.ConnectionReceived();
        }
    }
}
