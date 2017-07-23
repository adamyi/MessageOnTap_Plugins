package edu.cmu.chimps.iamhome;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.privacystreams.core.UQI;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import edu.cmu.chimps.iamhome.RecyView.Contact;
import edu.cmu.chimps.iamhome.RecyView.ContactAdapter;
import edu.cmu.chimps.iamhome.RecyView.ContactStorage;
import edu.cmu.chimps.iamhome.utils.WifiUtils;

import static android.app.PendingIntent.getService;

public class IAmHomeSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    IAmHomePlugin userstatus = new IAmHomePlugin();



    Intent circleIntent = new Intent();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.READ_CONTACTS},
//               0);
        setContentView(R.layout.welcome_page);

//        setTitle("Select Contacts");
        TextView textView = (TextView) findViewById(R.id.athomeView);
        /**
         * Test whether the user is at home
         */
        if(userstatus.isAtHome()){
            textView.setText("Welcome Home");
        }
        else{
            textView.setText("Not At Home");
        }

        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);

        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                /**
                 * Four buttons for actions;
                 */

                if (menuButton == menuButton.findViewById(R.id.alert)  ) {
                   circleIntent = new Intent(MyApplication.getContext(), ListActivity.class);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(circleIntent);
                        }
                    }, 1170);

                }
                if(menuButton == menuButton.findViewById(R.id.edit)){
                    circleIntent = new Intent(MyApplication.getContext(), ListActivity.class);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(circleIntent);
                        }
                    }, 1170);
                }
                if(menuButton == menuButton.findViewById(R.id.favorite)){
                    circleIntent = new Intent(MyApplication.getContext(), ListActivity.class);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(circleIntent);
                        }
                    }, 1170);
                }
                if(menuButton == menuButton.findViewById(R.id.search)){
                    circleIntent = new Intent(MyApplication.getContext(), ListActivity.class);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(circleIntent);
                        }
                    }, 1170);
                }


            }
        });

        circleMenu.setStateUpdateListener(new CircleMenu.OnStateUpdateListener() {
            @Override
            public void onMenuExpanded() {
            Log.i("expaned","circle menu expanded");
            }

            @Override
            public void onMenuCollapsed() {
                Log.i("collapsed", "circle menu collapsed");

            }

        });




            //initialize contactlist from whatsapp
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_CONTACTS)
//                == PackageManager.PERMISSION_GRANTED) {
//
//        Contact.contactList = Contact.getWhatsAppContacts(this);
//        ContactStorage.InitSelection(this);
//        ContactAdapter adapter = new ContactAdapter(Contact.contactList, IAmHomeSettingsActivity.this);
//        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);}
//
//        //set the alarm
////        AlarmUtils.setAlarm(this, 14,20,00);
//        startService(new Intent(this, IAmHomePlugin.class));
//
//        Button sendNotice = (Button) findViewById(R.id.button_notice);
//        Button whatsApp = (Button) findViewById(R.id.button_WhatsApp);
//        sendNotice.setOnClickListener(this);
//        whatsApp.setOnClickListener(this);


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
        //Toast.makeText(this, "Contacts saved" , Toast.LENGTH_SHORT).show();

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
                        .setPriority(Notification.PRIORITY_MAX) //为什么不work????
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

