package edu.cmu.chimps.googledocsplugin;

import android.content.Context;
import android.graphics.Color;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.exceptions.PSException;
import com.github.privacystreams.core.purposes.Purpose;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Contact {
    private String mName;
    private boolean mIsFlag;
    public static ArrayList<Contact> contactList =  new ArrayList<>();

    public Contact(String Name){
        this.mName = Name;
    }

    public String getName() {
        return mName;
    }

    public boolean isFlag(){
        return mIsFlag;
    }

    public void setFlag(boolean flag){
        mIsFlag = flag;
    }

    public static ArrayList<Contact> getWhatsAppContacts(Context context) throws PSException {

        UQI uqi = new UQI(context);
        ArrayList<Contact> results = new ArrayList<>();
        List<Item> whatsAppContactList= uqi.getData(com.github.privacystreams.communication.Contact.getWhatAppAll(), Purpose.UTILITY("get whatsapp contacts"))
                .asList();
        for(int i = 0; i < whatsAppContactList.size();i++){
            Contact contact = new Contact(whatsAppContactList.get(i).getValueByField(com.github.privacystreams.communication.Contact.NAME)
                    .toString());
            results.add(i, contact);

        }
        return results;
    }

    public char getFirstContact(){
        return this.mName.charAt(0);
    }
    public TextDrawable getContactPicture(){
        return TextDrawable.builder()
                .buildRound(String.valueOf(getFirstContact()), Color.GRAY);
    }

    public static int selectedItemCount(){
        int count = 0;
        for (int i=0; i<contactList.size(); i++){
            if (contactList.get(i).isFlag()){
                count++;
            }
        }
        return count;
    }

    public static void setAllFlags(Boolean flag){
        for (int i = 0; i < contactList.size(); i++) {
            contactList.get(i).setFlag(flag);
        }
    }

    public static void toggleFlag(Contact contact){
        if (contact.isFlag()){
            contact.setFlag(false);
        } else {
            contact.setFlag(true);
        }
    }

    public static ArrayList<String> getSavedContactList(){
        ArrayList<String> savedContactList = new ArrayList<>();
        for (int i = 0; i < Contact.contactList.size(); i++){
            if (Contact.contactList.get(i).isFlag()){
                savedContactList.add(Contact.contactList.get(i).getName());
            }
        }
        return savedContactList;
    }

    public static void initFlag(Context context, String filename){
        Set<String> set = ContactStorage.getContacts(context, filename);
        setAllFlags(false);
        if (set.size() != 0){
            for (String str: set){
                for (Contact contact: Contact.contactList){
                    if (str.equals(contact.getName())){
                        contact.setFlag(true);
                        //Toast.makeText(context, "selecte completed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}
