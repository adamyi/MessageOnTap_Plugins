package edu.cmu.chimps.iamhome;

import android.content.Context;




import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.exceptions.PSException;
import com.github.privacystreams.core.purposes.Purpose;

import com.github.privacystreams.device.WifiAp;

import java.util.List;


public class WifiMain {
    private UQI uqi;
    private Purpose purpose;
    WifiMain(Context context){
        this.purpose = Purpose.TEST("test");
        this.uqi = new UQI(context);
    }

    //get connected wifi BSSIDs
    public String getWifiBSSID() throws PSException {
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
    //check whether user has connected to a wifi
    public static Boolean isConnectedToWifi(Context context) throws PSException {
        UQI uqi = new UQI(context);
        Purpose purpose = Purpose.TEST("test");
        List<Item> wifi_list =uqi.getData(WifiAp.getScanResults(),purpose)
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .asList();
        return !wifi_list.isEmpty();
    }

    //check whether the user is at home;

    /**
     *
     * @param context
     * @return
     * @throws PSException
     */
    public Boolean isAtHome(Context context) throws PSException {
        WifiStatus wifistatus = new WifiStatus(context);
        return wifistatus.isAtHome();
    }

    //get user connected wifi's all BSSIDs;
    public List<String> getBSSIDList(Context context) throws PSException {

        String name = uqi.getData(WifiAp.getScanResults(), purpose)
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .getFirst().getField(WifiAp.SSID).toString();

        return uqi.getData(WifiAp.getScanResults(), purpose)
                .filter(WifiAp.SSID, name).asList(WifiAp.BSSID);
    }

}
