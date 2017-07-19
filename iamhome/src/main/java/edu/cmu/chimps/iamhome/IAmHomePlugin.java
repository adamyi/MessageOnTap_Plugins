package edu.cmu.chimps.iamhome;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.device.WifiAp;

import java.util.Set;

import edu.cmu.chimps.iamhome.utils.AlarmUtils;
import edu.cmu.chimps.iamhome.utils.WifiUtils;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.MessageData;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;

public class IAmHomePlugin extends MessageOnTapPlugin {
    UQI uqi;

    private final static int ALARM_HOUR = 22;
    private final static int ALARM_MINUTE = 0;
    private final static int ALARM_SECOND = 0;



    private OnHomeEventListener homeEventListener;

    public void setHomeEventListener(OnHomeEventListener homeEventListener) {
        this.homeEventListener=homeEventListener;
    }

    public void homeSensing() {
        uqi.getData(WifiAp.getUpdateStatus(), Purpose.FEATURE("Listen for wifi changes"))
                .forEach(new Callback<Item>() {
                    @Override
                    protected void onInput(Item input) {
                        if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_CONNECTED))){
                            Set<String> temp = WifiUtils.getUsersHomeWifiList(uqi.getContext());
                            if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){
                                homeEventListener.onEvent(true);
                            }
                        }
                        else if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_DISCONNECTED))){
                            Set<String> temp = WifiUtils.getUsersHomeWifiList(uqi.getContext());
                            if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){
                                homeEventListener.onEvent(false);
                            }
                        }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uqi = new UQI(this);
        //set the alarm
        AlarmUtils.setAlarm(this, ALARM_HOUR, ALARM_MINUTE, ALARM_SECOND);

        setHomeEventListener(new OnHomeEventListener(){
            public void onEvent(boolean arrivesHome){
                if(arrivesHome){
                    Log.e("TAG", "ARRIVES HOME");
                }
                else{
                    Log.e("TAG", "LEFT HOME");
                }
            }
        });
        homeSensing();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        uqi.stopAll();
        super.onDestroy();
    }

    @Override
    protected PluginData iPluginData() {
        return null;
    }

    @Override
    protected void analyzeMessage(MessageData data) {

    }
}
