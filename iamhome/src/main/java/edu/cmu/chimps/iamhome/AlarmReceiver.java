package edu.cmu.chimps.iamhome;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver{
    private static final int NOTIFICATION_ID = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        //triger notification
        createNotification(context);
    }

    public static int getNotificationId(){
        return NOTIFICATION_ID;
    }
    public void createNotification(Context context){
        //setting yes action
        Intent saveHomeWifiServiceIntent = new Intent(context, SaveHomeWifiService.class);
        saveHomeWifiServiceIntent.setAction(SaveHomeWifiService.ACTION_SAVE);
        PendingIntent yesPendingIntent = PendingIntent
                .getService(context.getApplicationContext(), 0, saveHomeWifiServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //setting no action
        saveHomeWifiServiceIntent = new Intent(context, SaveHomeWifiService.class);
        PendingIntent noPendingItent = PendingIntent
                .getService(context.getApplicationContext(), 0, saveHomeWifiServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(context.getResources().getString(R.string.Are_you_at_home))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_whiteicon, "Yes", yesPendingIntent)
                        .addAction(R.drawable.ic_whiteicon, "No", noPendingItent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


    }


}
