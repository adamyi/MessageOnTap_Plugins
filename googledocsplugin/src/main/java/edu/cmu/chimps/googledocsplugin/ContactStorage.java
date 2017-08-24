package edu.cmu.chimps.googledocsplugin;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ContactStorage {
    public static final String KEY_POSITION = "send_contacts_position";
    public static final String KEY_STORAGE = "save_contacts_file";
    public static final String KEY_ALL_SELECT_STORAGE = "save_contacts_file";

    public static void storeSendUsers(Context context, Set<String> set, String filename){
        SharedPreferences.Editor editor = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();
        editor.putStringSet(KEY_POSITION, set);
        editor.apply();
    }

    public static Set<String> getContacts(Context context, String filename){
        SharedPreferences pref = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return pref.getStringSet(KEY_POSITION, new HashSet<String>());
    }


}
