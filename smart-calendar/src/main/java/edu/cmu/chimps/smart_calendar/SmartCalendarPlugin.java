package edu.cmu.chimps.smart_calendar;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.EntityAttributes;
import edu.cmu.chimps.messageontap_api.Globals;
import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.ParseTree.Mood;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;


import static edu.cmu.chimps.messageontap_api.EntityAttributes.CURRENT_MESSAGE_EMBEDDED_TIME;
import static edu.cmu.chimps.messageontap_api.ParseTree.Direction;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.AddRootEventName;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.AddRootLocation;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getDate;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getEventList;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getHtml;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getTimeString;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.setListLocation;


public class SmartCalendarPlugin extends MessageOnTapPlugin {

    public static final String TAG = "SmartCalendar plugin";
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    HashMap<Long, Long> TidPutTreeToGetTime, TidPutTreeToGetLocation, TidShowBubble, TidShowHtml, TidAddAction_ShowBubble, TidAddAction;

    HashMap<Long,ArrayList<Event>> EventList;

    HashMap<Long, ParseTree> tree1, tree2;
    HashMap<Long, String> EventTimeString1, EventTimeString2;
    HashMap<Long, Long> EventBeginTime2, EventEndTime2;

    // init the tags
    Tag tag_I = new Tag("TAG_I", new HashSet<>(Collections.singletonList("I")));
    Tag tag_you = new Tag("TAG_You", new HashSet<>(Collections.singletonList("you")));
    Tag tag_free = new Tag("TAG_FREE", new HashSet<>(Collections.singletonList(
            "(free|available|have time)")));
    Tag tag_we = new Tag("TAG_WE", new HashSet<>(Collections.singletonList("(We|us|our)")));
    Tag tag_time = new Tag("TAG_TIME", new HashSet<>(Collections.singletonList(
            "(tomorrow|AM|PM|am|pm|today|morning|afternoon|evening|night)")));
    Tag tag_optional_time = new Tag("TAG_OPTIONAL_TIME",new HashSet<>(
            Collections.singletonList("([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]")));


    /**
     * Return the trigger criteria of this plug-in. This will be called when
     * MessageOnTap is started (when this plugin is already enabled) or when
     * this plugin is being enabled.
     *
     * @return PluginData containing the trigger
     */

