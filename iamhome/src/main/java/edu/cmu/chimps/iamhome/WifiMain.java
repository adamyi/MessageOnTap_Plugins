package edu.cmu.chimps.iamhome;

import android.content.Context;
import android.content.Context;



import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.exceptions.PSException;
import com.github.privacystreams.core.purposes.Purpose;

import com.github.privacystreams.device.WifiAp;

import java.util.List;
import java.util.List;

/**
 * Created by wangyusen on 7/16/17.
 */

public class WifiMain {
    private UQI uqi;
    private Purpose purpose;

    WifiMain(Context context){
        this.purpose = Purpose.TEST("test");
        this.uqi = new UQI(context);
    }


    public String getWIFI_BSSID() throws PSException {
        List<Item> wifi_list =uqi.getData(WifiAp.getScanResults(),purpose)
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .asList();
        if(wifi_list.isEmpty()){
            return null;
        }
        else{
            return wifi_list.get(0).getValueByField(WifiAp.BSSID).toString();
        }
    }

    public static Boolean isConnectedtoWifi(Context context) throws PSException {
        UQI uqi = new UQI(context);
        Purpose purpose = Purpose.TEST("test");
        List<Item> wifi_list =uqi.getData(WifiAp.getScanResults(),purpose)
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .asList();
        return !wifi_list.isEmpty();
    }

    public Boolean isAtHome(Context context) throws PSException {

        String current_bssid = getWIFI_BSSID();
        String home_wifi = WifiStorage.getUsersHomewifi(context);
        return current_bssid != null && current_bssid.equals(home_wifi);
    }
    public List<String> getB_List(Context context) throws PSException {
        String name = uqi.getData(WifiAp.getScanResults(), purpose)
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED).getFirst().getField(WifiAp.SSID).toString();

//        Log.i("name1", String.valueOf(uqi.getData(WifiAp.getScanResults(), purpose)
//                .filter(WifiAp.SSID, name).asList(WifiAp.BSSID)));

        return uqi.getData(WifiAp.getScanResults(), purpose)
                .filter(WifiAp.SSID, name).asList(WifiAp.BSSID);
    }

}
