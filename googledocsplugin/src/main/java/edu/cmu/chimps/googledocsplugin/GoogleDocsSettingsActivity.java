package edu.cmu.chimps.googledocsplugin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class GoogleDocsSettingsActivity extends AppCompatActivity {
    public static String TAG = "GoogleDocActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onPostResume() {
        Log.e(TAG, "onPostResume:  resume");
        super.onPostResume();
    }
}
