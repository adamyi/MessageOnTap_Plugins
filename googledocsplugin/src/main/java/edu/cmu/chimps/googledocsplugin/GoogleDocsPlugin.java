package edu.cmu.chimps.googledocsplugin;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import edu.cmu.chimps.messageontap_api.DataUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;

public class GoogleDocsPlugin extends MessageOnTapPlugin {
    public static final String TAG = "sample_plugin";
    @Override
    protected PluginData iPluginData() {
        Log.e("plugin", "getting plugin data");
        return new PluginData();
    }

    public void clearLists(ArrayList<Tag> mMandatory, ArrayList<Tag> mOptional){
        mMandatory.clear();
        mOptional.clear();
    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, DataUtils.hashMapToString(params));
        HashMap<String, Object> reqParams = new HashMap<>();
        reqParams.put("key1", "value1");
        reqParams.put("key2", "value2");
        reqParams.put("key3", "value3");
        // TID is something we might need to implement stateflow inside a plugin.
        long tid = newTaskRequest(sid, MethodConstants.PMS_TYPE, "test", params);

    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, DataUtils.hashMapToString(params));
        Log.e(TAG, "Ending session " + sid);
        endSession(sid);
        Log.e(TAG, "Session ended");
    }
}
