package edu.cmu.chimps.iamhome.sharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeStorage {

    private static final String IDENTIFIER = "firt_time";
    private static final String IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL = "send";

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

    public static void setContactActivityIndicatorSend(Context context, Boolean input) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL, input);
        editor.apply();
    }

    public static boolean getIndicator(Context context) {
        SharedPreferences isSpecialActivity = context.getSharedPreferences(IDENTIFIER, context.MODE_PRIVATE);
        Boolean specialIndicator = isSpecialActivity.getBoolean(IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL, false);
        return specialIndicator;
    }
}
