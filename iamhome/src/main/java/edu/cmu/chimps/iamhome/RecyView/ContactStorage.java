package edu.cmu.chimps.iamhome.RecyView;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by knight006 on 7/20/2017.
 */

public class ContactStorage {
    public static final String KEY_FOR_USER_WIFI = "send_contacts ";
    public static final String POSITION = "send_contacts_position";

    public static void storeSendUsers(Context context, Set<String>set){
        SharedPreferences.Editor editor = context.getSharedPreferences("contacts",context.MODE_PRIVATE).edit();
        editor.putStringSet(POSITION, set);
        editor.commit();
    }




    public static Set<String> getContacts(Context context){
        SharedPreferences pref = context.getSharedPreferences("get_contacts", context.MODE_PRIVATE);

        return pref.getStringSet(POSITION, new HashSet<String>());
    }

    public static void InitSelection(Context context){
        Set<String> set = ContactStorage.getContacts(context);
        for (String str: set){
            for (Contact contact: Contact.contactList){
                if (str.equals(contact.getName())){
                    contact.setFlag(true);
                    //Toast.makeText(context, "selected completed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
