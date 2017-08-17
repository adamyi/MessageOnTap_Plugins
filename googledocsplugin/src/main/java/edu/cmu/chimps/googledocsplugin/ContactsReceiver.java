package edu.cmu.chimps.googledocsplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsReceiver extends BroadcastReceiver {
    String TAG = "receiver";
    public static ArrayList<String> contactList;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Bundle bundle = intent.getBundleExtra("contacts");
        contactList = bundle.getStringArrayList("contacts");
        Log.e(TAG, "onReceive: " +contactList.toString());
        Toast.makeText(context, contactList.toString(), Toast.LENGTH_SHORT).show();
    }
}
