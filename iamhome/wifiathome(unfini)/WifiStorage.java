package com.everbright.wangyusen.contacs_app;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.privacystreams.core.exceptions.PSException;

/**
 * Created by wangyusen on 7/15/17.
 */

public class WifiStorage {
    public static final String KEY_FORUSERWIFI = "PersonalTimeLine";
    public static final String POSITION  = "position";

    //store wifi information by BSSID
    public static void storeUsersHomewifi(Context context) throws PSException {
        WIFItest wifItest = new WIFItest(context);
        String home_wifi_BSSID =wifItest.getWIFI_BSSID();
        SharedPreferences preferences = context.getSharedPreferences(KEY_FORUSERWIFI, Context.MODE_PRIVATE);
        preferences.edit().putString(POSITION, home_wifi_BSSID).commit();
    }
    public static String getUsersHomewifi(Context context){
        SharedPreferences preferences = context.getSharedPreferences(KEY_FORUSERWIFI, Context.MODE_PRIVATE);
        return preferences.getString(POSITION, "user does not register a home wifi ");
    }

}
