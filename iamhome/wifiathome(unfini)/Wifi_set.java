package com.everbright.wangyusen.contacs_app;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.privacystreams.core.exceptions.PSException;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by wangyusen on 7/16/17.
 */

public class Wifi_set {
    public static final String KEY_FORUSERWIFI = "set_PersonalTimeLine";
    public static final String POSITION  = "set_position";

    //store wifi information by BSSID
    public static void storeUsersHomewifi(Context context) throws PSException {
        WIFItest wifItest = new WIFItest(context);
        Set<String> home_wifi_BSSID = new HashSet<>();
        home_wifi_BSSID.addAll(wifItest.getB_List(context));
        SharedPreferences preferences = context.getSharedPreferences(KEY_FORUSERWIFI, Context.MODE_PRIVATE);
        preferences.edit().putStringSet(POSITION, home_wifi_BSSID).commit();
    }
    public static Set<String> getUsersHomewifiList(Context context){
        SharedPreferences preferences = context.getSharedPreferences(KEY_FORUSERWIFI, Context.MODE_PRIVATE);
        return preferences.getStringSet(POSITION,null);
    }

}
