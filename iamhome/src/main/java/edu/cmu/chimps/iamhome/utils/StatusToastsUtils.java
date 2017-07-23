package edu.cmu.chimps.iamhome.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import edu.cmu.chimps.iamhome.R;
import edu.cmu.chimps.iamhome.services.SendMessageService;

public class StatusToastsUtils {

    public static void atHomeToast(Context context){
        CharSequence text = "You are at home";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void leaveHomeToast(Context context){
        CharSequence text = "You have left home";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void wifiConnectedToast(Context context){
        CharSequence text = "Wifi Connected";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void wifiDisconnectedToast(Context context){
        CharSequence text = "Wifi Disconnected";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void saveHomeToast(Context context){
        CharSequence text = "save home success";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void createAthomeNoti(Context context){
        //setting yes action
        Intent sendMessageServiceIntent= new Intent(context, SendMessageService.class);
        sendMessageServiceIntent.setAction(SendMessageService.ACTION_SEND);
        PendingIntent yesPendingIntent = PendingIntent
                .getService(context.getApplicationContext(), 0, sendMessageServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //setting no action
        sendMessageServiceIntent = new Intent(context, SendMessageService.class);
        PendingIntent noPendingIntent = PendingIntent
                .getService(context.getApplicationContext(), 0, sendMessageServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("Do you want to send At Home Messgage to your selected friend")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .addAction(0, "Yes", yesPendingIntent)
                        .addAction(0, "No", noPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, mBuilder.build());
    }
}
