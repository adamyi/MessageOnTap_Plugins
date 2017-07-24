package edu.cmu.chimps.iamhome;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.iamhome.RecyView.Contact;
import edu.cmu.chimps.iamhome.RecyView.ContactAdapter;
import edu.cmu.chimps.iamhome.SharedPrefs.ContactStorage;

<<<<<<<HEAD
        =======
        >>>>>>>3b3ce5abc95956b9c1a861c76a6d290e9011139a

public class SelectContactActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    @Override
    public void onBackPressed() {
        Set<String> set = new HashSet<>(Contact.getSavedContactList());
        ContactStorage.storeSendUsers(getBaseContext(), set);
        Toast.makeText(this, "Contacts Saved", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitle("Select contacts to share");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorwhite));
        toolbar.setSubtitle(" "+Contact.SelectedItemCount() + " selected");
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
                switch (menuItemId){
                    case R.id.selectAll:
                        if (Contact.SelectedItemCount() == Contact.contactList.size()){
                            ContactAdapter.SetAllSelction(false, recyclerView);
                        } else {
                            ContactAdapter.SetAllSelction(true, recyclerView);
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
        Contact.InitSelection(this);
        ContactAdapter adapter = new ContactAdapter(Contact.contactList, toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //set the alarm
//        AlarmUtils.setAlarm(this, 14,20,00);
        startService(new Intent(this, IAmHomePlugin.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> savedContactList = new ArrayList<>();
        for (int i = 0; i < Contact.contactList.size(); i++){
            if (Contact.contactList.get(i).isFlag()){
                savedContactList.add(Contact.contactList.get(i).getName());
            }
        }
        //Toast.makeText(this, "Contacts Saved" , Toast.LENGTH_SHORT).show();

        Set<String> set = new HashSet<>(savedContactList);
        ContactStorage.storeSendUsers(this, set);

        return true;
    }
    }
 }

