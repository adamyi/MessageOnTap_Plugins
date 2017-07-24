package edu.cmu.chimps.iamhome;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.privacystreams.core.exceptions.PSException;

import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.iamhome.views.Contact;
import edu.cmu.chimps.iamhome.views.ContactAdapter;
import edu.cmu.chimps.iamhome.sharedPrefs.ContactStorage;
import edu.cmu.chimps.iamhome.sharedPrefs.FirstTimeStorage;
import edu.cmu.chimps.iamhome.services.ShareMessageService;


public class SelectContactActivity extends AppCompatActivity {

    private int BackPressedCount;
    Toast updatableToast;

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
        BackPressedCount = 0;
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

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                switch (menuItemId) {
                    case R.id.selectAll:
                        if (Contact.SelectedItemCount() == Contact.contactList.size()) {
                            ContactAdapter.SetAllSelection(false, recyclerView);
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Deselect All", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            Set<String> set = new HashSet<>(Contact.getSavedContactList());
                            ContactStorage.storeSendUsers(getBaseContext(), set, ContactStorage.ALLSELECTSTORAGE);
                            ContactAdapter.SetAllSelection(true, recyclerView);
                            Snackbar undoSnackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Select All", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Contact.InitSelection(SelectContactActivity.this, ContactStorage.ALLSELECTSTORAGE);
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


        //initialize contactlist from whatsapp
        try {
            Contact.contactList = Contact.getWhatsAppContacts(this);
        } catch (PSException e) {
            e.printStackTrace();
        }
        Contact.InitSelection(this, ContactStorage.STORAGE);
        ContactAdapter adapter = new ContactAdapter(Contact.contactList, toolbar);

        //Initialize UI
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
                    Intent launchService = new Intent(MyApplication.getContext(), ShareMessageService.class);
                    startService(launchService);
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
//        FloatingActionButton fabCheck = (FloatingActionButton) findViewById(R.id.fabCheck);
//        FloatingActionButton fabSend  = (FloatingActionButton) findViewById(R.id.fabSend);
//        fabCheck.setOnClickListener(this);
//        fabSend.setOnClickListener(this);
        //set the alarm
//        AlarmUtils.setAlarm(this, 14,20,00);
        startService(new Intent(this, IAmHomePlugin.class));
    }

}




