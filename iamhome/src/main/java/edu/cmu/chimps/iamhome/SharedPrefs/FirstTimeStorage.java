package edu.cmu.chimps.iamhome.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeStorage {

    private static final String IDENTIFIER = "firt_time";
    private static boolean isFirstTime;

    public static void setFirst(Context context, Boolean input) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER, input);
        editor.apply();
    }

    public static boolean getFirst(Context context) {
        SharedPreferences isFirstTime = context.getSharedPreferences(IDENTIFIER, context.MODE_PRIVATE);
        Boolean nowTime = isFirstTime.getBoolean(IDENTIFIER, true);
        return nowTime;
    }

}
