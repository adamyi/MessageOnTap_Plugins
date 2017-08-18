package edu.cmu.chimps.smart_calendar;

import android.util.Log;
import android.util.SparseArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.ServiceAttributes;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getEventList;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getHtml;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getTid;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.getTimeString;
import static edu.cmu.chimps.smart_calendar.SmartCalendarUtils.setListLocation;

public class SmartCalendarPlugin extends MessageOnTapPlugin {

    public static final String TAG = "SmartCalendar plugin";
    public static int eventNameId = 3726;
    public static int eventTimeId = 1567;
    public static int eventLocationId = 9123;
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    HashMap<Long, Long> tidPutTreeToGetTime = new HashMap<>();
    HashMap<Long,Long> tidPutTreeToGetLocation = new HashMap<>();
    HashMap<Long,Long> tidAddAction_ShowBubble = new HashMap<>();

    HashMap<Long,Long> tidAddAction = new HashMap<>();
    HashMap<Long,Long> tidShowHtml = new HashMap<>();
    HashMap<Long,Long> tidShowBubble = new HashMap<>();
    HashMap<Long,ArrayList<Event>> eventList = new HashMap<>();
    HashMap<Long, ParseTree> tree1 = new HashMap<>();
    HashMap<Long, ParseTree> tree2 = new HashMap<>();
   // HashMap<Long, String> eventTimeString1 = new HashMap<>();
   // HashMap<Long, String> eventTimeString2 = new HashMap<>();
    HashMap<Long, Long> eventBeginTime = new HashMap<>();
    HashMap<Long, Long> eventEndTime = new HashMap<>();

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

    Tag tag_I_text = new Tag ("TAG_T_TEXT",new HashSet<>(Collections.singletonList("I")));
    Tag tag_you_text = new Tag("TAG_YOU_TEXT",new HashSet<>(Collections.singletonList("you")));
    Tag tag_free_text = new Tag("TAG_FREE_TEXT", new HashSet<>(Collections.singletonList("free")));

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
        tagList.add(tag_free_text);

        //Tag Set
        Set<String> mMandatory = new HashSet<>();
        Set<String> mOptional = new HashSet<>();

        // Category one: show calendar
        // trigger1: are you free tomorrow? incoming
        mMandatory.add("TAG_You");
        mMandatory.add("TAG_FREE_TEXT");
        mMandatory.add("TAG_TIME");
        mOptional.add("TAG_OPTIONAL_TIME");
        HashSet<Trigger.Constraint> constraints= new HashSet<>();

