package edu.cmu.chimps.iamhome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.privacystreams.core.exceptions.PSException;

import edu.cmu.chimps.iamhome.utils.AlarmUtils;
import edu.cmu.chimps.iamhome.utils.WifiUtils;

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
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(AlarmReceiver.getNotificationId());
        if(action != null){
            if (action.equals(ACTION_SEND)) {
                //the user press yes and confirm he is at home.
                //// TODO: 7/21/17 send message to yuser

            }
        }

    }
}
}
