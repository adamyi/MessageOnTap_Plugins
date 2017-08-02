package edu.cmu.chimps.googledocsplugin;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;


public class GoogleDocsSettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