        Trigger trigger1 = new Trigger("calendar_trigger_one", mMandatory);//, mOptional, constraints,Mood.INTERROGTIVE, Direction.INCOMING);
        triggerArrayList.add(trigger1);
        clearLists(mMandatory,mOptional);
        // TODO: triggerListShow add entry
        // Category two: update calendar
        // trigger2: I can pick it up at 9pm. outgoing
        mMandatory.add("TAG_I");
        mMandatory.add("TAG_TIME");
        mOptional.add("TAG_OPTIONAL_TIME");
        HashSet<Trigger.Constraint> constraints2= new HashSet<>();
        Trigger trigger2 = new Trigger("calendar_trigger_two", mMandatory,mOptional);
        triggerArrayList.add(trigger2);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);
        //Session.TRIGGER_SOURCE = "update_calendar";
        // trigger3: We will (Let us) meet next Monday morning. both ways
        mMandatory.add("TAG_WE");
        mMandatory.add("TAG_TIME");
        mOptional.add("TAG_OPTIONAL_TIME");
        HashSet<Trigger.Constraint> constraints3= new HashSet<>();
        Trigger trigger3 = new Trigger("calendar_trigger_three", mMandatory);//, mOptional,constraints3, Mood.UNKNOWN, Direction.UNKNOWN);
        triggerArrayList.add(trigger3);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);

        // trigger 4: Can you (I) pick it up this afternoon? Incoming
        //mOptional.add("TAG_I");
        //mOptional.add("TAG_You");
        //mOptional.add("TAG_OPTIONAL_TIME");
        //mMandatory.add("TAG_TIME");
        //HashSet<Trigger.Constraint> constraints4= new HashSet<>();
        //Trigger trigger4 = new Trigger("calendar_trigger_four", mMandatory, mOptional);
        //triggerArrayList.add(trigger4);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);
        // TODO: triggerListAdd add entry and triggerArrayList add these two lists
        ArrayList<String> holder = new ArrayList<>();

        return new PluginData().triggerSet(JSONUtils.simpleObjectToJson(triggerArrayList, JSONUtils.TYPE_TRIGGER_SET))
                .tagSet(JSONUtils.simpleObjectToJson(tagList, JSONUtils.TYPE_TAG_SET));

    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        // TID is something we might need to implement stateflow inside a plugin.

        if (params.get(ServiceAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_one")||
                params.get(ServiceAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_two")){

            tree1.put(sid,(ParseTree) JSONUtils.jsonToSimpleObject((String)params
                    .get(ServiceAttributes.PMS.PARSE_TREE),JSONUtils.TYPE_PARSE_TREE));
            Log.e(TAG, "Tree is " + params.get(ServiceAttributes.PMS.PARSE_TREE));
            /*
            for (int i=0; i < tree1.getNodeList().size(); i++){
                ParseTree.Node node = tree1.getNodeList().get(i);

                if (node.getParentId() == 0){
                    node.setParentId(-1);
                    ParseTree.Node newNode = new ParseTree.Node();
                    newNode.setId(1);
                    newNode.setParentId(-1);
                    Set<Integer> set = new HashSet<>();
                    set.add(node.getId());
                    newNode.setChildrenIds(set);
                    newNode.addTag(ServiceAttributes.Graph.Event.NAME);
                }

                if (node.getTagList().contains(tag_time)){
                    node.getTagList().clear();
                    node.setWord(getTimeString(params));
                    node.addTag(ServiceAttributes.Graph.Event.TIME);
                }
            }

            /*
            Date date = new Date();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.ENGLISH);
            try {
                dateBegin = s.parse("2017-8-1-8-30");
            }catch (ParseException e){
                e.printStackTrace();
            }

            Date date2 = new Date();
            SimpleDateFormat en = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            try{
                dateEnd = s.parse("2017-8-1-9-40");
            }catch (ParseException e){

            }
            */
            ParseTree tree = new ParseTree();
            ParseTree.Node timeNode = new ParseTree.Node();
            timeNode.setWord(getTimeString(params));
            Log.e(TAG,getTimeString(params));
            Set<String> timeTagSet = new HashSet<>();
            timeTagSet.add(ServiceAttributes.Graph.Event.TIME);
            timeNode.setTagList(timeTagSet);
            timeNode.setId(eventTimeId);
            timeNode.setParentId(eventNameId);

            ParseTree.Node eventNode = new ParseTree.Node();
            Set<String> nameTagSet = new HashSet<>();
            nameTagSet.add(ServiceAttributes.Graph.Event.NAME);
            eventNode.setTagList(nameTagSet);
            eventNode.setId(eventNameId);
            eventNode.setParentId(-1);//root id

            Set<Integer> ChildrenId = new HashSet<>();
            ChildrenId.add(eventTimeId);
            eventNode.setChildrenIds(ChildrenId);

            SparseArray<ParseTree.Node> array = new SparseArray<>();
            array.put(eventTimeId,timeNode);
            array.put(eventNameId,eventNode);
            tree.setNodeList(array);
            tree1.put(sid,tree);

            Log.e(TAG, "Start to Send Tree to PMS");

            params.put(ServiceAttributes.PMS.PARSE_TREE, JSONUtils.simpleObjectToJson(tree1.get(sid), JSONUtils.TYPE_PARSE_TREE));
            Log.e(TAG, "Put Tree" + JSONUtils.simpleObjectToJson(tree1.get(sid), JSONUtils.TYPE_PARSE_TREE));
            tidPutTreeToGetTime.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE,
                    MethodConstants.GRAPH_METHOD_RETRIEVE, params));
            Log.e(TAG, "Send Tree to PMS");

           /*  testing html
            Event eventTest = new Event();
            Date date = new Date();
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd-HH-mm",Locale.ENGLISH);
            try {
                dateBegin = s.parse("2001-11-10-8-30");
            }catch (ParseException e){
                e.printStackTrace();
            }
            event1.setBeginTime(date.getTime());
            event1.setEndTime(date.getTime());
            Date dateEnd = new Date();
            SimpleDateFormat en = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            try{
                dateEnd = s.parse("2001-11-10-9-40");
            }catch (ParseException e){

            }
            eventTest.setEndTime(date2.getTime());
            eventTest.setEventName("Do HomeWork");
            eventTest.setLocation("Zoo");
            ArrayList<Event> events = new ArrayList<>();
            events.add(eventTest);
            eventList.put(sid,events);
            params.put("html_string", getHtml(eventList.get(sid)));
            tidShowHtml.put(sid, createTask(sid, MethodConstants.UI_TYPE,
                    MethodConstants.UI_METHOD_LOAD_WEBVIEW, params));
            */
        }

        if (params.get(ServiceAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_three")||params.get(ServiceAttributes.PMS.TRIGGER_SOURCE).equals("calendar_trigger_four")) {
            //tree2 = (ParseTree)params.get(ServiceAttributes.Graph.SYNTAX_TREE);
            Log.e(TAG, "Trigger3");
            if (params.get(ServiceAttributes.PMS.CURRENT_MESSAGE_EMBEDDED_TIME).equals("")){
                Log.e(TAG, "initNewSession: get messsage embeded time");
            ArrayList<ArrayList<Long>> messageTime = (ArrayList<ArrayList<Long>>)params.get(ServiceAttributes.PMS.CURRENT_MESSAGE_EMBEDDED_TIME);
            eventBeginTime.put(sid,messageTime.get(0).get(0));
            eventEndTime.put(sid,messageTime.get(0).get(1));
             params.put(ServiceAttributes.UI.BUBBLE_FIRST_LINE, "Add Calendar");
             params.put(ServiceAttributes.UI.BUBBLE_SECOND_LINE, eventBeginTime + "-" + eventEndTime);
                params.put(ServiceAttributes.UI.ICON_TYPE_STRING,R.string.fa_calendar);
            }
            else{
                params.put(ServiceAttributes.UI.BUBBLE_FIRST_LINE, "Add Calendar");
                params.put(ServiceAttributes.UI.BUBBLE_SECOND_LINE, "");
                params.put(ServiceAttributes.UI.ICON_TYPE_STRING,R.string.fa_calendar);
            }
            tidAddAction_ShowBubble.put(sid, createTask(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_SHOW_BUBBLE, params));
        }
    }

    @Override
    protected void newTaskResponded(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        if (tid == getTid(tidPutTreeToGetTime, sid)){
            try{
                eventList.put(sid, getEventList(params));
                Log.e(TAG, "Event List=" + getEventList(params).toString());
                Log.e(TAG, "Got Task ID");
              /*
                params.put(ServiceAttributes.Graph.SYNTAX_TREE, AddRootLocation(tree1.get(sid),
                        EventTimeString1.get(sid), tag_time));
                        */
              // set location node
                SparseArray<ParseTree.Node> nodeList = tree1.get(sid).getNodeList();
                nodeList.remove(eventNameId);
                ParseTree.Node locationNode = new ParseTree.Node();
                locationNode.setId(eventLocationId);
                locationNode.setParentId(-1);
                Set<Integer> setChildIds = new HashSet<>();
                setChildIds.add(eventTimeId);
                locationNode.setChildrenIds(setChildIds);
                //set tag
                Set<String> set = new HashSet<>();
                set.add(ServiceAttributes.Graph.Place.NAME);
                locationNode.setTagList(set);
                nodeList.put(eventLocationId, locationNode);
                //put and send tree
                tree1.get(sid).setNodeList(nodeList);
                Log.e(TAG, "tree1 is : " + JSONUtils.simpleObjectToJson(tree1, JSONUtils.TYPE_PARSE_TREE));
                params.put(ServiceAttributes.Graph.SYNTAX_TREE,tree1);
                tidPutTreeToGetLocation.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE,
                        MethodConstants.GRAPH_METHOD_RETRIEVE, params));
                Log.e(TAG,"PUT TREE TO GET LOCATION");

            }catch (Exception e){
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == getTid(tidPutTreeToGetLocation, sid)) {
            //getCardMessage and put it into params
            try {
                setListLocation(eventList.get(sid), params);
                params.put(ServiceAttributes.UI.BUBBLE_FIRST_LINE, "Smart Calendar");
                params.put(ServiceAttributes.UI.BUBBLE_SECOND_LINE,"Show Events");
                tidShowBubble.put(sid, createTask(sid, MethodConstants.UI_TYPE,
                        MethodConstants.UI_METHOD_SHOW_BUBBLE, params));
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == getTid(tidShowBubble, sid)){
            try {
                //if (params.get(BUBBLE_STATUS).equals(Bubble.M_CLICKED)){
                if (params.get(ServiceAttributes.UI.STATUS).equals("clicked")){
                    params.put("html_string", getHtml(eventListSortByTime(eventList.get(sid))));
                    tidShowHtml.put(sid, createTask(sid, MethodConstants.UI_TYPE,
                            MethodConstants.UI_METHOD_LOAD_WEBVIEW, params));
                } else {
                    endSession(sid);
                }
            }catch (Exception e){
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == getTid(tidShowHtml, sid)){
            Log.e(TAG, "Successfully Run Action");
            Log.e(TAG, "Ending session (triggerListShow)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }
        // Add Action
        if (tid == getTid(tidAddAction_ShowBubble, sid)){
            if (params.get(ServiceAttributes.UI.STATUS).equals("clicked")){       //BUBBLE_STATUS)==1
                params.put("calendar_extra_time_start",eventBeginTime);
                params.put("calendar_extra_time_end", eventEndTime);
                tidAddAction.put(sid, createTask(sid, MethodConstants.ACTION_TYPE,
                        MethodConstants.ACTION_METHOD_CALENDAR_NEW, params));
            } else {
                endSession(sid);
            }

        } else if (tid == getTid(tidAddAction, sid)){
            //Log.e(TAG, "Action Response:" + params.get(EntityAttributes.Action.RESULT));
            Log.e(TAG, "Ending session (triggerListAdd)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }
    }

    private ArrayList<Event> eventListSortByTime(ArrayList<Event> events){
        Collections.sort(events,new sortByTime());
        return events;

    }

    class sortByTime implements Comparator{
        public int compare(Object o1,Object o2){
            Event e1 = (Event) o1;
            Event e2 = (Event) o2;
            return e1.getBeginTime().compareTo(e2.getBeginTime());
        }
    }
    @Override
    protected void endSession(long sid) {
        tidPutTreeToGetTime.remove(sid); tidPutTreeToGetLocation.remove(sid); tidShowBubble.remove(sid);
        tidShowHtml.remove(sid); tidAddAction.remove(sid); tidAddAction_ShowBubble.remove(sid);
        eventList.remove(sid); tree1.remove(sid); tree2.remove(sid);
        // eventTimeString1.remove(sid);
        //eventTimeString2.remove(sid);
        eventBeginTime.remove(sid); eventEndTime.remove(sid);
        super.endSession(sid);
    }
}


