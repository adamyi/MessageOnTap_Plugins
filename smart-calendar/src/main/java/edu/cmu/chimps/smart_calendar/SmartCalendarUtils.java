package edu.cmu.chimps.smart_calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.EntityAttributes;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.Tag;

import static edu.cmu.chimps.messageontap_api.EntityAttributes.CURRENT_MESSAGE_EMBEDDED_TIME;

/**
 * Created by knight006 on 8/1/2017.
 */

public class SmartCalendarUtils {
    public static final int NAME_ROOT_ID = 45;
    public static final int LOCATION_ROOT_ID = 67;

    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";

    public static String getTimeString(HashMap<String, Object> params){
        Long[] timeArray = (Long[])params.get(CURRENT_MESSAGE_EMBEDDED_TIME);
        String time =  timeArray[0]+ "," + timeArray[1];
        return time;
    }



    public static ParseTree AddRootEventName(ParseTree tree, String time, Tag tag_time){
        for (int i=0; i < tree.getNodeList().size(); i++){
            ParseTree.Node node = tree.getNodeList().get(i);
            if (node.getParentId() == 0){
                node.setParentId(NAME_ROOT_ID);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(NAME_ROOT_ID);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(EntityAttributes.Graph.Event.NAME);
            }
            if (node.getTagList().contains(tag_time)){
                node.getTagList().clear();
                node.setWord(time);                         //The former root "time" need to be added a real time
                node.addTag(EntityAttributes.Graph.Event.TIME);
                node.addTag(EntityAttributes.Graph.Event.NAME);
            }
        }
        return tree;
    }

    public static ParseTree AddRootLocation(ParseTree tree, String time, Tag tag_time){
        for (int i=0; i < tree.getNodeList().size(); i++){
            ParseTree.Node node = tree.getNodeList().get(i);
            if (node.getParentId() == 0){
                node.setParentId(LOCATION_ROOT_ID);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(LOCATION_ROOT_ID);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(EntityAttributes.Graph.Place.NAME);
            }
            if (node.getTagList().contains(tag_time)){
                node.getTagList().clear();
                node.setWord(time);
                node.addTag(EntityAttributes.Graph.Document.CREATED_TIME);
                node.addTag(EntityAttributes.Graph.Document.MODIFIED_TIME);
            }
        }
        return tree;
    }

    public static ArrayList<Event> getEventList(HashMap<String, Object> params){
        ArrayList<Event> EventList = new ArrayList<>();
        ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get(EntityAttributes.Graph.CARD_LIST);
        for (HashMap<String, Object> card : cardList) {
            Event event = new Event();
            event.setEventName((String) card.get(EntityAttributes.Graph.Event.NAME));
            event.setBeginTime((Long) card.get(EntityAttributes.Graph.Event.START_TIME));
            event.setEndTime((Long) card.get(EntityAttributes.Graph.Event.END_TIME));
            EventList.add(event);
        }
        return EventList;
    }

    public static void setListLocation(ArrayList<Event> EventList, HashMap<String, Object> params){
        ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get(EntityAttributes.Graph.CARD_LIST);
        for (HashMap<String, Object> card : cardList) {
            if (card.get(EntityAttributes.Graph.Event.START_TIME).equals(EventList.get(cardList.indexOf(card)).getBeginTime())){
                EventList.get(cardList.indexOf(card)).setLocation((String) card.get(EntityAttributes.Graph.Place.NAME));
            }
        }
    }




    public static Calendar getDate(Long time){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
        return date;
    }


    public static String getHtml(ArrayList<Event> eventList){

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

            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
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
}
