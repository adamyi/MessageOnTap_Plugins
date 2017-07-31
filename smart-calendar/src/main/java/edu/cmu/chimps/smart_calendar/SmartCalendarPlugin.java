package edu.cmu.chimps.smart_calendar;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.DataUtils;
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


public class SmartCalendarPlugin extends MessageOnTapPlugin {

    public static final String TAG = "SmartCalendar plugin";
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    long TidShow1, TidShow2, TidShow3, TidAdd1, TidAdd2;

    public ArrayList<Trigger>  triggerListShow = new ArrayList<>();
    public ArrayList<Trigger>  triggerListAdd = new ArrayList<>();

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
        return new PluginDaa().trigger(new Trigger(holder));
    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        // TID is something we might need to implement stateflow inside a plugin.

        if (params.get(Session.TRIGGER_SOURCE).equals("Trigger name")){
            tree1 = (ParseTree)params.get(Graph.SYNTAX_TREE);
            Node EventTime = new Node();
            Node EventName = new Node();
            EventTime.setId(Root_ID);
            EventName.setId(Node_ID);
            EventTime.setParent(0);
            EventName.setParent(root);

            tree.setNodebyId(root,ROOT_ID);
            tree.setNodebyId(node,Node_ID);                    //Retrieval events
            params.put("tree", tree1);
            //Retrieval events
            params.put(Graph.SYNTAX_TREE, tree1);

            TidShow1 = newTaskRequest(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);
        }

        if (params.get(Session.TRIGGER_SOURCE).equals("Trigger name2")){
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
        Log.e(TAG, DataUtils.hashMapToString(params));

        ArrayList<ArrayList<Object>> eventList;
            if (tid == TidShow1) {
                //getCardMessage and put it into params
                eventList = new ArrayList<>();
                try {
                    ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                    for (HashMap<String, Object> card : cardList) {
                        ArrayList<Object> Events = new ArrayList<>();
                        Events.add((String) card.get("Graph.Event.Time"));
                        Events.add((ArrayList<Long>) card.get("Graph.Event.Name"));
                        eventList.add(Events);
                    }

                    if (!cardList.isEmpty()) {
                        params.put(BUBBLE_FIRST_LINE, "Show Calendar");
                        params.put(BUBBLE_SECOND_LINE, "Event time:"+EventTime1);
                        TidShow2 = newTaskRequest(sid, MethodConstants.UI_SHOW, "Bubble", params);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    endSession(sid);
                }
                TidShow2 = newTaskRequest(sid, MethodConstants.UI_SHOW, "paramsMessage", params);
            } else if (tid == TidShow2){
                try {
                    if (params.get(BUBBLE_STATUS) == 1){
                        params.put("HTML Details", getHtml(eventList, EventTime1));
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


    private String getHtml(ArrayList<ArrayList<Object>> eventList){
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
                "border-radius:5px\n" +

                "}\n" + ".text{\n" +
                "\t\tmargin:10px;\n" +
                "\t}" +
                "</style>";
        ////////////////Recycle Events//////////////
        Iterator iterator = eventList.iterator();

        while (iterator.hasNext()) {
            String theEvent = ((ArrayList<String>) iterator.next()).get(0);
            ArrayList<Long> Time = ((ArrayList<Long>) iterator.next());
            Long begintime = Time.get(0);
            Long endTime = Time.get(1);
            int height = (int) (endTime-begintime)/1000/3600*20;// ms->s->h->x20(20px/hour)


            htmlString = htmlString + //if （year 与 之前加的不同）-》 + year框
                    "<div class=\"datashower\" style=\"height:" + height + "\">\n" +
                    // 加上Time and Event
                    "<p class = \"text\">" + theEvent + "</p>\n" +            //time??  date = new Date(key)  date.getyear
                    "<p class = \"text\">" + begintime + "</p>\n" +
                    "<p class = \"text\">" + endTime + "</p>\n"+ //event??
                    //////////////
                    "</div>";

        }
/*
        for (int i = 0;i < html.size();i++){
            htmlString =
                    "<div class=\"datashower\" >\n" +
                    // 加上Time and Event
                    "<p>time</p>\n" +
                    "<p>Events</p>\n" +
                            /////////
                    "</div>";
        }
        */
///////ending/////////
        htmlString = htmlString + "</body> </html>";
        return htmlString;
    }



/*
    private String AddRootAndGetTime(ParseTree tree1){
        for (Node node: tree){
            if (node.getParent() == 0){
                node.setParent(ROOT);
                Node newNode = new Node();
                newNode.setId(Event Name);
                newNode.setParent(0);
                newNode.setChildren(node.getId());
                node.addTag("GRAPH_EVENT_TIME");
                EventTime1 = node.getContent();
            }
        }
        return EventTime1;
    }
   */

    private HashMap<String, Integer> getCurrentDate(){
        Calendar c = Calendar.getInstance();
        HashMap<String, Integer> Data = new HashMap<>();
        Data.put("year", c.get(Calendar.YEAR));
        Data.put("month", c.get(Calendar.MONTH));
        Data.put("day", c.get(Calendar.DAY_OF_MONTH));
        return Data;
    }

}

