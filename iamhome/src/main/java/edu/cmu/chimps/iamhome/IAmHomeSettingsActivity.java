package edu.cmu.chimps.iamhome;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.device.WifiAp;
import com.imangazaliev.circlemenu.CircleMenu;
import com.imangazaliev.circlemenu.CircleMenuButton;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.util.Timer;
import java.util.TimerTask;

import edu.cmu.chimps.iamhome.services.SaveHomeWifiService;
import edu.cmu.chimps.iamhome.services.ShareMessageService;
import edu.cmu.chimps.iamhome.sharedPrefs.FirstTimeStorage;
import edu.cmu.chimps.iamhome.sharedPrefs.StringStorage;
import edu.cmu.chimps.iamhome.utils.AutoSelectUtils;
import edu.cmu.chimps.iamhome.utils.StatusToastsUtils;
import edu.cmu.chimps.iamhome.utils.WifiUtils;

public class IAmHomeSettingsActivity extends AppCompatActivity {
    protected MyApplication mAPP;
    private String sentText;
    public String username;
    IAmHomePlugin userstatus = new IAmHomePlugin();

    Intent circleIntent = new Intent();
    boolean connected = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAPP = (MyApplication) this.getApplicationContext();

        UQI uqi = new UQI(this);
        uqi.getData(com.github.privacystreams.communication.Contact.getAll(), Purpose.UTILITY("test")).debug();
        setContentView(R.layout.welcome_page);
        if (FirstTimeStorage.getFirst(MyApplication.getContext())) {
            //Toast.makeText(MyApplication.getContext(), "This is I AM HOME Plugin", Toast.LENGTH_SHORT).show();
            StringStorage.storeMessage(MyApplication.getContext(), "", true);
        } else {
            //Log.e("test", Boolean.toString(AutoSelectUtils.hasPermission(MyApplication.getContext())));
        }

