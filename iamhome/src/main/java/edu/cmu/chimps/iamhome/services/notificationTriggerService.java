package edu.cmu.chimps.iamhome.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by wangyusen on 7/21/17.
 */

public class NotificationTriggerService extends IntentService{
    public static final String ACTION_SEND = "ACTION_SEND";
    public NotificationTriggerService(String name) {
        super(name);
    }
    public NotificationTriggerService(){
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
                notificationManager.cancel(2);
        if(action != null){
            if (action.equals(ACTION_SEND)) {
                //the user press yes and confirm he is at home.
                //// TODO: 7/21/17 send message to yuser
                Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                this.sendBroadcast(closeNotificationDrawer);
                Intent launchService = new Intent(this, ShareMessageService.class);
                startService(launchService);
            }
        }

    }
}
}
