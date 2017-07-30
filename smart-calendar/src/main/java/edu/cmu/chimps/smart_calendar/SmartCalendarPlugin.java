package edu.cmu.chimps.smart_calendar;

import android.text.Html;
import android.util.EventLogTags;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.cmu.chimps.messageontap_api.DataUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;

import static android.R.attr.tag;
import static android.R.id.message;


public class SmartCalendarPlugin extends MessageOnTapPlugin {

    public static final String TAG = "SmartCalendar plugin";
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    long TidShow1, TidShow2, TidShow3, TidAdd1, TidAdd2;
    private Tree tree1,tree2;
    String EventTime;

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

        // TID is something we might need to implement stateflow inside a plugin.
        if (triggerListShow.contains(params.get("trigger"))){
            //Todo:Add root
            tree1 = params.get("tree");
            EventTime = AddRoot(params);                    //Event
            params.put("tree", tree);
            TidShow1 = newTaskRequest(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);
        }
        if (triggerListAdd.contains(params.get("trigger"))){
            tree = (Tree)params.get("tree");

            TidAdd1 = newTaskRequest(sid, MethodConstants.UI_SHOW, "BubbleShow", params);
        }
    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        ArrayList<String> eventList;
            if (tid == TidShow1) {
                //Todo:getCardMessage and put it into params
                eventList = new ArrayList<>();
                try {
                    ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                    for (HashMap<String, Object> card:cardList){
                        eventList.add((String)card.get("GRAPH_EVENT_NAME"));
                    }
                    if (!cardList.isEmpty()) {
                        params.put("Bubble Content", "Show Calendar");
                        TidShow2 = newTaskRequest(sid, MethodConstants.UI_SHOW, "Bubble", params);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    endSession(sid);
                }
                TidShow2 = newTaskRequest(sid, MethodConstants.UI_SHOW, "paramsMessage", params);
            } else if (tid == TidShow2){
                try {
                    params.put("HTML Details", getHtml(eventList, EventTime));
                    TidShow3 = newTaskRequest(sid, MethodConstants.UI_UPDATE, "html", params);
                }catch (Exception e){
                    e.printStackTrace();
                    endSession(sid);
                }
            } else if (tid == TidShow3){
                Log.e(TAG, "Ending session (triggerList1)");
                endSession(sid);
                Log.e(TAG, "Session ended");
            }

            if (tid == TidAdd1){
                String event = tree.FindNodeByTag(tag_event);     //！！如果要AddCalendar一定要有event
                String time = tree.FindNodeByTag(tag_time);
                params.put("action:Add to calendar event", event);
                params.put("action:Add to calendar time", time);        //time 必须要精确到日期？
                TidAdd2 = newTaskRequest(sid, MethodConstants.ACTION, "params", params);
            } else if (tid == TidAdd2){
                Log.e(TAG, "Ending session (triggerList2)");
                endSession(sid);
                Log.e(TAG, "Session ended");
            }
    }

    private String getHtml(ArrayList<String> eventList, String EventTime){
        String html = "";

        return html;
    }

    private Boolean CardisEmpty(HashMap<String, Object> params){
        try {
            HashMap<String, Object> card = (HashMap<String, Object>) params.get("Card");
            if (card.isEmpty()){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "CardisEmpty: can not find card");
        }
        return true;
    }

    private String AddRoot(Tree tree1){
        for (Node node: tree){
            if (node.getParent() == 0){
                node.setParent(ROOT);
                Node newNode = new Node();
                newNode.setId(Event Name);
                newNode.setParent(0);
                newNode.setChildren(node.getId());
                node.addTag("GRAPH_EVENT_TIME");
                EventTime = node.getContent();
            }
        }
        return EventTime;
    }

}

