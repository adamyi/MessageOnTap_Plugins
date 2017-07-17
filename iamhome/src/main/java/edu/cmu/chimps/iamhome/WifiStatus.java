package edu.cmu.chimps.iamhome;

import android.content.Context;
import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.device.WifiAp;
import java.util.Set;

/**
 * Created by wangyusen on 7/16/17.
 */

public class WifiStatus {
    private UQI uqi;
    private Purpose purpose;
    private Context context;
    public boolean athome;
    WifiStatus(Context context){
        this.purpose = Purpose.TEST("test");
        this.uqi = new UQI(context);
        this.context = context;
    }
    public void ifWifiStatusChange() {
        uqi.getData(WifiAp.getUpdateStatus(), purpose).forEach(new Callback<Item>() {
            @Override
            protected void onInput(Item input) {

                //toast "you are at home" if user is connecting to home wifi
                if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_CONNECTED))
                        && isAthome()){
                    StatusToasts.atHomeToast(context);
                }
                //toast "you have left home" if user is disconnecting from home wifi
                if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_DISCONNECTED))
                        && isAthome()){
                    StatusToasts.leaveHomeToast(context);
                }

            }
        });
    }
    //check whether user is at home;
    public boolean isAthome(){
        uqi.getData(WifiAp.getUpdateStatus(), purpose).forEach(new Callback<Item>() {
            @Override
            protected void onInput(Item input) {
                athome = false;
                athome = false;
                Set<String> temp = WifiStorage.getUsersHomewifiList(context);
                if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){
                    athome = true;
                }
            }
        });
        return athome;
    }


}
