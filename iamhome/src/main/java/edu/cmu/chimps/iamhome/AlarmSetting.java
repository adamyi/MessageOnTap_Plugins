package edu.cmu.chimps.iamhome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by wangyusen on 7/16/17.
 */

public class AlarmSetting {
    public static int HOUR = 9;
    public static int MIN = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void fireAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        Calendar right_now = Calendar.getInstance();

        int time_Offset = HOUR - right_now.get(Calendar.HOUR);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, HOUR - time_Offset);
        calendar.set(Calendar.MINUTE, MIN);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent penintent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        //set the alarm repeat one day

        alm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, penintent);
        //cancel the wifi if the user get the home wifi address
        if(WifiStorage.getUsersHomewifiList(context)!=null){
            alm.cancel(penintent);
        }

    }
}
