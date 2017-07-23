package edu.cmu.chimps.iamhome.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ContactStorage {
    public static final String KEY_FOR_USER_WIFI = "send_contacts ";
    public static final String POSITION = "send_contacts_position";

    public static void storeSendUsers(Context context, Set<String>set){
        SharedPreferences.Editor editor = context.getSharedPreferences("contacts",context.MODE_PRIVATE).edit();
        editor.putStringSet(POSITION, set);
        editor.apply();
    }

    public static Set<String> getContacts(Context context){
        SharedPreferences pref = context.getSharedPreferences("contacts", context.MODE_PRIVATE);
        return pref.getStringSet(POSITION, new HashSet<String>());
    }


}
