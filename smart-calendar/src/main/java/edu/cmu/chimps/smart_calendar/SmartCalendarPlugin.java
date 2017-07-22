package edu.cmu.chimps.smart_calendar;

import android.os.RemoteException;
import android.util.Log;

import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.MessageData;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;

public class SmartCalendarPlugin extends MessageOnTapPlugin {

    @Override
    protected PluginData iPluginData() {
        Log.e("plugin", "getting plugin data");
        return new PluginData();
    }

    @Override
    protected void analyzeMessage(MessageData data) {
        Log.e("plugin", "got messagedata: " + data);
        try {
            mManager.sendResponse(data.response("test response"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
