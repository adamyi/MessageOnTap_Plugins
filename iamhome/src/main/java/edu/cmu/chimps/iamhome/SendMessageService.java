package edu.cmu.chimps.iamhome;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by wangyusen on 7/21/17.
 */

public class SendMessageService extends IntentService{
    public static final String ACTION_SEND = "ACTION_SEND";
    public SendMessageService(String name) {
        super(name);
    }
    public SendMessageService(){
        super("");

    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            NotificationManager notificationManager =
                   (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(2);
        if(action != null){
            if (action.equals(ACTION_SEND)) {
                //the user press yes and confirm he is at home.
                //// TODO: 7/21/17 send message to yuser
                Intent launchService = new Intent(this, ShareMessageService.class);
                startService(launchService);
            }
        }

    }
}
}
