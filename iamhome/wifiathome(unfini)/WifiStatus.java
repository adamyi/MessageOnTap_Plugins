package com.everbright.wangyusen.contacs_app;

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
    WifiStatus(Context context){
        this.purpose = Purpose.TEST("test");
        this.uqi = new UQI(context);
        this.context = context;
    }
    public void ifWifiStatusChange() {
        uqi.getData(WifiAp.getUpdateStatus(), purpose).forEach(new Callback<Item>() {
            @Override
            protected void onInput(Item input) {

                boolean athome = false;
                Set<String> temp = Wifi_set.getUsersHomewifiList(context);
                if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){
                    athome = true;
                }
                if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_CONNECTED))
                        && athome){
                    StatusToasts.atHomeToast(context);

                }
                if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_DISCONNECTED))
                        && athome){
                    StatusToasts.leaveHomeToast(context);

                }

                if (input.getValueByField(WifiAp.STATUS).toString()
                        .equals(WifiAp.STATUS_CONNECTED)){
                    StatusToasts.wifiConnectedToast(context);
                    Headsup_Noti.notification(context);
                }
                if(input.getValueByField(WifiAp.STATUS).toString()
                        .equals(WifiAp.STATUS_DISCONNECTED)){
                    StatusToasts.wifiDisconnectedToast(context);
                }


            }
        });
    }


}
