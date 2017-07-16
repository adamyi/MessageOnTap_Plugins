package com.everbright.wangyusen.contacs_app;



import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.OperationApplicationException;


import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.RemoteException;

import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.github.privacystreams.core.exceptions.PSException;



public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public Button mButton;
    public Boolean is_connectedWifi;
    public String bssid;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);
        mButton = (Button) findViewById(R.id.button3);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTask().execute();
            }
        });

        //alarm broadcaster
//        Calendar calendar = Calendar.getInstance();
//
//        calendar.set(Calendar.MONTH, 7);
//        calendar.set(Calendar.YEAR, 2017);
//        calendar.set(Calendar.DAY_OF_MONTH, 5);
//        calendar.set(Calendar.HOUR_OF_DAY, 6);
//        calendar.set(Calendar.MINUTE, 32);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.AM_PM,Calendar.PM);
//
//        Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent,0);
//
//        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

        //AlarmSetting.fireAlarm(this,10,16);
        WifiStatus wifistatus = new WifiStatus(this);

        wifistatus.ifWifiStatusChange();



//            notification();
//            Log.i("wasd", WifiStorage.getUsersHomewifi(getApplicationContext()));



    }

    private class MyAsyncTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object[] objects) {
            WIFItest wifi = new WIFItest(getApplicationContext());
            try {
                bssid = wifi.getWIFI_BSSID();
            } catch (PSException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(bssid);
        }
    }

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) throws PSException, RemoteException, OperationApplicationException {
        TextView textView = (TextView) findViewById(R.id.textView);
        WIFItest wifItest = new WIFItest(getApplicationContext());
        textView.setText(wifItest.getWIFI_BSSID());
//        Contacts s = new Contacts(getApplicationContext());
//
//        s.getContacts_name();
//        s.getContacts_id();
//
//        s.updateContactsEmail(getApplicationContext(), "6", "success@");
//        s.updateContactsPhone(getApplicationContext(), "6", "13613215058");
//        textView.setText(s.getDisplayName() + s.getDisplayID());


    }


//    public void testCalender(View view) throws PSException {
//        TextView textView = (TextView) findViewById(R.id.textView);
//
//        //Calender a = new Calender(getApplicationContext());
//
//        //textView.setText(a.getCalendareventID("Kickboxing"));
//
//        //  a.UpdateCalendarEntry(getContentResolver());
//
//        //startActivity(Calender.addCalendar_event(2017,7,1,730,830,"wow"));
//
//        //startActivity(Calender.UpdateCalendarEntry());
//
//        //textView.setText("adding events success");
//
//        //a.addEvents(getContentResolver(),getApplicationContext(), 2017, 1,8, 730,830,"kk");
//
//        //a.updateEvents(getContentResolver(), "woooo");
//        //PendingIntent intent = new PendingIntent(PendingIntent.);
//
//
//        //textView.setText("testing_wifi");
//
//
//
//        }

    public void notification() {

        Intent iAction1 = new Intent(getApplicationContext(), SaveHomeWifiService.class);
        iAction1.setAction(SaveHomeWifiService.ACTION1);
        PendingIntent piAction1 = PendingIntent.getService(getApplicationContext(), 0, iAction1, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_arrow)
                        .setContentTitle("My notification")
                        .setContentText("Are you currently at home?").setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .addAction(R.drawable.ic_arrow, "YES", piAction1)
                        .addAction(R.drawable.ic_arrow, "No", null);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void fireAlarm(int Month, int Day, int Hour, int Min) {
        Calendar calendar = Calendar.getInstance();
        Calendar right_now = Calendar.getInstance();
        calendar.set(Calendar.HOUR, right_now.get(Calendar.HOUR));

        int time_Offset = Hour - right_now.get(Calendar.HOUR);
        calendar.set(Calendar.YEAR, right_now.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, Month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Day);
        calendar.set(Calendar.HOUR, Hour - time_Offset);
        calendar.set(Calendar.MINUTE, Min);
        calendar.set(Calendar.SECOND, 0);

        //used for debug
        Log.i("actual", String.valueOf(right_now.getTime()));
        Log.i("settingtime", String.valueOf(calendar.getTime()));

        Intent intent = new Intent(this, AlarmReciver.class);
        PendingIntent penintent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        AlarmManager alm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), penintent);
    }




}