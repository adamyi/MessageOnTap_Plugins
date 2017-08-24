package edu.cmu.chimps.starbucksplugin;

import android.content.Context;
import android.content.SharedPreferences;

public class ScriptStorage {
    public static final String KEY_POSITION = "send_script_position";
    public static final String KEY_STORAGE = "save_script_file";

    public static void storeScript(Context context, String scriptName){
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE).edit();

        editor.putString(KEY_POSITION, scriptName);
        editor.apply();
    }

    public static String getScript(Context context){
        SharedPreferences pref = context.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        return pref.getString(KEY_POSITION,"empty");
    }


}
