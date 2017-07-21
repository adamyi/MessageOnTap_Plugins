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
import edu.cmu.chimps.messageontap_api.MessageData;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.PluginData;

public class IAmHomePlugin extends MessageOnTapPlugin {
    UQI mUQI;


    private final static int ALARM_HOUR = 18;
    private final static int ALARM_MINUTE = 30;

    private final static int ALARM_SECOND = 0;


    private OnHomeEventListener homeEventListener;

    public void setHomeEventListener(OnHomeEventListener homeEventListener) {
        this.homeEventListener=homeEventListener;
    }

    public void homeSensing() {

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

        mUQI.getData(WifiAp.getUpdateStatus(), Purpose.FEATURE("Listen for wifi changes"))
                .forEach(new Callback<Item>() {
                    @Override
                    protected void onInput(Item input) {
                        if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_CONNECTED))){
                            Set<String> temp = WifiUtils.getUsersHomeWifiList(MyApplication.getContext());
                            if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){

                                homeEventListener.onEvent(true);
                                StatusToasts.atHomeToast(MyApplication.getContext());
                            }
                            StatusToasts.wifiConnectedToast(MyApplication.getContext());
                        }
                        else if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_DISCONNECTED))){
                            Set<String> temp = WifiUtils.getUsersHomeWifiList(MyApplication.getContext());
                            if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){
                                homeEventListener.onEvent(false);
                                StatusToasts.leaveHomeToast(MyApplication.getContext());
                            }
                            StatusToasts.wifiDisconnectedToast(MyApplication.getContext());
                        }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUQI = new UQI(this);
        Log.e("service","stshbuob" );
        //set the alarm
        AlarmUtils.setAlarm(this,ALARM_HOUR, ALARM_MINUTE, ALARM_SECOND);

        homeSensing();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mUQI.stopAll();
        super.onDestroy();
    }

    @Override
    protected PluginData iPluginData() {
        return new PluginData();
    }

    @Override
    protected void analyzeMessage(MessageData data) {

    }
}
