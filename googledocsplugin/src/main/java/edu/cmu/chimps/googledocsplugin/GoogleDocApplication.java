package edu.cmu.chimps.googledocsplugin;

import android.app.Application;
import android.content.Context;



public class GoogleDocApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        GoogleDocApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return GoogleDocApplication.context;
    }
}
