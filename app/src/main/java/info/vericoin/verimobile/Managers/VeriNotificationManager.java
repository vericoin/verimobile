package info.vericoin.verimobile.Managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;

import info.vericoin.verimobile.R;
import info.vericoin.verimobile.SplashActivity;

public class VeriNotificationManager {

    private static final String CHANNEL_ID = "payment-channel-0";

    private static final int NOTIFICATION_ID = 0;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void clearTransactions(){
        transactions.clear();
    }

    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.payment_channel_name);
            String description = context.getString(R.string.payment_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(Context context, NotificationCompat.Builder mBuilder){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void createNotification(Context context, Wallet wallet, Transaction tx){
        transactions.add(tx);

        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_veri_noti_icon)
                .setContentTitle(context.getString(R.string.notification_payment_title))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for(int i = 0; i < transactions.size(); i++){
            String friendlyStringValue = transactions.get(i).getValue(wallet).toFriendlyString();
            String content = context.getString(R.string.received_text) + " " + friendlyStringValue;
            inboxStyle.addLine(content);
            mBuilder.setContentText(content);
        }
        mBuilder.setStyle(inboxStyle);

        showNotification(context, mBuilder);
    }

}
