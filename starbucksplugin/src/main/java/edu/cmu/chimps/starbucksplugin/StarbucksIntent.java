package edu.cmu.chimps.starbucksplugin;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
public class StarbucksIntent extends PreferenceActivity {
    public static String scriptResult;
    public final static String TAG = "StarbucksIntent";
    public static String scriptName;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent sugiliteIntent = new Intent("edu.cmu.hcii.sugilite.COMMUNICATION");
        sugiliteIntent.addCategory("android.intent.category.DEFAULT");
        sugiliteIntent.putExtra("messageType", "RUN_SCRIPT");
        if (scriptName != null) {
            sugiliteIntent.putExtra("arg1", scriptName);
            sugiliteIntent.putExtra("arg2", "Run Script Complete");
            startActivityForResult(sugiliteIntent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1){
            Bundle bundle = data.getExtras();
            scriptResult = bundle.getString("messageType");
            Log.e(TAG, "onActivityResult: " + scriptResult);

        }
    }

}
