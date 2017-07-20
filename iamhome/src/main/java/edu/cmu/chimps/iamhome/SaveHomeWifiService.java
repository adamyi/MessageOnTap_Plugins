package edu.cmu.chimps.iamhome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.privacystreams.core.exceptions.PSException;

import edu.cmu.chimps.iamhome.utils.AlarmUtils;
import edu.cmu.chimps.iamhome.utils.WifiUtils;


public class SaveHomeWifiService extends IntentService{
    public static final String ACTION_SAVE = "ACTION_SAVE";

    public SaveHomeWifiService(String name) {
        super(name);
    }
    public SaveHomeWifiService(){
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(AlarmReceiver.getNotificationId());

            if (action.equals(ACTION_SAVE)) {
                //the user press yes and confirm he is at home.
                // We store the current wifi BSSIDs;
                try {
                    WifiUtils.storeUsersHomeWifi(this);
                    AlarmUtils.cancelAlarm();
                }
                catch (PSException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