        /**
         * set user wifi status
         */
        uqi.getData(WifiAp.getUpdateStatus(), Purpose.UTILITY("check wifi status")).forEach(new Callback<Item>() {
            @Override
            protected void onInput(Item input) {

                if (WifiUtils.getUsersHomeWifiList(MyApplication.getContext()).contains(input.getValueByField(WifiAp.BSSID)) &&
                        input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_CONNECTED) ) {
                    TextView textView = (TextView) findViewById(R.id.textView3);
                    textView.setText("Connected WIFI: " + "\n" + input.getValueByField(WifiAp.SSID));
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_home_white_24px));
                    StatusToastsUtils.createAthomeNoti(MyApplication.getContext());


                } else if (input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_CONNECTED)) {
                    TextView textView = (TextView) findViewById(R.id.textView3);
                    textView.setText("Connected WIFI: " + "\n" + input.getValueByField(WifiAp.SSID));
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_work_white_24px));

                } else {
                    TextView textView = (TextView) findViewById(R.id.textView3);
                    textView.setText("Connected WIFI: \n" + "No Connection");
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_work_white_24px));
                }
            }
        });

        //Callback when the view is ready
        /**
         * tutorial steps, and the first time overview of using this application
         */
        final LinearLayout welcomePage = (LinearLayout) findViewById(R.id.welcome_page);
        ViewTreeObserver vto = welcomePage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //Detect whether it's first time
                if (FirstTimeStorage.getFirst(MyApplication.getContext())) {
                    welcomePage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    /**
                     * overview
                     */

                    //Tutorial
                    View homeView = findViewById(R.id.imageView);
                    int[] homeViewLocation = new int[2];
                    homeView.getLocationOnScreen(homeViewLocation);
                    float imageX = homeViewLocation[0] + homeView.getWidth() / 2f;
                    float imageY = homeViewLocation[1] + homeView.getHeight() / 2f;
                    // make an target
                    SimpleTarget homeViewTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(imageX, imageY)
                            .setRadius(200f)
                            .setTitle("Status Icon")
                            .setDescription("It indicates your \" HOME \" status")
                            .build();

                    SimpleTarget mainIntroTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(imageX, imageY)
                            .setRadius(1f)
                            .setTitle("Welcome to use \nI Am Home!")
                            .setDescription(getString(R.string.tutorial_main_intro))
                            .build();

                    SimpleTarget wifiTextTarget =
                            new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(findViewById(R.id.textView3))
                                    .setRadius(200f)
                                    .setTitle("Current Wi-Fi")
                                    .setDescription("It shows your device's connected Wi-Fi \n ")
                                    .build();

                    View circleMenuView = findViewById(R.id.circleMenu);
                    int[] circleMenuLocation = new int[2];
                    circleMenuView.getLocationInWindow(circleMenuLocation);
                    PointF point =
                            new PointF(circleMenuLocation[0] + circleMenuView.getWidth() / 2f, circleMenuLocation[1] + circleMenuView.getHeight() / 2f);
                    // make an target
                    SimpleTarget circleMenuViewTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(point)
                            .setRadius(160f)
                            .setTitle("Menu")
                            .setDescription("This is the menu button for settings! \n\nTry it out yourself!")
                            .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                                @Override
                                public void onStarted(SimpleTarget target) {
                                    //Toast.makeText(IAmHomeSettingsActivity.this, "target is started", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onEnded(SimpleTarget target) {
                                    //Toast.makeText(IAmHomeSettingsActivity.this, "target is ended", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .build();

                    Spotlight.with(IAmHomeSettingsActivity.this)
                            .setDuration(800L)
                            .setAnimation(new DecelerateInterpolator(2f))
                            .setTargets(mainIntroTarget, homeViewTarget, wifiTextTarget, circleMenuViewTarget)
                            .setOnSpotlightStartedListener(new OnSpotlightStartedListener() {
                                @Override
                                public void onStarted() {
                                    Toast.makeText(IAmHomeSettingsActivity.this, "Let's learn how to use this masterpiece", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setOnSpotlightEndedListener(new OnSpotlightEndedListener() {
                                @Override
                                public void onEnded() {
                                    //Toast.makeText(IAmHomeSettingsActivity.this, "spotlight is ended", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .start();
                    //

                    final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
                    circleMenu.setStateUpdateListener(new CircleMenu.OnStateUpdateListener() {
                        @Override

                        public void onMenuExpanded() {
                            if(FirstTimeStorage.getFirst(MyApplication.getContext())){
                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                View reset = findViewById(R.id.circle_reset_wifi);
                                                int[] imageLocation = new int[2];
                                                reset.getLocationOnScreen(imageLocation);
                                                float imageX = imageLocation[0] + reset.getWidth() / 2f;
                                                float imageY = imageLocation[1] + reset.getHeight() / 2f;

                                                // make an target
                                                SimpleTarget firstTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(imageX, imageY)
                                                        .setRadius(200f)
                                                        .setTitle("Reset")
                                                        .setDescription("Use this to reset your home WIFI")
                                                        .build();

                                                View two = findViewById(R.id.circle_edit_text);
                                                int[] twoLocation = new int[2];
                                                two.getLocationInWindow(twoLocation);
                                                PointF point =
                                                        new PointF(twoLocation[0] + two.getWidth() / 2f, twoLocation[1] + two.getHeight() / 2f);
                                                // make an target
                                                SimpleTarget thirdTarget = new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(point)
                                                        .setRadius(200f)
                                                        .setTitle("Edit")
                                                        .setDescription("Customize your at home message")
                                                        .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                                                            @Override
                                                            public void onStarted(SimpleTarget target) {

                                                            }

                                                            @Override
                                                            public void onEnded(SimpleTarget target) {

                                                            }
                                                        })
                                                        .build();

                                                SimpleTarget fourthTarget =
                                                        new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(findViewById(R.id.circle_contact_list))
                                                                .setRadius(200f)
                                                                .setTitle("Contacts")
                                                                .setDescription("Check your sending contact list")
                                                                .build();

                                                SimpleTarget secondTarget =
                                                        new SimpleTarget.Builder(IAmHomeSettingsActivity.this).setPoint(findViewById(R.id.circle_send_message))
                                                                .setRadius(200f)
                                                                .setTitle("Launch!")
                                                                .setDescription("One tap to tell your friends you are home")
                                                                .build();

                                                Spotlight.with(IAmHomeSettingsActivity.this)
                                                        .setDuration(1000L)
                                                        .setAnimation(new DecelerateInterpolator(2f))
                                                        .setTargets(firstTarget, secondTarget, thirdTarget, fourthTarget)
                                                        .setOnSpotlightStartedListener(new OnSpotlightStartedListener() {
                                                            @Override
                                                            public void onStarted() {
                                                            }
                                                        })
                                                        .setOnSpotlightEndedListener(new OnSpotlightEndedListener() {
                                                            @Override
                                                            public void onEnded() {
                                                                Toast.makeText(IAmHomeSettingsActivity.this, "You've learned how to use this masterpiece", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .start();
                                            }
                                        });
                                    }
                                }, 100);
                                FirstTimeStorage.setFirst(MyApplication.getContext(), false);
                            }
                        }


                        @Override
                        public void onMenuCollapsed() {
                            /**
                             * the menu is collapsed
                             */
                        }
                    });
                }
            }
        });
        uqi.getData(com.github.privacystreams.communication.Contact.getAll(), Purpose.UTILITY("test")).debug();


        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
        circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
            @Override
            public void onItemClick(CircleMenuButton menuButton) {
                /**
                 * Four buttons for actions;
                 */

                if (menuButton == menuButton.findViewById(R.id.circle_reset_wifi)) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    dialog.setTitle("Reset Home Wifi");
                    dialog.setMessage("Saved wifi will be replaced by the connected wifi.");
                    dialog.setPositiveButton("RESET TO CURRENT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Reset Wifi code here
                            Intent saveHomeWifiServiceIntent = new Intent(MyApplication.getContext(), SaveHomeWifiService.class);
                            saveHomeWifiServiceIntent.setAction(SaveHomeWifiService.ACTION_SAVE);
                            MyApplication.getContext().startService(saveHomeWifiServiceIntent);

                            ImageView imageView1 = (ImageView) findViewById(R.id.imageView);
                            imageView1.setImageDrawable(getDrawable(R.drawable.ic_home_white_24px));

                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                            sendBroadcast(closeNotificationDrawer);
                        }
                    });
                    dialog.show();
                }
                if (menuButton == menuButton.findViewById(R.id.circle_contact_list)) {
                    circleIntent = new Intent(MyApplication.getContext(), SelectContactActivity.class);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            startActivity(circleIntent);
                        }
                    }, 1170);
                }
                if (menuButton == menuButton.findViewById(R.id.circle_edit_text)) {
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
                    builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sentText = input.getText().toString();
                            StringStorage.storeMessage(IAmHomeSettingsActivity.this, sentText, false);
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
                if (menuButton == menuButton.findViewById(R.id.circle_send_message)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(IAmHomeSettingsActivity.this, R.style.myDialog));
                    //dialog.setTitle("Send Message");
                    dialog.setMessage("Current message:\n\n\"" + StringStorage.getMessage(MyApplication.getContext()) + "\"");
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
        startService(new Intent(this, IAmHomePlugin.class));
    }
    protected void onResume() {
        super.onResume();
        mAPP.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = mAPP.getCurrentActivity();
        if (this.equals(currActivity))
            mAPP.setCurrentActivity(null);
    }

   }

