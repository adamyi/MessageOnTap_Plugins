package edu.cmu.chimps.googledocsplugin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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


public class GoogleDocsSettingsActivity extends AppCompatActivity {
    public static String TAG = "GoogleDocActivity";
    Toolbar mToolBar;
    RecyclerView recyclerView;
    private int mBackPressedCount;
    public static IconChangeListener iconChangeListener;

    public static void setIconChangeListener(IconChangeListener icl) {
        iconChangeListener = icl;
    }

    @Override
    public void onBackPressed() {

        if (mBackPressedCount == 0) {
            Toast.makeText(GoogleDocsSettingsActivity.this, "Click again to cancel the change", Toast.LENGTH_SHORT).show();
            mBackPressedCount++;
        } else if (mBackPressedCount == 1) {
            Toast.makeText(GoogleDocsSettingsActivity.this, "Change canceled", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult:     get result " );
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK && null != data) {
                    String text = data.getStringExtra("result");
                    Log.e(TAG, "onActivityResult:     get result"+ text);
                    GoogleDocsSettingsActivity.super.onBackPressed();
                }
                break;
            }
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);

        //initialize contactlist from whatsapp
        try {
            Contact.contactList = Contact.getWhatsAppContacts(this);
        } catch (PSException e) {
            e.printStackTrace();
        }
        Contact.initFlag(this, ContactStorage.KEY_STORAGE);

        //Initialize UI
        setContentView(R.layout.activity_contact_select);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        //mToolBar.setNavigationIcon(R.drawable.ic_action_back);
        mToolBar.setTitle("Select Contacts");
        mToolBar.setTitleTextColor(getResources().getColor(R.color.colorwhite));
        mToolBar.setSubtitle(" " + Contact.selectedItemCount() + " selected");
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.colorwhite));
        mToolBar.inflateMenu(R.menu.selectall);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getBaseContext(), "Contacts Saved" , Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        setIconChangeListener(new IconChangeListener() {
            public void onChange(Boolean wantChange) {
                Log.e("test", "Listener recieved");
                MenuItem i = mToolBar.getMenu().getItem(0);
                if (wantChange) {
                    i.setIcon(getDrawable(R.drawable.ic_delete_sweep_black_24dp));
                } else {
                    i.setIcon(getDrawable(R.drawable.ic_action_selectall));
                }
            }
        });

        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                switch (menuItemId) {
                    case R.id.selectAll:
                        if (Contact.selectedItemCount() == Contact.contactList.size()) {
                            item.setIcon(getDrawable(R.drawable.ic_action_selectall));
                            ContactAdapter.setAllSelections(false, recyclerView);
                            Snackbar snackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Deselect All", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            item.setIcon(getDrawable(R.drawable.ic_delete_sweep_black_24dp));
                            Set<String> set = new HashSet<>(Contact.getSavedContactList());
                            ContactStorage.storeSendUsers(getBaseContext(), set, ContactStorage.KEY_ALL_SELECT_STORAGE);
                            ContactAdapter.setAllSelections(true, recyclerView);
                            final MenuItem itemP = item;
                            Snackbar undoSnackbar = Snackbar
                                    .make(findViewById(R.id.recyclerview), "Select All", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            itemP.setIcon(getDrawable(R.drawable.ic_action_selectall));
                                            Contact.initFlag(GoogleDocsSettingsActivity.this, ContactStorage.KEY_ALL_SELECT_STORAGE);
                                            ContactAdapter.setAllSavedSelections(recyclerView);
                                            mToolBar.setSubtitle(" " + Contact.selectedItemCount() + " selected");
                                        }
                                    });

                            undoSnackbar.show();
                        }
                        mToolBar.setSubtitle(" " + Contact.selectedItemCount() + " selected");
                        //Toast.makeText(getBaseContext(), "Select All" , Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        ContactAdapter adapter = new ContactAdapter(Contact.contactList, mToolBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        FloatingActionButton floatingUndefinedButton = (FloatingActionButton) findViewById(R.id.floatingUndefinedAction);
        floatingUndefinedButton.setImageResource(R.drawable.ic_action_check);
        floatingUndefinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<String> set = new HashSet<>(Contact.getSavedContactList());
                ContactStorage.storeSendUsers(GoogleDocsSettingsActivity.this, set, ContactStorage.KEY_STORAGE);
                Toast.makeText(GoogleDocsSettingsActivity.this, "Contacts saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("edu.cmu.chimps.googledocsplugin.sendcontacts");
                intent.addCategory("sendcontacts");
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("contacts", Contact.getSavedContactList());
                intent.putExtra("contacts", bundle);
                sendBroadcast(intent);

                /*
                Intent sugiliteIntent = new Intent("edu.cmu.hcii.sugilite.COMMUNICATION");
                sugiliteIntent.addCategory("android.intent.category.DEFAULT");
                sugiliteIntent.putExtra("messageType", "GET_SCRIPT_LIST");
                startActivityForResult(sugiliteIntent, 1);
                */
            }
        });

    }


}
