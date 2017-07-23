package edu.cmu.chimps.iamhome.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.github.privacystreams.accessibility.AccEvent;
import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.utils.AppUtils;

import java.util.Set;

import edu.cmu.chimps.iamhome.MyApplication;
import edu.cmu.chimps.iamhome.NodeInfoListener;
import edu.cmu.chimps.iamhome.SharedPrefs.ContactStorage;
import edu.cmu.chimps.iamhome.SharedPrefs.StringStorage;
import edu.cmu.chimps.iamhome.utils.AutoSelectUtils;

public class ShareMessageService extends Service {

    UQI uqi;
    String[] contactNames;
    Boolean clicked;
    private static final String CONTACT_RESOURCE_ID = "contactpicker_row_name";

    private NodeInfoListener nodeInfoListener;

    public void setNodeInfoListener(NodeInfoListener nodeInfoListener) {
        this.nodeInfoListener = nodeInfoListener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.e("Service", "service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uqi = new UQI(this);
        clicked = false;

        if (ContactStorage.getContacts(MyApplication.getContext()) == null) {
            Toast noListInStoreToast = Toast.makeText(this, "Set default select list first", Toast.LENGTH_SHORT);
            noListInStoreToast.show();
            stopSelf();
        }

        Set<String> inputSet = ContactStorage.getContacts(MyApplication.getContext());
        contactNames = inputSet.toArray(new String[inputSet.size()]);
        AutoSelectUtils autoSelectUtils = new AutoSelectUtils();

        setNodeInfoListener(new NodeInfoListener() {
            public void nodeInfoReceived(AccessibilityNodeInfo selectingView) {
                Log.e("HI", "NodeINfoReceived");
                if (!clicked) {
                    Log.e("List Size", Integer.toString(contactNames.length));
                    clicked = AutoSelectUtils.autoSelect(contactNames, selectingView);
                }
                stopSelf();
            }
        });

        uqi.getData(AccEvent.asUpdates(), Purpose.FEATURE("base event"))
                .forEach(new Callback<Item>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override

                    protected void onInput(Item item) {
                        AccessibilityNodeInfo root = item.getValueByField(AccEvent.ROOT_NODE);
                        if ((int)item.getValueByField(AccEvent.EVENT_TYPE) != 0x00000800) {Log.e("Fuuuu", item.toString());}
                        if (root != null && root.getPackageName().equals(AppUtils.APP_PACKAGE_WHATSAPP)
                                && (int) item.getValueByField(AccEvent.EVENT_TYPE) == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                            nodeInfoListener.nodeInfoReceived(root);
                            Log.e("UQI Thread", "Transferred");
                            stopSelf();
                        }
                    }
                });

        autoSelectUtils.autoLaunch(this, StringStorage.getMessage(getBaseContext()), AppUtils.APP_PACKAGE_WHATSAPP);

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Log.e("stopped", "stopped");
            uqi.stopAll();
        } catch (Exception e) {
            Log.e("stopped", "exception");
            e.printStackTrace();
        }
        uqi = null;
        clicked = false;
    }
}
