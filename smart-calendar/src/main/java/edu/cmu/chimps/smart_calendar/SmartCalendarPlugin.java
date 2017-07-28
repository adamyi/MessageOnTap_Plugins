package edu.cmu.chimps.smart_calendar;

import android.text.Html;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.cmu.chimps.messageontap_api.DataUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;


public class SmartCalendarPlugin extends MessageOnTapPlugin {

    public static final String TAG = "sample_plugin";
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    long tid1, tid2, tid3, tid4, tid5;

    /**
     * Return the trigger criteria of this plug-in. This will be called when
     * MessageOnTap is started (when this plugin is already enabled) or when
     * this plugin is being enabled.
     *
     * @return PluginData containing the trigger
     */

    public void clearLists(ArrayList<Tag> mMandatory, ArrayList<Tag> mOptional){
        mMandatory.clear();
        mOptional.clear();
    }
    @Override
    protected PluginData iPluginData() {
        // init the tags
        Tag tag_I = new Tag("TAG_I", new ArrayList<String>(Collections.singletonList("I")));
        Tag tag_you = new Tag("TAG_You", new ArrayList<String>(Collections.singletonList("you")));
        Tag tag_free = new Tag("TAG_FREE", new ArrayList<String>(Collections.singletonList(
                "(free|available|have time)")));
        Tag tag_we = new Tag("TAG_WE", new ArrayList<String>(Collections.singletonList("(We|us|our)")));
        Tag tag_time = new Tag("TAG_TIME", new ArrayList<String>(Collections.singletonList(
                "(tomorrow|AM|PM|am|pm|today|morning|afternoon|evening|night)")));
        Tag tag_optional_time = new Tag("TAG_OPTIONAL_TIME",new ArrayList<String>(
                Collections.singletonList("([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]")));
        Log.e("plugin", "getting plugin data");
        ArrayList<Trigger> triggerArrayList = new ArrayList<>();
        ArrayList<Tag> mMandatory = new ArrayList<>();
        ArrayList<Tag> mOptional = new ArrayList<>();

        // Category one: show calendar
        // trigger1: are you free tomorrow? incoming
        mMandatory.add(tag_you);
        mMandatory.add(tag_free);
        mMandatory.add(tag_time);
        mOptional.add(tag_optional_time);
        MOOD = 1;
        DIRECTION = 0;
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);

        // Category two: update calendar
        // trigger2: I can pick it up at 9pm. outgoing
        mMandatory.add(tag_I);
        mMandatory.add(tag_time);
        mOptional.add(tag_optional_time);
        MOOD = 0;
        DIRECTION = 1;
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);

        // trigger3: We will (Let us) meet next Monday morning. both ways
        mMandatory.add(tag_we);
        mMandatory.add(tag_time);
        mOptional.add(tag_optional_time);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);

        // trigger 4: Can you (I) pick it up this afternoon? Incoming
        mOptional.add(tag_I);
        mOptional.add(tag_you);
        mOptional.add(tag_optional_time);
        mMandatory.add(tag_time);
        DIRECTION = 0;
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);
        ArrayList<String> holder = new ArrayList<>();
        return new PluginData().trigger(new Trigger(holder));
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
        Tree tree = params.get("tree");
        if (tree.get("event") == ){
            tid1 = newTaskRequest(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);
        } else {
            tid4 = newTaskRequest(sid, MethodConstants.UI_SHOW, MethodConstants.BUBBLE, params);
        }
    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        if (triggerListShow.contains(params.get("trigger"))){
            if (tid == tid1){
                tid2 = newTaskRequest(sid, MethodConstants.UI_SHOW, "params", params); //what are params
            } else if (tid == tid2){
                params.put("HTML", getHtml(params));
                tid3 = newTaskRequest(sid, MethodConstants.UI_UPDATE, "html", params);  //how to pass html
            } else if (tid == tid3){
                Log.e(TAG, "Ending session (triggerList1)");                                //require tigger and message
                endSession(sid);
                Log.e(TAG, "Session ended");
            }
        }

        if (triggerListAdd.contains(params.get("trigger"))){
            if (tid == tid4){
                params.put("action", "Add to calendar:"+tree.getEvent);
                tid5 = newTaskRequest(sid, MethodConstants.ACTION, "params", params); //what are params
            } else if (tid == tid5){
                Log.e(TAG, "Ending session (triggerList2)");                                //require tigger and message
                endSession(sid);
                Log.e(TAG, "Session ended");
            }
        }


    }

    private Html getHtml(HashMap<String, Object> params){
        Html html = null;



        return html;
    }
    }
}
