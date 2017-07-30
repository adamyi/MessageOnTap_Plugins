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

import static android.R.attr.y;

public class GoogleDocsPlugin extends MessageOnTapPlugin {

    public static final String TAG = "GoogleDoc plugin";
    private Long TidFindDoc, TidBubble, TidDocSend;
    ArrayList<String> DocList;

    //Todo: 写trigger, 分两种：1.包含文件名的。2.不包含文件名的

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

        DocList = new ArrayList<>();
        params.put("FindDoc", DocList);

        //todo: if root is not googleDoc, add it
        //能不能找GoogleDoc？
        TidFindDoc = newTaskResponsed(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);

    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        if (tid == TidFindDoc){
            try {
                HashMap<String, Object> card = (HashMap<String, Object>) params.get("Card");
                if (!card.isEmpty()) {
                    DocList = (ArrayList<String>) card.get("FindDoc");
                    params.put("Bubble Content", "Send Docs");
                    TidBubble = newTaskRequest(sid, MethodConstants.UI_SHOW, "Bubble", params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidBubble){
            params.put("Message to send", DocList);
            params.put("Action message", );
            TidDocSend = newTaskRequest(sid, MethodConstants.ACTION, "Send GoogleDoc", params);
        } else if (tid == TidDocSend){
            Log.e(TAG, "Ending session");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }

    }

}

