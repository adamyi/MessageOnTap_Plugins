package edu.cmu.chimps.iamhome;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.privacystreams.core.exceptions.PSException;

import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.iamhome.listeners.IconChangeListener;
import edu.cmu.chimps.iamhome.sharedPrefs.ContactStorage;
import edu.cmu.chimps.iamhome.sharedPrefs.FirstTimeStorage;
import edu.cmu.chimps.iamhome.views.Contact;
import edu.cmu.chimps.iamhome.views.ContactAdapter;


public class SelectContactActivity extends AppCompatActivity {
    protected MyApplication mAPP;
    private int BackPressedCount;
    Toast updatableToast;
    public static IconChangeListener iconChangeListener;

    public static void setIconChangeListener(IconChangeListener icl) {
        iconChangeListener = icl;
    }

    @Override
    public void onBackPressed() {

        if (BackPressedCount == 0) {
            if (updatableToast != null) {
                updatableToast.cancel();
            }
            updatableToast = Toast.makeText(SelectContactActivity.this, "Click again to cancel the change", Toast.LENGTH_SHORT);
            updatableToast.show();
            BackPressedCount++;
        } else if (BackPressedCount == 1) {
            if (updatableToast != null) {
                updatableToast.cancel();
            }
            updatableToast = Toast.makeText(SelectContactActivity.this, "Change canceled", Toast.LENGTH_SHORT);
            updatableToast.show();
            super.onBackPressed();
        } else {
            Set<String> set = new HashSet<>(Contact.getSavedContactList());
            ContactStorage.storeSendUsers(SelectContactActivity.this, set, ContactStorage.STORAGE);
            if (updatableToast != null) {
                updatableToast.cancel();
            }
            updatableToast = Toast.makeText(SelectContactActivity.this, "Contacts saved", Toast.LENGTH_SHORT);
            updatableToast.show();
            super.onBackPressed();
        }

    }

    Toolbar toolbar;
    RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPP = (MyApplication) this.getApplicationContext();

        BackPressedCount = 0;


        //initialize contactlist from whatsapp
        try {
            Contact.contactList = Contact.getWhatsAppContacts(this);
        } catch (PSException e) {
            e.printStackTrace();
        }
        Contact.InitFlag(this, ContactStorage.STORAGE);

        //Initialize UI
        setContentView(R.layout.activity_contact_select);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitle("Select Contacts");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorwhite));
        toolbar.setSubtitle(" " + Contact.SelectedItemCount() + " selected");
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorwhite));
        toolbar.inflateMenu(R.menu.selectall);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getBaseContext(), "Contacts Saved" , Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        setIconChangeListener(new IconChangeListener() {
            public void onChange(Boolean wantChange) {
                Log.e("test", "Listener recieved");
                MenuItem i = toolbar.getMenu().getItem(0);
                if (wantChange) {
                    i.setIcon(getDrawable(R.drawable.ic_delete_sweep_black_24dp));
                } else {
                    i.setIcon(getDrawable(R.drawable.ic_action_selectall));
                }
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                switch (menuItemId) {
                    case R.id.selectAll:
                        if (Contact.SelectedItemCount() == Contact.contactList.size()) {
                            item.setIcon(getDrawable(R.drawable.ic_action_selectall));
                            ContactAdapter.SetAllSelection(false, recyclerView);
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Deselect All", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            item.setIcon(getDrawable(R.drawable.ic_delete_sweep_black_24dp));
                            Set<String> set = new HashSet<>(Contact.getSavedContactList());
                            ContactStorage.storeSendUsers(getBaseContext(), set, ContactStorage.ALLSELECTSTORAGE);
                            ContactAdapter.SetAllSelection(true, recyclerView);
                            final MenuItem itemP = item;
                            Snackbar undoSnackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Select All", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            itemP.setIcon(getDrawable(R.drawable.ic_action_selectall));
                                            Contact.InitFlag(SelectContactActivity.this, ContactStorage.ALLSELECTSTORAGE);
                                            ContactAdapter.SetAllSavedSelection(recyclerView);
                                            toolbar.setSubtitle(" " + Contact.SelectedItemCount() + " selected");
                                        }
                                    });

                            undoSnackbar.show();
                        }
                        toolbar.setSubtitle(" " + Contact.SelectedItemCount() + " selected");
                        //Toast.makeText(getBaseContext(), "Select All" , Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        ContactAdapter adapter = new ContactAdapter(Contact.contactList, toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        FloatingActionButton floatingUndefinedButton = (FloatingActionButton) findViewById(R.id.floatingUndefinedAction);
        if (FirstTimeStorage.getIndicator(MyApplication.getContext())) {
            //Toast.makeText(MyApplication.getContext(), "Send Botton", Toast.LENGTH_SHORT).show();
            floatingUndefinedButton.setImageResource(R.drawable.ic_action_send);
            floatingUndefinedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackPressedCount = 2;
                    onBackPressed();
                    Intent startSessionIntent = new Intent("Session On Start");
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(startSessionIntent);
                }
            });
        } else {
            //Toast.makeText(MyApplication.getContext(), "Save Botton", Toast.LENGTH_SHORT).show();
            floatingUndefinedButton.setImageResource(R.drawable.ic_action_check);
            floatingUndefinedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackPressedCount = 2;
                    onBackPressed();
                }
            });
        }

        FirstTimeStorage.setContactActivityIndicatorSend(MyApplication.getContext(), false);

        //set the alarm
        // AlarmUtils.setAlarm(this, 14,20,00);
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




