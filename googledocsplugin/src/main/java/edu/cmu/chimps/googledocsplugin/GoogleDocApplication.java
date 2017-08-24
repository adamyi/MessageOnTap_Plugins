package edu.cmu.chimps.googledocsplugin;

import android.app.Application;
import android.content.Context;



public class GoogleDocApplication extends Application {
    private static Context sContext;

    public void onCreate() {
        super.onCreate();
        GoogleDocApplication.sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return GoogleDocApplication.sContext;
    }
}
