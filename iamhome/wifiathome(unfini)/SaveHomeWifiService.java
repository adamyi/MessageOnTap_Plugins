package com.everbright.wangyusen.contacs_app;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.privacystreams.core.exceptions.PSException;


/**
 * Created by wangyusen on 7/14/17.
 */

public class SaveHomeWifiService extends IntentService {
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


            NotificationManager curr_notification =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            curr_notification.cancel(001);
            //StatusToasts.saveHomeToast(this);
            try {
                Wifi_set.storeUsersHomewifi(this);
                //Log.i("name1s", String.valueOf(Wifi_set.getUsersHomewifiList(this)));

            } catch (PSException e) {
                e.printStackTrace();
            }
        } else if (ACTION2.equals(action)) {
            // TODO: 7/15/17 dismiss the notification
            NotificationManager curr_notification =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            curr_notification.cancel(001);

        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }
    }

