package edu.cmu.chimps.iamhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by wangyusen on 7/16/17.
 */

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        HeadsupNotification.notification(context);
        Log.i("alarm","alarm worked");
    }
}
