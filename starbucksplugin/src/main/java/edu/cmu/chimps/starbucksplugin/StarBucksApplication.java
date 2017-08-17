package edu.cmu.chimps.starbucksplugin;

import android.app.Application;
import android.content.Context;


public class StarBucksApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        StarBucksApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return StarBucksApplication.context;
    }
}
