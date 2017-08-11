package edu.cmu.chimps.starbucksplugin;

import android.app.Application;
import android.content.Context;

/**
 * Created by knight006 on 8/8/2017.
 */

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
