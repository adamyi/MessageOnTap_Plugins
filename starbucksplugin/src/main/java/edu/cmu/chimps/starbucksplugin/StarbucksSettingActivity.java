package edu.cmu.chimps.starbucksplugin;


import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class StarbucksSettingActivity extends AppCompatActivity {
    public static String TAG = "StarbucksActivity";
    private static String sResult;
    Toolbar mToolbar;
    ScriptAdapter mAdapter;
    RecyclerView mRecyclerView;
    private int mBackPressedCount;
    public static FlagChangeListener listener;
    public static void setFlagChangeListener(FlagChangeListener icl) {
         listener = icl;
    }

    @Override
    public void onBackPressed() {

        if (mBackPressedCount == 0) {
            Toast.makeText(StarbucksSettingActivity.this, "Click again to cancel the change", Toast.LENGTH_SHORT).show();
            mBackPressedCount++;
        } else if (mBackPressedCount == 1) {
            Toast.makeText(StarbucksSettingActivity.this, "Change canceled", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    sResult = data.getStringExtra("result");
                    //ArrayList<String> ResultArray = (ArrayList<String>) JSONUtils.jsonToSimpleObject(SResult,JSONUtils.TYPE_TAG_ARRAY);
                    Log.e(TAG, "onResult:" + sResult);
                    ArrayList<String> result = new ArrayList<>();
                    if (sResult != "") {
                        result = rehandledResultArrayList(sResult);
                        Script.scriptList.clear();
                        for (String str : result){
                            Script script = new Script(str);
                            Script.scriptList.add(script);
                        }
                        mAdapter.notifyDataSetChanged();
                        Log.e(TAG, "onActivityResult: " + result.toString());
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.recyclerview), "Scripts have been updated", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.recyclerview), "No Script", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                break;
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_select);

        //Initialize ScriptList
        Intent sugiliteIntent = new Intent("edu.cmu.hcii.sugilite.COMMUNICATION");
        sugiliteIntent.addCategory("android.intent.category.DEFAULT");
        sugiliteIntent.putExtra("messageType", "GET_SCRIPT_LIST");
        startActivityForResult(sugiliteIntent, 1);

        setFlagChangeListener(new FlagChangeListener() {
            @Override
            public void onChange(Boolean wantChange) {
                if (wantChange) ScriptAdapter.SetAllSelection(mRecyclerView);
            }
        });

        //Initialize UI
        setContentView(R.layout.activity_contact_select);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setTitle("Select Script");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorwhite));
        mToolbar.inflateMenu(R.menu.updatescript);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getBaseContext(), "Contacts Saved" , Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (Script.scriptList.isEmpty()) return false;
                int menuItemId = item.getItemId();
                switch (menuItemId) {
                    case R.id.updatescript:
                        Intent sugiliteIntent = new Intent("edu.cmu.hcii.sugilite.COMMUNICATION");
                        sugiliteIntent.addCategory("android.intent.category.DEFAULT");
                        sugiliteIntent.putExtra("messageType", "GET_SCRIPT_LIST");
                        startActivityForResult(sugiliteIntent, 1);
                        break;
                }
                return true;
            }
        });

        mAdapter = new ScriptAdapter(Script.scriptList, mToolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        FloatingActionButton floatingUndefinedButton = (FloatingActionButton) findViewById(R.id.floatingUndefinedAction);
        floatingUndefinedButton.setImageResource(R.drawable.ic_action_check);
        floatingUndefinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(StarbucksSettingActivity.this, "Contacts saved", Toast.LENGTH_SHORT).show();
                ScriptStorage.storeScript(StarbucksSettingActivity.this, Script.getSelectedName());//if scriptName is empty, save "empty"
                Toast.makeText(StarbucksSettingActivity.this, "script saved", Toast.LENGTH_SHORT).show();
            }
        });        
    }
    //turn the json string into Arraylist
    protected ArrayList<String> rehandledResultArrayList(String json){
        ArrayList<String> result = new ArrayList<>();
        String nJson = json;
        nJson = nJson.substring(2);
        nJson = nJson.substring(0,nJson.length()-2);
        String[] nJsonString = nJson.split("\",\"");
        for (String i:nJsonString){
            result.add(i);
        }
        return result;
    }
}
