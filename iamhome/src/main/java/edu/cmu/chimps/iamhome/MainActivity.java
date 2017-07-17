package edu.cmu.chimps.iamhome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import edu.cmu.chimps.messageontap_api.ExtensionData;
import edu.cmu.chimps.messageontap_api.MessageData;
import edu.cmu.chimps.messageontap_api.IExtensionManager;
import edu.cmu.chimps.messageontap_api.IExtensionManagerListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button extensionButton;
    private Button messageButton;

    private IExtensionManager mIExtensionManager;

    private IExtensionManagerListener mExtentionManagerListener = new IExtensionManagerListener.Stub() {
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
                mIExtensionManager.registerListener(mExtentionManagerListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mIExtensionManager.unregisterListener(mExtentionManagerListener);
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

    private void initView() {
        extensionButton = (Button) findViewById(R.id.extensionButton);
        messageButton = (Button) findViewById(R.id.messageButton);

        extensionButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageButton:
                try {
                    mIExtensionManager.sendResponse(new MessageData().messageID(1000).request("test request").response("test response"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.extensionButton:
                try {
                    mIExtensionManager.updateInfo(new ExtensionData().trigger("test trigger"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
