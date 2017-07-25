package edu.cmu.chimps.iamhome;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {

    private static Context mContext;
    private Activity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public static Context getContext() {
        return mContext;
    }

}