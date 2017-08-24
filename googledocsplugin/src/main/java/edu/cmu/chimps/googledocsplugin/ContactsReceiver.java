package edu.cmu.chimps.googledocsplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsReceiver extends BroadcastReceiver {
    public static ArrayList<String> contactList;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Bundle bundle = intent.getBundleExtra("contacts");
        contactList = bundle.getStringArrayList("contacts");
        if (contactList != null) {
            Toast.makeText(context, contactList.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
