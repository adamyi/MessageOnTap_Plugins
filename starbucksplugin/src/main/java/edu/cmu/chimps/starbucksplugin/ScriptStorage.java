package edu.cmu.chimps.starbucksplugin;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ScriptStorage {
    public static final String POSITION = "send_script_position";
    public static final String STORAGE = "save_script_file";

    public static void storeScript(Context context, String scriptName){
        SharedPreferences.Editor editor = context.getSharedPreferences(STORAGE, context.MODE_PRIVATE).edit();

        editor.putString(POSITION, scriptName);
        editor.apply();
    }

    public static String getScript(Context context){
        SharedPreferences pref = context.getSharedPreferences(STORAGE, context.MODE_PRIVATE);
        return pref.getString(POSITION,"empty");
    }


}
