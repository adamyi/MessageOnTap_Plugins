package edu.cmu.chimps.iamhome;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by wangyusen on 7/16/17.
 */

public class HeadsupNotification {

    public static void notification(Context mContext){
        //setting yes action
        Intent iAction1 = new Intent(mContext, SaveHomeWifiService.class);
        iAction1.setAction(SaveHomeWifiService.ACTION1);
        PendingIntent piAction1 = PendingIntent
                .getService(mContext.getApplicationContext(), 0, iAction1, PendingIntent.FLAG_UPDATE_CURRENT);
        //setting no action
        Intent iAction2 = new Intent(mContext, SaveHomeWifiService.class);
        iAction2.setAction(SaveHomeWifiService.ACTION1);
        PendingIntent piAction2 = PendingIntent
                .getService(mContext.getApplicationContext(), 0, iAction2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentTitle("My notification")
                        .setContentText("Are you currently at home?").setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_whiteicon,"Yes", piAction1)
                        .addAction(R.drawable.ic_whiteicon, "No", piAction2);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());


    }
}
