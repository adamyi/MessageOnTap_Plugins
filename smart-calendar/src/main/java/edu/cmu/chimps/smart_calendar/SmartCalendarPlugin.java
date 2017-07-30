package edu.cmu.chimps.smart_calendar;

import android.text.Html;
import android.util.EventLogTags;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import edu.cmu.chimps.messageontap_api.DataUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;

import static android.R.attr.tag;
import static android.R.id.message;
import static edu.cmu.chimps.messageontap_api.EntityAttributes.Event.EVENT_TIME;


public class SmartCalendarPlugin extends MessageOnTapPlugin {

    public static final String TAG = "SmartCalendar plugin";
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    long TidShow1, TidShow2, TidShow3, TidAdd1, TidAdd2;

    public ArrayList<Trigger>  triggerListShow = new ArrayList<>();
    public ArrayList<Trigger>  triggerListAdd = new ArrayList<>();

    private Tree tree1,tree2;
    String EventTime1, EventTime2;


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
        // TODO: triggerListShow add entry

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
        // TODO: triggerListAdd add entry and triggerArrayList add these two lists
        ArrayList<String> holder = new ArrayList<>();
        return new PluginData().trigger(new Trigger(holder));
    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        // TID is something we might need to implement stateflow inside a plugin.

        if (triggerListShow.contains(params.get("trigger"))){               //有没有可能符合两个trigger？希望pms能每符合一个trigger就发一次init
            tree1 = (Tree)params.get("tree");
            EventTime1 = AddRootAndGetTime(tree1);                    //Retrieval events
            params.put("tree", tree1);

            TidShow1 = newTaskRequest(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);
        }
        if (triggerListAdd.contains(params.get("trigger"))){
            tree2 = (Tree)params.get("tree");
            for (Node node : tree2){
                if (node.getId() == EVENT_TIME_){
                    EventTime2 = node.getContent();
                }
            }
            params.put(BUBBLE_FIRST_LINE, "Add Calendar");
            params.put(BUBBLE_SECOND_LINE, "Event time:"+EventTime2);
            TidAdd1 = newTaskRequest(sid, MethodConstants.UI_SHOW, "BubbleShow", params);
        }
    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        ArrayList<String> eventList;
            if (tid == TidShow1) {
                //getCardMessage and put it into params
                eventList = new ArrayList<>();
                try {
                    ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                    for (HashMap<String, Object> card:cardList){
                        eventList.add((String)card.get("GRAPH_EVENT_NAME"));
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
                    params.put("HTML Details", getHtml(eventList, EventTime1));
                    TidShow3 = newTaskRequest(sid, MethodConstants.UI_UPDATE, "html", params);
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
                params.put("action:Add to calendar time", EventTime2);        //time 必须要精确到日期？
                TidAdd2 = newTaskRequest(sid, MethodConstants.ACTION, "params", params);
            } else if (tid == TidAdd2){
                Log.e(TAG, "Ending session (triggerListAdd)");
                endSession(sid);
                Log.e(TAG, "Session ended");
            }
    }

<<<<<<< HEAD
=======
    private String getHtml(ArrayList<String> eventList, String EventTime1){
        String html = "";
>>>>>>> 9f627f897018a8a318cb7e853218e1a6af9a577b

    protected String getHtml(ArrayList<String> eventList,String EventTime){



        int year;
        ////////////////年月日份表///////////////////
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
                "height:auto" +
                "}\n" + ".text{\n" +
                "\t\tmargin:10px;\n" +
                "\t}"+
                "</style>";
        ////////////////循环Events//////////////
        Iterator iterator = eventList.iterator();

        while (iterator.hasNext()) {
            String theEvent = (String) iterator.next();
            htmlString = htmlString + //if （year 与 之前加的不同）-》 + year框
                    "<div class=\"datashower\" >\n" +
                    // 加上Time and Event
                    "<p class = \"text\">"+ theEvent + "</p>\n" +            //time??  date = new Date(key)  date.getyear
                    "<p class = \"text\">"+ EventTime +"</p>\n" +  //event??
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



    private String AddRootAndGetTime(Tree tree1){
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

}

