package edu.cmu.chimps.iamhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.device.WifiAp;

import java.util.HashMap;
import java.util.Set;

import edu.cmu.chimps.iamhome.listeners.OnHomeEventListener;
import edu.cmu.chimps.iamhome.sharedprefs.ContactStorage;
import edu.cmu.chimps.iamhome.sharedprefs.StringStorage;
import edu.cmu.chimps.iamhome.utils.AlarmUtils;
import edu.cmu.chimps.iamhome.utils.StatusToastsUtils;
import edu.cmu.chimps.iamhome.utils.WifiUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.ServiceAttributes;

public class IAmHomePlugin extends MessageOnTapPlugin {
    private UQI mUQI;
    private Long tid;

    private final static int ALARM_HOUR = 8;
    private final static int ALARM_MINUTE = 29;

    private final static int ALARM_SECOND = 0;

    public static boolean result = false;
    private OnHomeEventListener homeEventListener;

    @Override
    protected void initNewSession(long l, HashMap<String, Object> hashMap) throws Exception {
        hashMap.put(ServiceAttributes.Action.SHARE_EXTRA_REFERENCE_LIST, ContactStorage.getContacts(MyApplication.getContext(), ContactStorage.KEY_STORAGE).toArray());
        hashMap.put(ServiceAttributes.Action.SHARE_EXTRA_MESSAGE, StringStorage.getMessage(MyApplication.getContext()));
        hashMap.put(ServiceAttributes.Action.SHARE_EXTRA_APP, "whatsapp");
        hashMap.put(ServiceAttributes.Action.SHARE_EXTRA_TOAST, true);
        tid = createTask(l, MethodConstants.ACTION_TYPE, MethodConstants.ACTION_METHOD_AUTO_SHARE, hashMap);
    }

    @Override
    protected void newTaskResponded(long l, long l1, HashMap<String, Object> hashMap) throws Exception {
        if (l1 == tid) {
            endSession(l);
        }
    }

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
                                result = true;
                                homeEventListener.onEvent(true);
                                StatusToastsUtils.atHomeToast(MyApplication.getContext());
                            }
                            StatusToastsUtils.wifiConnectedToast(MyApplication.getContext());
                        }
                        else if((input.getValueByField(WifiAp.STATUS).toString().equals(WifiAp.STATUS_DISCONNECTED))){
                            Set<String> temp = WifiUtils.getUsersHomeWifiList(MyApplication.getContext());
                            if(temp != null && temp.contains(input.getValueByField(WifiAp.BSSID))){
                                homeEventListener.onEvent(false);
                                StatusToastsUtils.leaveHomeToast(MyApplication.getContext());
                                result = false;
                            }
                            StatusToastsUtils.wifiDisconnectedToast(MyApplication.getContext());
                        }
            }
        });

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("BroadcastReceiver", "Received Broadcast");
            createSession();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUQI = new UQI(this);
        Log.e("service","stshbuob" );
        //set the alarm
        AlarmUtils.setAlarm(this,ALARM_HOUR, ALARM_MINUTE, ALARM_SECOND);
        homeSensing();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Session On Start"));
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

    public boolean isAtHome(){
        return result;
    }

}
