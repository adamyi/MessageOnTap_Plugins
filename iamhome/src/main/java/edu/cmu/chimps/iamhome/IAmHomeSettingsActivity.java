package edu.cmu.chimps.iamhome;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
<<<<<<< HEAD
import android.widget.Toast;

=======
import android.widget.ImageView;
import android.widget.TextView;

import com.github.privacystreams.core.exceptions.PSException;
>>>>>>> cdd9b466d813fa85417fb0dce87a6e617d57c0ac
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;

import java.util.Timer;
import java.util.TimerTask;
<<<<<<< HEAD

import edu.cmu.chimps.iamhome.SharedPrefs.StringStorage;
import edu.cmu.chimps.iamhome.services.ShareMessageService;

import static edu.cmu.chimps.iamhome.utils.AutoSelectUtils.hasAccPermission;
=======
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import edu.cmu.chimps.iamhome.SharedPrefs.StringStorage;
import edu.cmu.chimps.iamhome.utils.WifiUtils;
>>>>>>> cdd9b466d813fa85417fb0dce87a6e617d57c0ac


public class IAmHomeSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private String sentText;
    IAmHomePlugin userstatus = new IAmHomePlugin();

    Intent circleIntent = new Intent();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_page);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);



<<<<<<< HEAD
=======
//        setTitle("Select Contacts");

        /**
         * Test whether the user is at home
         */
        if (userstatus.isAtHome()) {
            Drawable drawable = getDrawable(R.drawable.ic_work_black_24dp);
            imageView.setImageDrawable(drawable);
        } else {
            Drawable drawable = getDrawable(R.drawable.ic_home_black_24dp);
            imageView.setImageDrawable(drawable);

        }

>>>>>>> cdd9b466d813fa85417fb0dce87a6e617d57c0ac
        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);

        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                /**
                 * Four buttons for actions;
                 */

                if (menuButton == menuButton.findViewById(R.id.search)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    dialog.setTitle("Reset Home Wifi");
                    dialog.setMessage("Saved wifi will be replaced by the connected wifi");
                    dialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Reset Wifi code here
                            new LongOperation().execute(" ");
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
                    builder.setTitle("Set message to send");
                    final EditText input = new EditText(MyApplication.getContext());
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
                if (menuButton == menuButton.findViewById(R.id.explorer)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    dialog.setTitle("Send message now!");
                    dialog.setMessage("Send your message: \n\"" + StringStorage.getMessage(MyApplication.getContext()) + "\"\n to your friends!");
                    dialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                Intent launchService = new Intent(MyApplication.getContext(), ShareMessageService.class);
                                startService(launchService);
                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                            MyApplication.getContext().sendBroadcast(closeNotificationDrawer);
                        }
                    });
                    dialog.show();
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
<<<<<<< HEAD
=======

        startService(new Intent(this, IAmHomePlugin.class));


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                WifiUtils.storeUsersHomeWifi(MyApplication.getContext());
                Log.i("test", "stored");
                Log.i("get", String.valueOf(WifiUtils.getUsersHomeWifiList(MyApplication.getContext())));
            } catch (PSException e) {
                e.printStackTrace();
            }
            return "Executed";
        }
    }
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
>>>>>>> cdd9b466d813fa85417fb0dce87a6e617d57c0ac
    }

    @Override
    public void onClick(View view) {

    }
}

