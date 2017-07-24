package edu.cmu.chimps.iamhome.sharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ContactStorage {
    public static final String KEY_FOR_USER_WIFI = "send_contacts ";
    public static final String POSITION = "send_contacts_position";
    public static final String STORAGE = "save_contacts_file";
    public static final String ALLSELECTSTORAGE = "save_contacts_file";

    public static void storeSendUsers(Context context, Set<String>set, String filename){
        SharedPreferences.Editor editor = context.getSharedPreferences(filename, context.MODE_PRIVATE).edit();
        editor.putStringSet(POSITION, set);
        editor.apply();
        if (FirstTimeStorage.getFirst(context)) {
            FirstTimeStorage.setFirst(context, false);
        }
    }

    public static Set<String> getContacts(Context context, String filename){
        SharedPreferences pref = context.getSharedPreferences(filename, context.MODE_PRIVATE);
        return pref.getStringSet(POSITION, new HashSet<String>());
    }


}
