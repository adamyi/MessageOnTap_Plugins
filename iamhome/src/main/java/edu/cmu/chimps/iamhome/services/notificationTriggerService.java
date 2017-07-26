package edu.cmu.chimps.iamhome.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by wangyusen on 7/21/17.
 */

public class notificationTriggerService extends IntentService{
    public static final String ACTION_SEND = "ACTION_SEND";
    public notificationTriggerService(String name) {
        super(name);
    }
    public notificationTriggerService(){
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            /**
             * cancel send message notification
             */
            NotificationManager notificationManager =
                   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(3);
        if(action != null){
            if (action.equals(ACTION_SEND)) {
                NotificationManager notificationManager1 =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(3);
                //the user press yes and confirm he is at home.
                //// TODO: 7/21/17 send message to yuser
                Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                this.sendBroadcast(closeNotificationDrawer);
                Intent launchService = new Intent(this, ShareMessageService.class);
                startService(launchService);
            }
        }

    }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3);
}
}
