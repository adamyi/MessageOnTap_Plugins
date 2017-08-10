package edu.cmu.chimps.starbucksplugin;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import edu.cmu.chimps.messageontap_api.JSONUtils;

public class StarbucksSettingActivity extends PreferenceActivity {
    public static String TAG = "StarbucksActivity";
    public static String Result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starbucks);
        Intent sugiliteIntent = new Intent("edu.cmu.hcii.sugilite.COMMUNICATION");
        sugiliteIntent.addCategory("android.intent.category.DEFAULT");
        sugiliteIntent.putExtra("messageType", "GET_SCRIPT_LIST");
        startActivityForResult(sugiliteIntent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
      switch (requestCode){
          case 1:
              if (resultCode == RESULT_OK && data != null) {
                  Result = data.getStringExtra("result");
                  //ArrayList<String> ResultArray = (ArrayList<String>) JSONUtils.jsonToSimpleObject(Result,JSONUtils.TYPE_TAG_ARRAY);
                  Log.e(TAG, "onResult:" + Result);

                  ArrayList<String> result = rehandledResultArrayList(Result);
                  Log.e(TAG, "onActivityResult: " + result.toString());


              }
              break;
      }
    }

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
