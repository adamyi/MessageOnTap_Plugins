package edu.cmu.chimps.iamhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import edu.cmu.chimps.messageontap_api.ExtensionData;
import edu.cmu.chimps.messageontap_api.IExtensionManager;
import edu.cmu.chimps.messageontap_api.IExtensionManagerListener;
import edu.cmu.chimps.messageontap_api.MessageData;

public class MainActivity extends AppCompatActivity {

    private IExtensionManager mIExtensionManager;

    private IExtensionManagerListener mExtensionManagerListener = new IExtensionManagerListener.Stub() {
        @Override
        public void onMessageReceived(MessageData data) throws RemoteException {
            Log.e("extension", "message data:" + data.toString());
        }
    };

    private ServiceConnection conn = new ServiceConnection() {

        //绑定服务，回调onBind()方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIExtensionManager = IExtensionManager.Stub.asInterface(service);
            try {
                mIExtensionManager.registerListener(mExtensionManagerListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mIExtensionManager.unregisterListener(mExtensionManagerListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mIExtensionManager = null;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService();
        initView();
        //set the alarm
        AlarmSetting.fireAlarm(this);
        //listen if the wifi status has changed
        new WifiStatus(this).ifWifiStatusChange();

    }

    private ArrayList<String> getWhatsAppContacts(){
        Cursor c = getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[] { ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY },
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[] { "com.whatsapp" },
                null);

        ArrayList<String> whatsAppContacts = new ArrayList<>();
        int contactNameColumn;
        if (c != null) {
            contactNameColumn = c.getColumnIndex(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY);
            while (c.moveToNext())
            {
                // You can also read RawContacts.CONTACT_ID to read the
                // ContactsContract.Contacts table or any of the other related ones.
                whatsAppContacts.add(c.getString(contactNameColumn));
            }
            c.close();
        }
        return whatsAppContacts;
    }


    private void initView() {

    }

    private void sendResponse(long queryId, String request, String response){
        try {
            mIExtensionManager.sendResponse(new MessageData().messageID(queryId)
                    .request(request)
                    .response(response)
            );
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initialize(String trigger){
        try {
            mIExtensionManager.updateInfo(new ExtensionData().trigger(trigger));
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindService() {

        Intent intent = new Intent();
        //绑定服务端的service
        intent.setAction("edu.cmu.chimps.messageontap_prototype.service.ExtensionManagerService");
        //新版本（5.0后）必须显式intent启动 绑定服务
        intent.setComponent(new ComponentName("edu.cmu.chimps.messageontap_prototype","edu.cmu.chimps.messageontap_prototype.service.ExtensionManagerService"));
        //绑定的时候服务端自动创建
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
