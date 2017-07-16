package com.everbright.wangyusen.contacs_app;

/**
 * Created by wangyusen on 7/15/17.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Headsup_Noti.notification(context);
        Log.i("alarm","alarm worked");
    }

}