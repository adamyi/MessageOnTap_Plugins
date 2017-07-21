package edu.cmu.chimps.iamhome.RecyView;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactStorage {
    public static final String KEY_FOR_USER_WIFI = "send_contacts ";
    public static final String POSITION = "send_contacts_position";
    public ArrayList<String> savedContactList = new ArrayList<>();
    public static void storeSendUsers(Context context){
        Set<String> set = new HashSet<>();
        SharedPreferences.Editor editor = context.getSharedPreferences("set_contacts",context.MODE_PRIVATE).edit();
        editor.putStringSet("contacts", set);
        editor.commit();
    }
    public static Set<String> getContacts(Context context){
        SharedPreferences pref = context.getSharedPreferences("get_contacts", context.MODE_PRIVATE);
        return pref.getStringSet(POSITION, new HashSet<String>());
    }
}
