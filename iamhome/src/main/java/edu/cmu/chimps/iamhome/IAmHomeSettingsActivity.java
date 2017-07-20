package edu.cmu.chimps.iamhome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.cmu.chimps.iamhome.RecyView.Contact;
import edu.cmu.chimps.iamhome.RecyView.ContactAdapter;
import edu.cmu.chimps.iamhome.utils.AlarmUtils;
import edu.cmu.chimps.iamhome.utils.WifiUtils;

public class IAmHomeSettingsActivity extends AppCompatActivity {
    TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Select Contacts");
        //initialize contactlist from whatsapp
        Contact.contactList = Contact.getWhatsAppContacts(this);
        ContactAdapter adapter = new ContactAdapter(Contact.contactList, IAmHomeSettingsActivity.this);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        textView = (TextView)findViewById(R.id.textview);
        //set the alarm
//        AlarmUtils.setAlarm(this, 14,20,00);
//        startService(new Intent(this, IAmHomePlugin.class));
        AlarmReceiver alarmReceiver = new AlarmReceiver();
        alarmReceiver.createNotification(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> savedContactList = new ArrayList<>();
        for (int i = 0; i < Contact.contactList.size(); i++){
            if (Contact.contactList.get(i).isFlag()){
                savedContactList.add(Contact.contactList.get(i).getName());
            }
        }
        Toast.makeText(this, "Contacts saved" , Toast.LENGTH_SHORT).show();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < savedContactList.size(); i++){
            builder.append(savedContactList.get(i));
        }
        textView.setText(builder);
        return true;
    }
}
