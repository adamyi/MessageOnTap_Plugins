package edu.cmu.chimps.iamhome;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.iamhome.RecyView.Contact;
import edu.cmu.chimps.iamhome.RecyView.ContactAdapter;
import edu.cmu.chimps.iamhome.RecyView.ContactStorage;

import static android.app.PendingIntent.getService;

public class IAmHomeSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitle("Select Contacts");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorwhite));
        toolbar.setSubtitle("0 selected");

        toolbar.inflateMenu(R.menu.main);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                switch (menuItemId){
                    case R.id.checkMark:
                        ArrayList<String> savedContactList = new ArrayList<>();
                        for (int i = 0; i < Contact.contactList.size(); i++){
                            if (Contact.contactList.get(i).isFlag()){
                                savedContactList.add(Contact.contactList.get(i).getName());
                            }
                        }
                        Toast.makeText(getBaseContext(), "Contacts Saved" , Toast.LENGTH_SHORT).show();

                        Set<String> set = new HashSet<>(savedContactList);
                        ContactStorage.storeSendUsers(getBaseContext(), set);
                }

                return true;
            }
        });


        //initialize contactlist from whatsapp
        Contact.contactList = Contact.getWhatsAppContacts(this);
        ContactStorage.InitSelection(this);
        ContactAdapter adapter = new ContactAdapter(Contact.contactList, IAmHomeSettingsActivity.this, toolbar);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //set the alarm
//        AlarmUtils.setAlarm(this, 14,20,00);
        startService(new Intent(this, IAmHomePlugin.class));

        Button sendNotice = (Button) findViewById(R.id.button_notice);
        Button whatsApp = (Button) findViewById(R.id.button_WhatsApp);
        sendNotice.setOnClickListener(this);
        whatsApp.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> savedContactList = new ArrayList<>();
        for (int i = 0; i < Contact.contactList.size(); i++){
            if (Contact.contactList.get(i).isFlag()){
                savedContactList.add(Contact.contactList.get(i).getName());
            }
        }
        Toast.makeText(this, "Contacts Saved" , Toast.LENGTH_SHORT).show();

        Set<String> set = new HashSet<>(savedContactList);
        ContactStorage.storeSendUsers(this, set);

        return true;
    }


    public void onClick(View v) {

        Intent launchService = new Intent(this, ShareMessageService.class);

        switch (v.getId()) {

            case R.id.button_notice:
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("You just arrived home!")
                        .setContentText("Notify your contacts now!")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(getService(this, 0, launchService, 0))
                        .setAutoCancel(true)
                        .build();
                manager.notify(1, notification);
                break;

            case R.id.button_WhatsApp:
                startService(launchService);
                break;

            default:
                break;
        }

    }
 }

