package edu.cmu.chimps.googledocsplugin;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.cmu.chimps.messageontap_api.DataUtils;
import edu.cmu.chimps.messageontap_api.EntityAttributes;
import edu.cmu.chimps.messageontap_api.Globals;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;

import static android.R.attr.y;

public class GoogleDocsPlugin extends MessageOnTapPlugin {

    public static final String TAG = "GoogleDoc plugin";
    private Long TidFindDoc, TidBubble, TidDocSend;
    ArrayList<String> DocList;
    private Tag TAG_FILENAME;
    Tag tag_doc = new Tag("TAG_DOC", new ArrayList<String>(Collections.singletonList(
            "(file|doc|document)")));
    Tag tag_I = new Tag("TAG_I", new ArrayList<String>(Collections.singletonList("I")));
    Tag tag_me = new Tag("TAG_ME", new ArrayList<String>(Collections.singletonList(
            "(us|me)")));
    Tag tag_send = new Tag("TAG_SEND", new ArrayList<String>(Collections.singletonList(
            "(share|send|show|give)")));
    Tag tag_time = new Tag("TAG_TIME", new ArrayList<String>(Collections.singletonList(
            "(tomorrow|AM|PM|am|pm|today|morning|afternoon|evening|night)")));
    Tag tag_you = new Tag("TAG_You", new ArrayList<String>(Collections.singletonList("you")));
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    public int COMPLETE = 0; // 0 is complete

// doc, file
    // optional flag month, date, regular expression different format
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
        ArrayList<Trigger> triggerArrayList = new ArrayList<>();
        ArrayList<Tag> mMandatory = new ArrayList<>();
        ArrayList<Tag> mOptional = new ArrayList<>();

        // Category one: with file name
        // trigger 1: Can you send me XXX (a file)?
        COMPLETE = 0;
        mOptional.add(tag_you);
        mMandatory.add(tag_send);
        mOptional.add(tag_me);
        mMandatory.add(TAG_FILENAME);
        DIRECTION = 0;
        clearLists(mMandatory,mOptional);
        //trigger 4: I can send you XXX
        mMandatory.add(tag_I);
        mMandatory.add(tag_send);
        mOptional.add(tag_you);
        MOOD = 0;
        DIRECTION = 1;
        // Category two: without file name
        // trigger 2: Can you send me the file on this topic
        // second example: send me the file please
        mMandatory.add(tag_send);
        mOptional.add(tag_me);
        mMandatory.add(tag_doc);
        mOptional.add(tag_time);
        DIRECTION = 0;
        clearLists(mMandatory,mOptional);
        // trigger 3: I want to send you the doc we talked about earlier
        // second example: I'll share my document
        mOptional.add(tag_I);
        mMandatory.add(tag_send);
        mOptional.add(tag_you);
        mMandatory.add(tag_doc);
        mOptional.add(tag_time);
        DIRECTION = 1;
        MOOD = 0;
        clearLists(mMandatory,mOptional);
        return new PluginData().trigger(new Trigger());
    }

    public void clearLists(ArrayList<Tag> mMandatory, ArrayList<Tag> mOptional){
        mMandatory.clear();
        mOptional.clear();
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
            //params.put("Action message", );
            TidDocSend = newTaskRequest(sid, MethodConstants.ACTION, "Send GoogleDoc", params);
        } else if (tid == TidDocSend){
            Log.e(TAG, "Ending session");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }
    }

}

