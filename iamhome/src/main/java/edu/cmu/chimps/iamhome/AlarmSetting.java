package edu.cmu.chimps.iamhome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;


public class AlarmSetting {
    private final static int ALARM_HOUR = 22;
    private final static int ALARM_MINUTE = 0;
    private final static int ALARM_SECOND = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void fireAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        Calendar right_now = Calendar.getInstance();

        int time_Offset = ALARM_HOUR - right_now.get(Calendar.HOUR);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, ALARM_HOUR - time_Offset);
        calendar.set(Calendar.MINUTE, ALARM_MINUTE);
        calendar.set(Calendar.SECOND, ALARM_SECOND);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //set the alarm repeat one day

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //cancel the wifi if the user get the home wifi address
        if(WifiStorage.getUsersHomeWifiList(context)!=null){
            alarmManager.cancel(pendingIntent);
        }

    }
}
