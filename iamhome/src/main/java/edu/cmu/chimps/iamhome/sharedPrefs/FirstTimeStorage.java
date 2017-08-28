package edu.cmu.chimps.iamhome.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeStorage {

    private static final String IDENTIFIER = "firt_time";
    private static final String IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL = "send";

    public static void setFirst(Context context, Boolean input) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER, input);
        editor.apply();
    }

    public static void setFirst(Context context, Boolean input, String stage) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER + stage, input);
        editor.apply();
    }

    public static boolean getFirst(Context context) {
        SharedPreferences isFirstTime = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE);
        return isFirstTime.getBoolean(IDENTIFIER, true);
    }

    public static void setContactActivityIndicatorSend(Context context, Boolean input) {
        SharedPreferences.Editor editor = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL, input);
        editor.apply();
    }

    public static boolean getIndicator(Context context) {
        SharedPreferences isSpecialActivity = context.getSharedPreferences(IDENTIFIER, Context.MODE_PRIVATE);
        return isSpecialActivity.getBoolean(IDENTIFIER_CONTACT_ACTIVITY_INDICATOR_SPECIAL, false);
    }
}