    public void clearLists(Set<String> mMandatory, Set<String> mOptional){
        mMandatory.clear();
        mOptional.clear();
    }
    @Override
    protected PluginData iPluginData() {
        Log.e("plugin", "getting plugin data");
        Set<Trigger> triggerArrayList = new HashSet<>();
        Set<Tag> tagList = new HashSet<>();
        tagList.add(tag_I);
        tagList.add(tag_you);
        tagList.add(tag_free);
        tagList.add(tag_we);
        tagList.add(tag_time);
        tagList.add(tag_optional_time);

        Set<String> mMandatory = new HashSet<>();
        Set<String> mOptional = new HashSet<>();

        // Category one: show calendar
        // trigger1: are you free tomorrow? incoming
        mMandatory.add("TAG_You");
        mMandatory.add("TAG_FREE");
        mMandatory.add("TAG_TIME");
        mOptional.add("TAG_OPTIONAL_TIME");
        HashSet<Trigger.Constraint> constraints= new HashSet<>();
        Trigger trigger1 = new Trigger("calendar_trigger_one", mMandatory, mOptional, constraints,
                Mood.INTERROGTIVE, Direction.INCOMING);
        triggerArrayList.add(trigger1);
        clearLists(mMandatory,mOptional);
        // TODO: triggerListShow add entry
        // Category two: update calendar
        // trigger2: I can pick it up at 9pm. outgoing
        mMandatory.add("TAG_I");
        mMandatory.add("TAG_TIME");
        mOptional.add("TAG_OPTIONAL_TIME");
        HashSet<Trigger.Constraint> constraints2= new HashSet<>();
        Trigger trigger2 = new Trigger("calendar_trigger_two", mMandatory, mOptional, constraints2,
                Mood.IMPERATIVE, Direction.OUTGOING);
        triggerArrayList.add(trigger2);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);
        //Session.TRIGGER_SOURCE = "update_calendar";
        // trigger3: We will (Let us) meet next Monday morning. both ways
        mMandatory.add("TAG_WE");
        mMandatory.add("TAG_TIME");
        mOptional.add("TAG_OPTIONAL_TIME");
        HashSet<Trigger.Constraint> constraints3= new HashSet<>();
        Trigger trigger3 = new Trigger("calendar_trigger_three", mMandatory, mOptional,
                constraints3, Mood.UNKNOWN, Direction.UNKNOWN);
        triggerArrayList.add(trigger3);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);

        // trigger 4: Can you (I) pick it up this afternoon? Incoming
        mOptional.add("TAG_I");
        mOptional.add("TAG_You");
        mOptional.add("TAG_OPTIONAL_TIME");
        mMandatory.add("TAG_TIME");
        HashSet<Trigger.Constraint> constraints4= new HashSet<>();
        Trigger trigger4 = new Trigger("calendar_trigger_four", mMandatory, mOptional, constraints4,
                Mood.UNKNOWN, Direction.INCOMING);
        triggerArrayList.add(trigger4);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);
        // TODO: triggerListAdd add entry and triggerArrayList add these two lists
        ArrayList<String> holder = new ArrayList<>();
        return new PluginData().tagSet(JSONUtils.simpleObjectToJson(tagList, Globals.TYPE_TAG_SET))
                .triggerSet(JSONUtils.simpleObjectToJson(triggerArrayList, Globals.TYPE_TRIGGER_SET));
    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        // TID is something we might need to implement stateflow inside a plugin.

        if (params.get(EntityAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_one")||
                params.get(EntityAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_two")){
            tree1.put(sid, (ParseTree)params.get(EntityAttributes.Graph.SYNTAX_TREE));
            EventTimeString1.put(sid, getTimeString(params));
            params.remove(EntityAttributes.Graph.SYNTAX_TREE);
            params.put(EntityAttributes.Graph.SYNTAX_TREE, AddRootEventName(tree1.get(sid), EventTimeString1.get(sid), tag_time));
            TidPutTreeToGetTime.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params));
        }

        if (params.get(EntityAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_three")||
                params.get(EntityAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_four")){
            //tree2 = (ParseTree)params.get(EntityAttributes.Graph.SYNTAX_TREE);
            Long[] timeArray = (Long[])params.get(CURRENT_MESSAGE_EMBEDDED_TIME);
            EventBeginTime2.put(sid, timeArray[0]);
            EventEndTime2.put(sid, timeArray[1]);
            //EventTimeString2 = getTimeString(params);
            //params.put(BUBBLE_FIRST_LINE, "Add Calendar");
            //params.put(BUBBLE_SECOND_LINE, "Event begin time:"+ EventBeginTime2);
            TidAddAction_ShowBubble.put(sid, createTask(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_SHOW_BUBBLE, params));
        }
    }


    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, JSONUtils.hashMapToString(params));

        if (tid == TidPutTreeToGetTime.get(sid)){
            try{
                EventList.put(sid, getEventList(params));
                params.remove(EntityAttributes.Graph.SYNTAX_TREE);
                params.put(EntityAttributes.Graph.SYNTAX_TREE, AddRootLocation(tree1.get(sid), EventTimeString1.get(sid), tag_time));
                TidPutTreeToGetLocation.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params));

            }catch (Exception e){
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidPutTreeToGetLocation.get(sid)) {
            //getCardMessage and put it into params
            try {
                setListLocation(EventList.get(sid), params);
                //params.put(BUBBLE_FIRST_LINE, "Show Calendar");
                TidShowBubble.put(sid, createTask(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_SHOW_BUBBLE, params));
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidShowBubble.get(sid)){
            try {
                //if (params.get(BUBBLE_STATUS).equals(Bubble.M_CLICKED)){
                if (1 == 1){
                    params.put("HTML Details", getHtml(EventListSortByTime(EventList.get(sid))));
                    TidShowHtml.put(sid, createTask(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_LOAD_WEBVIEW, params));
                } else {
                    endSession(sid);
                }
            }catch (Exception e){
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidShowHtml.get(sid)){
            Log.e(TAG, "Ending session (triggerListShow)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }


        if (tid == TidAddAction_ShowBubble.get(sid)){
            //if (params.get(BUBBLE_STATUS) == 1) {
            if (1 == 1){
                Calendar beginDate = getDate(EventBeginTime2.get(sid));           //transfer from Long to date
                Calendar endDate = getDate(EventEndTime2.get(sid));
                params.put("action:Add to calendar time", beginDate);
                params.put("action:Add to calendar time", endDate);
                TidAddAction.put(sid, createTask(sid, MethodConstants.ACTION_TYPE, MethodConstants.ACTION_METHOD_CALENDAR_NEW, params));
            } else {
                endSession(sid);
            }
        } else if (tid == TidAddAction.get(sid)){
            Log.e(TAG, "Ending session (triggerListAdd)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }
    }

    private ArrayList<Event> EventListSortByTime(ArrayList<Event> events){
        Collections.sort(events,new SortbyTime());
        return events;

    }

    class SortbyTime implements Comparator{
        public int compare(Object o1,Object o2){
            Event e1 = (Event) o1;
            Event e2 = (Event) o2;
            return e1.getBeginTime().compareTo(e2.getBeginTime());
        }
    }

    @Override
    protected void endSession(long sid) {
        TidPutTreeToGetTime.remove(sid); TidPutTreeToGetLocation.remove(sid); TidShowBubble.remove(sid); TidShowHtml.remove(sid);
        TidAddAction.remove(sid); TidAddAction_ShowBubble.remove(sid); EventList.remove(sid); tree1.remove(sid);
        tree2.remove(sid); EventTimeString1.remove(sid); EventTimeString2.remove(sid);
        EventBeginTime2.remove(sid); EventEndTime2.remove(sid);
        super.endSession(sid);
    }
    


}


