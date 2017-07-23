package edu.cmu.chimps.iamhome;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import java.util.Timer;
import java.util.TimerTask;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import edu.cmu.chimps.iamhome.SharedPrefs.StringStorage;





public class IAmHomeSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private String sentText;
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
        if (userstatus.isAtHome()) {
            textView.setText("Welcome Home");
        } else {
            textView.setText("Not At Home");
        }

        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);

        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                /**
                 * Four buttons for actions;
                 */

                if (menuButton == menuButton.findViewById(R.id.alert)) {


                }
                if (menuButton == menuButton.findViewById(R.id.search)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    dialog.setTitle("Reset Home Wifi");
                    dialog.setMessage("Saved wifi will be replaced by the connected wifi");
                    dialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Reset Wifi code here

                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                            sendBroadcast(closeNotificationDrawer);
                        }
                    });
                    dialog.show();
                }
                if (menuButton == menuButton.findViewById(R.id.favorite)) {
                    circleIntent = new Intent(MyApplication.getContext(), SelectContactActivity.class);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(circleIntent);
                        }
                    }, 1170);
                }
                if (menuButton == menuButton.findViewById(R.id.edit)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
//                            AlertDialog.Builder builder = new AlertDialog.Builder(MyApplication.getContext());
                            builder.setTitle("Set message to send");
                            final EditText input = new EditText(MyApplication.getContext());
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            input.setHint(StringStorage.getMessage(getBaseContext()));

                            input.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view) {
                                    input.setHint(null);
                                }
                            });
                            builder.setView(input);
                            builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sentText = input.getText().toString();
                                    StringStorage.storeMessage(IAmHomeSettingsActivity.this, sentText);
                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                            Log.i("diaglog", "ok");
                }


            }
        });

        circleMenu.setStateUpdateListener(new CircleMenu.OnStateUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Log.i("expaned", "circle menu expanded");
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_contact:
                Intent setContactIntent = new Intent(this, SelectContactActivity.class);
                startActivity(setContactIntent);
                //Toast.makeText(this, "set contact", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rest_wifi:
                //Toast.makeText(this, "reset wifi", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Reset Home Wifi");
                dialog.setMessage("Saved wifi will be replaced by the connected wifi");
                dialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Reset Wifi code here

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(closeNotificationDrawer);
                    }
                });
                dialog.show();
                break;
            case R.id.ser_default_message:
                //Toast.makeText(this, "set message", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set message to send");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint(StringStorage.getMessage(getBaseContext()));
                input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        input.setHint(null);
                    }
                });
                builder.setView(input);
                builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sentText = input.getText().toString();
                        StringStorage.storeMessage(getBaseContext(), sentText);
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onClick(View view) {

    }
}

