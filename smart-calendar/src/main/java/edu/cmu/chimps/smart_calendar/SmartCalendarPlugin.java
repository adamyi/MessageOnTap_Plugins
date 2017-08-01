package edu.cmu.chimps.smart_calendar;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.DataUtils;
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

import static edu.cmu.chimps.messageontap_api.ParseTree.Direction;
import static edu.cmu.chimps.messageontap_api.ParseTree.Node;
import java.util.Collections;

public class SmartCalendarPlugin extends MessageOnTapPlugin {

    class SortbyTime implements Comparator{
        public int compare(Object o1,Object o2){
            Event e1 = (Event) o1;
            Event e2 = (Event) o2;
            return e1.getBeginTime().compareTo(e2.getBeginTime());
        }
    }

    public static final String TAG = "SmartCalendar plugin";
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    long TidShow0, TidShow1, TidShow2, TidShow3, TidAdd1, TidAdd2;

    ArrayList<Event> EventList;


    private ParseTree tree1,tree2;
    String EventTime1, EventTime2;

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

    public void clearLists(Set<Tag> mMandatory, Set<Tag> mOptional){
        mMandatory.clear();
        mOptional.clear();
    }
    @Override
    protected PluginData iPluginData() {
        Log.e("plugin", "getting plugin data");
        ArrayList<Trigger> triggerArrayList = new ArrayList<>();
        Set<Tag> mMandatory = new HashSet<>();
        Set<Tag> mOptional = new HashSet<>();

        // Category one: show calendar
        // trigger1: are you free tomorrow? incoming
        mMandatory.add(tag_you);
        mMandatory.add(tag_free);
        mMandatory.add(tag_time);
        mOptional.add(tag_optional_time);
        HashSet<Trigger.Constraint> constraints= new HashSet<>();
        Trigger trigger1 = new Trigger("calendar_trigger_one",mMandatory,mOptional,constraints,
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
        Trigger trigger2 = new Trigger("calendar_trigger_two",mMandatory,mOptional,constraints2,
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
        Trigger trigger3 = new Trigger("calendar_trigger_three",mMandatory,mOptional,constraints3,
                Mood.UNKNOWN, Direction.UNKNOWN);
        triggerArrayList.add(trigger3);
        // TODO: create trigger and add it to triggerArrayList
        clearLists(mMandatory,mOptional);

        // trigger 4: Can you (I) pick it up this afternoon? Incoming
        mOptional.add(tag_I);
        mOptional.add(tag_you);
        mOptional.add(tag_optional_time);
        mMandatory.add(tag_time);
        HashSet<Trigger.Constraint> constraints4= new HashSet<>();
        Trigger trigger4 = new Trigger("calendar_trigger_four",mMandatory,mOptional,constraints4,
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

        if (params.get(Session.TRIGGER_SOURCE).equals("calendar_trigger_one")||
                params.get(Session.TRIGGER_SOURCE).equals("calendar_trigger_two")){
            tree1 = (ParseTree)params.get(Graph.SYNTAX_TREE);
            params.remove(Graph.SYNTAX_TREE);
            params.put(Graph.SYNTAX_TREE, AddRootEventName(tree1));
            TidShow0 = newTaskRequest(sid, MethodConstants.PERSONAL_GRAPE_TYPE, MethodConstants.GRAPH_RETRIEVAL, params);
        }

        if (params.get(Session.TRIGGER_SOURCE).equals("calendar_trigger_three")||
                params.get(Session.TRIGGER_SOURCE).equals("calendar_trigger_four")){
            tree2 = (ParseTree)params.get(Graph.SYNTAX_TREE);
            EventTime2 = params.get(CURRENT_MESSAGE_EMBEDDED_TIME);
            params.put(BUBBLE_FIRST_LINE, "Add Calendar");
            params.put(BUBBLE_SECOND_LINE, "Event time:"+EventTime2);
            TidAdd1 = newTaskRequest(sid, MethodConstants.UI_SHOW, "BubbleShow", params);
        }
    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, JSONUtils.hashMapToString(params));

        if (tid == TidShow0){
            try{
                EventList = getEventList(params);
                params.remove(Graph.SYNTAX_TREE);
                params.put(Graph.SYNTAX_TREE, AddRootLocation(tree1));
                TidShow0 = newTaskRequest(sid, MethodConstants.PERSONAL_GRAPE_TYPE, MethodConstants.GRAPH_RETRIEVAL, params);

            }catch (Exception e){
                e.printStackTrace();
                endSession(sid);
            }

        } else if (tid == TidShow1) {
            //getCardMessage and put it into params
            try {
                setLocation(params);
                params.put(BUBBLE_FIRST_LINE, "Show Calendar");
                params.put(BUBBLE_SECOND_LINE, "Event time:"+EventTime1);
                TidShow2 = newTaskRequest(sid, MethodConstants.UI_SHOW, "Bubble", params);
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidShow2){
            try {
                if (params.get(BUBBLE_STATUS) == 1){
                    params.put("HTML Details", getHtml(EventListSortByTime(EventList)));
                    TidShow3 = newTaskRequest(sid, MethodConstants.UI_UPDATE, "html", params);
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
            ArrayList<Long> time = (ArrayList<Long>) params.get("time");
            params.put("action:Add to calendar time", time.get(0));        //time 必须要精确到日期？
            TidAdd2 = newTaskRequest(sid, MethodConstants.ACTION, "params", params);
        } else if (tid == TidAdd2){
            Log.e(TAG, "Ending session (triggerListAdd)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }
    }

    private String getHtml(ArrayList<Event> eventList){

        String html = "";

        int year;

        ////////////////Time///////////////////
        String yeartablehtml = ".year{\n" +
                "\t\tbackground: #39A90E;\n" +
                "\t\theight:auto;\n" +
                "\t\tborder-radius:5px;\n" +
                "\t\ttext-align: center;\n" +
                "\t}";
        /////////////////style/////////////////////
        String htmlString = "<html>\n" +
                "<style>\n" +
                ".datashower{\n" +
                "background:#08AED8;\n" +
                "border-radius:5px;\n" +
                "color: aliceblue;" +
                "}\n" + ".text{\n" +
                "\t\tmargin:10px;\n" +
                "\t}" + ".checkbox{\n" +
                "\t\t float:right\n" +
                "\t}" +
                "</style>";
        ////////////////Recycle Events//////////////
        for (Event event: eventList){

            String theEvent = event.getEventName();


            Long begintime = event.getBeginTime();
            Long endTime = event.getEndTime();
            String location = event.getLocation();
            Calendar beginT = Calendar.getInstance();
            beginT.setTimeInMillis(begintime);

            Calendar endT = Calendar.getInstance();
            endT.setTimeInMillis(endTime);

            int beginHour = beginT.get(Calendar.HOUR_OF_DAY);
            int endHour = endT.get(Calendar.HOUR_OF_DAY);

            SimpleDateFormat fmt = new SimpleDateFormat("HH-mm");
            String finalBeginTime = fmt.format(begintime);
            String finalEndTime = fmt.format(endTime);





            int height = (int) (beginHour-endHour)*20;// ms->s->h->x20(20px/hour)
            if (height < 75){
                height = -1;
            }

            String h;
            if (height == -1){
                h = "auto";
            }else
            {   h = "" + height + "px";}

            htmlString = htmlString +
                    "<div class=\"datashower\" style=\"height:" + h + "\";>\n" +
                    // 加上Time and Event
                    "<h class = \"text\" style = \"text-align:left;\">" + finalBeginTime + "</h >\n" +
                    "<p class = \"text\" style = \"text-align:center;\">" + theEvent + "<input type=\"checkbox\" class = \"checkbox\">"+"</p >\n" +
                    "<h class = \"text\" style = \"text-align:left;\">" + finalEndTime +
                    "</h >\n"+ "<h style=\"float: right;margin-right:10px;\">" + location + "</h>"+
                    //////////////
                    "</div>";
        }

        ///////ending/////////
        htmlString = htmlString + "<div style=\"text-align: center\">\n" +

                "<button class=\"data\" style=\"\" data=\"\">\n" +
                "\tOK\n" +
                "</button>\n" +
                "</div>"+"</body> </html>";
        return htmlString;
    }

    private ArrayList<Event> EventListSortByTime(ArrayList<Event> events){
        Collections.sort(events,new SortbyTime());
        return events;

    }


    private ParseTree AddRootEventName(ParseTree tree){
        for (ParseTree.Node node : tree.getNodeList){
            if (node.getParentId() == 0){
                node.setParentId(3232);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(3232);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(Graph.Event.NAME);
            }
        }
        return tree;
    }

    private ParseTree AddRootLocation(ParseTree tree){
        for (ParseTree.Node node : tree.getNodeList){
            if (node.getParentId() == 0){
                node.setParentId(213123);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(213123);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(Graph.Place.NAME);
            }
        }
        return tree;
    }

    private ArrayList<Event> getEventList(HashMap<String, Object> params){
        ArrayList<Event> EventList = new ArrayList<>();
        ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
        for (HashMap<String, Object> card : cardList) {
            Event event = new Event();
            event.setEventName((String) card.get(Graph.Document.Name));
            event.setBeginTime((String)card.get(Graph.Event.START_TIME));
            event.setEndTime((String)card.get(Graph.Event.END_TIME));
            EventList.add(event);
        }
        return EventList;
    }

    private void setLocation(HashMap<String, Object> params){
        ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
        for (HashMap<String, Object> card : cardList) {
            if ((String)card.get(Graph.Event.START_TIME).equals(EventList.get(cardList.indexOf(card)).getBeginTime())){
                EventList.get(cardList.indexOf(card)).setLocation(card.get(Graph.Place.NAME));
            }
        }
    }


}


