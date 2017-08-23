package edu.cmu.chimps.starbucksplugin;

import android.content.Context;
import android.content.SharedPreferences;

public class ScriptStorage {
    public static final String POSITION = "send_script_position";
    public static final String STORAGE = "save_script_file";

    public static void storeScript(Context context, String scriptName){
        SharedPreferences.Editor editor = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE).edit();

        editor.putString(POSITION, scriptName);
        editor.apply();
    }

    public static String getScript(Context context){
        SharedPreferences pref = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return pref.getString(POSITION,"empty");
    }


}
