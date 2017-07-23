package edu.cmu.chimps.iamhome.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class TimeStorage {

    public static final String IDENTIFIER_ALARM = "alarm_send_time";
    public static final String IDENTIFIER_NOTIFICATION = "notification_send_time";
    private List<Integer> storedTimeimeList;

    public static void storeTime(Context context, String identifier, List<Integer> timeList) {
        SharedPreferences.Editor editor = context.getSharedPreferences(identifier, context.MODE_PRIVATE).edit();
        editor.putInt("Hour", timeList.get(0));
        editor.putInt("Minute", timeList.get(1));
        if (timeList.size() < 2) {
            editor.putInt("Second", 0);
        } else {
            editor.putInt("Minute", timeList.get(3));
        }
        editor.apply();
    }

    public static ArrayList<Integer> getTime(Context context, String identifier) {
        SharedPreferences time = context.getSharedPreferences(identifier, context.MODE_PRIVATE);
        Integer hour = time.getInt("Hour", 0);
        Integer minute = time.getInt("Minute", 0);
        Integer second = time.getInt("Second", 0);
        return toList(hour, minute, second);
    }

    public static ArrayList<Integer> toList(Integer hour, Integer minute) {
        ArrayList<Integer> outputTimeList = new ArrayList<Integer>();
        outputTimeList.add(hour);
        outputTimeList.add(minute);
        return outputTimeList;
    }

    public static ArrayList<Integer> toList(Integer hour, Integer minute, Integer second) {
        ArrayList<Integer> outputTimeList = new  ArrayList<Integer>();
        outputTimeList.add(hour);
        outputTimeList.add(minute);
        outputTimeList.add(second);
        return outputTimeList;
    }

    public boolean hasTimeData (String identifier) {
        return false;
    }
}
