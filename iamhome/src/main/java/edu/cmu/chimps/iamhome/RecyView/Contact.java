package edu.cmu.chimps.iamhome.RecyView;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

/**
 * Created by knight006 on 7/18/2017.
 */

public class Contact {
    private String Name;
  //  private int imageId;
    private boolean isFlag;
    public static ArrayList<Contact> contactList =  new ArrayList<>();
    public Contact(String Name){
        this.Name = Name;
    }


    public String getName() {
        return Name;
    }


    public boolean isFlag(){
        return isFlag;
    }

    public void setFlag(boolean flag){
        isFlag = flag;
    }

    public static ArrayList<Contact> getWhatsAppContacts(Context context){
        Cursor c = context.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[] { ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY },
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[] { "com.whatsapp" },
                null);

        ArrayList<Contact> whatsAppContacts = new ArrayList<>();
        int contactNameColumn;
        if (c != null) {
            contactNameColumn = c.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);
            while (c.moveToNext())
            {
                // You can also read RawContacts.CONTACT_ID to read the
                // ContactsContract.Contacts table or any of the other related ones.
                whatsAppContacts.add(new Contact(c.getString(contactNameColumn)));
            }
            c.close();
        }
        return whatsAppContacts;
    }
    public char getFirstC(){
        return this.Name.charAt(0);
    }
    public TextDrawable getContactPicture(){
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(getFirstC()), Color.GRAY);
        return drawable;


    }
}
