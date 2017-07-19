package edu.cmu.chimps.messageontap_api;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

public abstract class MessageOnTapPlugin extends Service {
    /**
     * The {@link Intent} action representing a MessageOnTap extension. This service should
     * declare an <code>&lt;intent-filter&gt;</code> for this action in order to register with
     * DashClock.
     */
    public static final String ACTION_EXTENSION = "edu.cmu.chimps.messageontap_prototype.Plugin";

    protected IExtensionManager mManager;

    private IBinder mBinder = new IExtension.Stub(){

        @Override
        public void onMessageReceived(MessageData data) throws RemoteException {
            analyzeMessage(data);
        }

        @Override
        public ExtensionData getExtensionData() throws RemoteException {
            return iExtensionData();
        }

        @Override
        public void registerManager(IExtensionManager manager) throws RemoteException {
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(Binder.getCallingUid());
            if(packages!=null && packages.length>0){
                packageName = packages[0];
            }
            mManager = manager;
            //extensions.put(packageName, listener);
            //mListenerList.register(listener);
        }

        @Override
        public void unregisterManager(IExtensionManager manager) throws RemoteException {
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(Binder.getCallingUid());
            if(packages!=null && packages.length>0){
                packageName = packages[0];
            }
            mManager = null;
            //extensions.remove(packageName);
            /*boolean success = mListenerList.unregister(listener);
            if (success) {
                Log.d("extension", "Unregistration succeed.");
            } else {
                Log.d("extension", "Not found, cannot unregister.");
            }*/
        }

        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected abstract ExtensionData iExtensionData();
    protected abstract void analyzeMessage(MessageData data);
}
