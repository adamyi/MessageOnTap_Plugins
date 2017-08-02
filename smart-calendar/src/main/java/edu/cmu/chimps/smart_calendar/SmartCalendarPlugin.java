package edu.cmu.chimps.smart_calendar;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.EntityAttributes;
import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.ParseTree.Mood;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Session;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;

import static edu.cmu.chimps.messageontap_api.EntityAttributes.CURRENT_MESSAGE_EMBEDDED_TIME;
import static edu.cmu.chimps.messageontap_api.ParseTree.Direction;

import static edu.cmu.chimps.messageontap_api.ParseTree.Node;

import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.AddRootEventName;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.AddRootLocation;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.DAY;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.HOUR;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.LOCATIONROOTID;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.LOCATION_ROOT_ID;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.MONTH;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.NAMEROOTID;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.NAME_ROOT_ID;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.YEAR;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getDate;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getEventList;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getHtml;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getTimeString;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.setListLocation;


public class SmartCalendarPlugin extends MessageOnTapPlugin {

    public static final String TAG = "SmartCalendar plugin";
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    long TidShow0, TidShow1, TidShow2, TidShow3, TidAdd1, TidAdd2;

    ArrayList<Event> EventList;

    private ParseTree tree1, tree2;
    String EventTimeString1, EventTimeString2;
    Long EventBeginTime2, EventEndTime2;

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
        ArrayList<Trigger> triggerArrayList = new ArrayList<>();
        Set<String> mMandatory = new HashSet<>();
        Set<String> mOptional = new HashSet<>();

        // Category one: show calendar
        // trigger1: are you free tomorrow? incoming
        mMandatory.add(tag_you);
        mMandatory.add(tag_free);
        mMandatory.add(tag_time);
        mOptional.add(tag_optional_time);
        HashSet<Trigger.Constraint> constraints= new HashSet<>();
        Trigger trigger1 = new Trigger("calendar_trigger_one", mMandatory, mOptional, constraints,
                Mood.INTERROGTIVE, Direction.INCOMING);
        triggerArrayList.add(trigger1);
        clearLists(mMandatory,mOptional);
        // TODO: triggerListShow add entry
        // Category two: update calendar
        // trigger2: I can pick it up at 9pm. outgoing
        mMandatory.add(tag_I);
        mMandatory.add(tag_time);
        mOptional.add(tag_optional_time);
        HashSet<Trigger.Constraint> constraints2= new HashSet<>();
        Trigger trigger2 = new Trigger("calendar_trigger_two", mMandatory, mOptional, constraints2,
                Mood.IMPERATIVE, Direction.OUTGOING);
        triggerArrayList.add(trigger2);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);
        //Session.TRIGGER_SOURCE = "update_calendar";
        // trigger3: We will (Let us) meet next Monday morning. both ways
        mMandatory.add(tag_we);
        mMandatory.add(tag_time);
        mOptional.add(tag_optional_time);
        HashSet<Trigger.Constraint> constraints3= new HashSet<>();
        Trigger trigger3 = new Trigger("calendar_trigger_three", mMandatory, mOptional,
                constraints3, Mood.UNKNOWN, Direction.UNKNOWN);
        triggerArrayList.add(trigger3);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);

        // trigger 4: Can you (I) pick it up this afternoon? Incoming
        mOptional.add(tag_I);
        mOptional.add(tag_you);
        mOptional.add(tag_optional_time);
        mMandatory.add(tag_time);
        HashSet<Trigger.Constraint> constraints4= new HashSet<>();
        Trigger trigger4 = new Trigger("calendar_trigger_four", mMandatory, mOptional, constraints4,
                Mood.UNKNOWN, Direction.INCOMING);
        triggerArrayList.add(trigger4);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);
        // TODO: triggerListAdd add entry and triggerArrayList add these two lists
        ArrayList<String> holder = new ArrayList<>();
        return new PluginData().trigger(trigger1);
    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        // TID is something we might need to implement stateflow inside a plugin.

        if (params.get(Session.TRIGGER_SOURCE ).equals("calendar_trigger_one")||
                params.get(Session.TRIGGER_SOURCE).equals("calendar_trigger_two")){
            tree1 = (ParseTree)params.get(EntityAttributes.Graph.SYNTAX_TREE);
            EventTimeString1 = getTimeString(params);
            params.remove(EntityAttributes.Graph.SYNTAX_TREE);
            params.put(EntityAttributes.Graph.SYNTAX_TREE, AddRootEventName(tree1, EventTimeString1, tag_time));
            TidShow0 = newTaskRequest(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params);
        }

        if (params.get(Session.TRIGGER_SOURCE).equals("calendar_trigger_three")||
                params.get(Session.TRIGGER_SOURCE).equals("calendar_trigger_four")){
            //tree2 = (ParseTree)params.get(EntityAttributes.Graph.SYNTAX_TREE);
            Long[] timeArray = (Long[])params.get(CURRENT_MESSAGE_EMBEDDED_TIME);
            EventBeginTime2 = timeArray[0];
            EventEndTime2 = timeArray[1];
            //EventTimeString2 = getTimeString(params);
            params.put(BUBBLE_FIRST_LINE, "Add Calendar");
            params.put(BUBBLE_SECOND_LINE, "Event begin time:"+ EventBeginTime2);
            TidAdd1 = newTaskRequest(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_SHOW_BUBBLE, params);
        }
    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, JSONUtils.hashMapToString(params));

        if (tid == TidShow0){
            try{
                EventList = getEventList(params);
                params.remove(EntityAttributes.Graph.SYNTAX_TREE);
                params.put(EntityAttributes.Graph.SYNTAX_TREE, AddRootLocation(tree1, EventTimeString1, tag_time));
                TidShow0 = newTaskRequest(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params);

            }catch (Exception e){
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidShow1) {
            //getCardMessage and put it into params
            try {
                setListLocation(EventList, params);
                params.put(BUBBLE_FIRST_LINE, "Show Calendar");
                TidShow2 = newTaskRequest(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_SHOW_BUBBLE, params);
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidShow2){
            try {
                if (params.get(BUBBLE_STATUS) == 1){
                    params.put("HTML Details", getHtml(EventListSortByTime(EventList)));
                    TidShow3 = newTaskRequest(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_LOAD_WEBVIEW, params);
                } else {
                    endSession(sid);
                }
            }catch (Exception e){
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidShow3){
            Log.e(TAG, "Ending session (triggerListShow)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }


        if (tid == TidAdd1){
            if (params.get(BUBBLE_STATUS) == 1) {
                Calendar beginDate = getDate(EventBeginTime2);           //transfer from Long to date
                Calendar endDate = getDate(EventEndTime2);
                params.put("action:Add to calendar time", beginDate);
                params.put("action:Add to calendar time", endDate);
                TidAdd2 = newTaskRequest(sid, MethodConstants.ACTION_TYPE, MethodConstants.ACTION_METHOD_CALENDAR_NEW, params);
            } else {
                endSession(sid);
            }
        } else if (tid == TidAdd2){
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


}


