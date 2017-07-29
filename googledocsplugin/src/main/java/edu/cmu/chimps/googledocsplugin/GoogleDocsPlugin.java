package edu.cmu.chimps.googledocsplugin;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import edu.cmu.chimps.messageontap_api.DataUtils;
import edu.cmu.chimps.messageontap_api.EntityAttributes;
import edu.cmu.chimps.messageontap_api.Globals;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Trigger;

public class GoogleDocsPlugin extends MessageOnTapPlugin {

    public static final String TAG = "GoogleDoc plugin";
    private Long TidPKG, TidBubble, TidDocSend;

    /**
     * Return the trigger criteria of this plug-in. This will be called when
     * MessageOnTap is started (when this plugin is already enabled) or when
     * this plugin is being enabled.
     *
     * @return PluginData containing the trigger
     */
    @Override
    protected PluginData iPluginData() {
        Log.e(TAG, "getting plugin data");
        ArrayList<String> mKeyList = new ArrayList<>();
        mKeyList.add(EntityAttributes.Person.PERSON_NAME);
        mKeyList.add(EntityAttributes.Person.PHONE_NUMBER);
        mKeyList.add(Globals.KEY_QUERY_SUBJECT);
        return new PluginData().trigger(new Trigger(mKeyList));
    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, DataUtils.hashMapToString(params));
        // TID is something we might need to implement stateflow inside a plugin.
        TidPKG = newTaskRequest(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);
    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        if (tid == TidPKG){
            params.put("UI message", );
            TidBubble = newTaskRequest(sid, MethodConstants.UI_SHOW, "paramsMessage", params);
        } else if (tid == TidBubble){
            params.put("Action message", );
            TidDocSend = newTaskRequest(sid, MethodConstants.ACTION, "Send GoogleDoc", params);
        } else if (tid == TidDocSend){
            Log.e(TAG, "Ending session");                                //require tigger and message
            endSession(sid);
            Log.e(TAG, "Session ended");
        }


    }
}

