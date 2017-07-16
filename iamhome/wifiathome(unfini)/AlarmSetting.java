package com.everbright.wangyusen.contacs_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by wangyusen on 7/15/17.
 */

public class AlarmSetting {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void fireAlarm(Context context, int Hour, int Min) {
        Calendar calendar = Calendar.getInstance();
        Calendar right_now = Calendar.getInstance();


        int time_Offset = Hour - right_now.get(Calendar.HOUR);
//        calendar.set(Calendar.YEAR, right_now.get(Calendar.YEAR));
//        calendar.set(Calendar.MONTH, right_now.get(Calendar.MONTH) - 1);
//        calendar.set(Calendar.DAY_OF_MONTH, right_now.get(Calendar.DAY_OF_MONTH));
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, Hour - time_Offset);
        calendar.set(Calendar.MINUTE, Min);
        calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.DAY_OF_MONTH, 15);



        //used for debug
        Log.i("actual", String.valueOf(right_now.getTime()));
        Log.i("settingtime", String.valueOf(calendar.getTime()));

        Intent intent = new Intent(context, AlarmReciver.class);
        PendingIntent penintent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
       // alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), penintent);

        alm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
               calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, penintent);

    }
}
