package edu.cmu.chimps.iamhome.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import edu.cmu.chimps.iamhome.AlarmReceiver;

public class AlarmUtils {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setAlarm(Context context, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        Calendar right_now = Calendar.getInstance();

        int timeOffset = hour - right_now.get(Calendar.HOUR);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, hour - timeOffset);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //set the alarm repeat one day
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        //cancel the wifi if the user get the home wifi address
        if(WifiUtils.getUsersHomeWifiList()!=null){
            alarmManager.cancel(pendingIntent);
        }
    }
}
