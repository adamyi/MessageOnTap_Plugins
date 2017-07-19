package edu.cmu.chimps.smart_calendar;

import android.os.RemoteException;
import android.util.Log;

import edu.cmu.chimps.messageontap_api.ExtensionData;
import edu.cmu.chimps.messageontap_api.MessageData;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;

public class SmartCalendarPlugin extends MessageOnTapPlugin {

    @Override
    protected ExtensionData iExtensionData() {
        Log.e("extension", "getting extension data");
        return new ExtensionData().trigger("test trigger");
    }

    @Override
    protected void analyzeMessage(MessageData data) {
        Log.e("extension", "got messagedata: " + data);
        try {
            mManager.sendResponse(data.response("test response"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
