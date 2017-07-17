package edu.cmu.chimps.iamhome;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.privacystreams.core.exceptions.PSException;

/**
 * Created by wangyusen on 7/16/17.
 */

public class SaveHomeWifiService extends IntentService{
    public static final String ACTION1 = "ACTION1";
    public static final String ACTION2 = "ACTION2";

    public SaveHomeWifiService(String name) {
        super(name);
    }
    public SaveHomeWifiService(){
        super("");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String action = intent.getAction();
        if (ACTION1.equals(action)) {
            //the user press yes and confirm he is at home. We store the current wifi BSSIDs;
            NotificationManager curr_notification =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            curr_notification.cancel(001);

            try {
                WifiStorage.storeUsersHomewifi(this);

            } catch (PSException e) {
                e.printStackTrace();
            }
        } else if (ACTION2.equals(action)) {
            // dismiss the location and do nothing
            NotificationManager curr_notification =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            curr_notification.cancel(001);
        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }
}
