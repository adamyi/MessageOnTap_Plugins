package edu.cmu.chimps.iamhome;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {

    private static Context mContext;

    @Override
    public void onCreate() {

        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
    return mContext;
}

}