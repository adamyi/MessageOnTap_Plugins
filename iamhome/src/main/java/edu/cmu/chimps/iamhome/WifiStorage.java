package edu.cmu.chimps.iamhome;

import android.content.Context;
import android.content.SharedPreferences;
import com.github.privacystreams.core.exceptions.PSException;
import java.util.HashSet;
import java.util.Set;


public class WifiStorage {
    public static final String KEY_FOR_USER_WIFI = "set_PersonalTimeLine";
    public static final String POSITION  = "set_position";

    //store all user's wifi " BSSi
    public static void storeUsersHomeWifi(Context context) throws PSException {
        WifiMain wifimain = new WifiMain(context);
        Set<String> home_wifi_BSSID = new HashSet<>();
        home_wifi_BSSID.addAll(wifimain.getBSSIDList(context));
        SharedPreferences preferences = context.getSharedPreferences(KEY_FOR_USER_WIFI, Context.MODE_PRIVATE);
        preferences.edit().putStringSet(POSITION, home_wifi_BSSID).apply();
    }
    public static Set<String> getUsersHomeWifiList(Context context){
        SharedPreferences preferences = context.getSharedPreferences(KEY_FOR_USER_WIFI, Context.MODE_PRIVATE);
        return preferences.getStringSet(POSITION, new HashSet<String>());
    }
}
