package edu.cmu.chimps.smart_calendar;

import android.util.SparseArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.ServiceAttributes;
import edu.cmu.chimps.messageontap_api.Tag;


public class SmartCalendarUtils {
    public static final int NAME_ROOT_ID = 45;
    public static final int LOCATION_ROOT_ID = 67;

    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";

    public static Long getTid (HashMap<Long, Long> map, Long sid){
        if (map.get(sid) != null){
            return map.get(sid);
        }
        return (Long) (long) -1;
    }


    public static String getTimeString(HashMap<String, Object> params){
        ArrayList<Long> messageTime = (ArrayList<Long>) params.get(ServiceAttributes.PMS.CURRENT_MESSAGE_EMBEDDED_TIME);      //CURRENT_MESSAGE_EMBEDDED_TIME
        StringBuilder timeString =  new StringBuilder();
        try {
            timeString.append(messageTime.get(0)).append(",").append(messageTime.get(1));
        } catch (Exception e){
            e.printStackTrace();
        }


        return  timeString.toString();
    }



    public static ParseTree AddRootEventName(ParseTree tree, String time, Tag tag_time){
        SparseArray<ParseTree.Node> nodeList = tree.getNodeList();
        int key = 0;
        for(int i = 0; i < nodeList.size(); i++) {
            key = nodeList.keyAt(i);
            ParseTree.Node node = nodeList.get(key);
            if (node.getParentId() == -1) {
                node.setParentId(NAME_ROOT_ID);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(NAME_ROOT_ID);
                newNode.setParentId(-1);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(ServiceAttributes.Graph.Event.NAME);
                nodeList.put(NAME_ROOT_ID, newNode);
                tree.setNodeList(nodeList);
                break;
            }
        }
        for(int i = 0; i < nodeList.size(); i++) {
            key = nodeList.keyAt(i);
            ParseTree.Node node = nodeList.get(key);
            if (node.getTagList().contains(tag_time)){
                node.getTagList().clear();
                node.setWord(time);                         //The former root "time" need to be added a real time
                node.addTag(ServiceAttributes.Graph.Event.TIME);
            } else {
                nodeList.delete(key);
            }
        }
        return tree;
    }

    public static ParseTree AddRootLocation(ParseTree tree, String time, Tag tag_time){
        for (int i=0; i < tree.getNodeList().size(); i++){
            ParseTree.Node node = tree.getNodeList().get(i);
            if (node.getParentId() == -1){
                node.setParentId(LOCATION_ROOT_ID);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(LOCATION_ROOT_ID);
                newNode.setParentId(-1);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(ServiceAttributes.Graph.Place.NAME);
            }
            if (node.getTagList().contains(tag_time)){
                node.getTagList().clear();
                node.setWord(time);
                node.addTag(ServiceAttributes.Graph.Document.CREATED_TIME);
                node.addTag(ServiceAttributes.Graph.Document.MODIFIED_TIME);
            }
        }
        return tree;
    }

    public static ArrayList<Event> getEventList(HashMap<String, Object> params){
        ArrayList<Event> EventList = new ArrayList<>();
        ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>)JSONUtils.jsonToSimpleObject((String)params.get(ServiceAttributes.Graph.CARD_LIST), JSONUtils.TYPE_CARD_LIST);
        for (HashMap<String, Object> card : cardList) {
            Event event = new Event();
            event.setEventName((String) card.get(ServiceAttributes.Graph.Event.NAME));
            ArrayList<Long> Time = (ArrayList<Long>) card.get(ServiceAttributes.Graph.Event.TIME);
            event.setBeginTime(Time.get(0));
            event.setEndTime(Time.get(1));

            EventList.add(event);
        }
        return EventList;
    }

    public static void setListLocation(ArrayList<Event> EventList, HashMap<String, Object> params){
        ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get(ServiceAttributes.Graph.CARD_LIST);
        for (HashMap<String, Object> card : cardList) {
            if (card.get(ServiceAttributes.Graph.Event.START_TIME).equals(EventList.get(cardList.indexOf(card)).getBeginTime())){
                EventList.get(cardList.indexOf(card)).setLocation((String) card.get(ServiceAttributes.Graph.Place.NAME));
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
               "<form class = \"eventform\">";
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
           // year = beginT.get(Calendar.YEAR);
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            String finalBeginTime = fmt.format(begintime);
            String finalEndTime = fmt.format(endTime);
            int height = (beginHour-endHour) * 20;// ms->s->h->x20(20px/hour)
            if (height < 75){
                height = -1;
            }
            String h;
            if (height == -1){
                h = "auto";
            }else
            {   h = "" + height + "px";}

            htmlString = htmlString +
                    "<div class=\"datashower\" style=\"height:" + h + ";background:#08AED8;" + "border-radius:5px;\n" +
                    "color: aliceblue;" + "\">\n" +
                    // 加上Time and Event
                    "<h class=\"text\" style=\"text-align:left; margin:10px;\">" + finalBeginTime + "</h >\n" +
                    "<p class=\"text\" style=\"text-align:center;margin:10px;\">" + theEvent +
                    "<input id=\"pluginForm\"type=\"checkbox\" style=\"float:right;\" class=\"checkbox\""+ "name=\""+ theEvent + "\"></p >\n" +
                    "<h class=\"text\" style=\"text-align:left;margin:10px;\">" + finalEndTime +
                    "</h>\n"+ "<h style=\"float: right;margin-right:10px;\">" + location + "</h>"+
                    //////////////
                    "</div>";
        }

        ///////ending/////////
        htmlString = htmlString +  "<div style=\"text-align: center\">\n" +

                "<input type=\"submit\" class=\"pluginButton\" style=\"" +
                "  background: #3498db;\n" +
                "  background-image: -webkit-linear-gradient(top, #3498db, #2980b9);\n" +
                "  background-image: -moz-linear-gradient(top, #3498db, #2980b9);\n" +
                "  background-image: -ms-linear-gradient(top, #3498db, #2980b9);\n" +
                "  background-image: -o-linear-gradient(top, #3498db, #2980b9);\n" +
                "  background-image: linear-gradient(to bottom, #3498db, #2980b9);\n" +
                "  -webkit-border-radius: 28;\n" +
                "  -moz-border-radius: 28;\n" +
                "  border-radius: 28px;\n" +
                "  font-family: Arial;\n" +
                "  color: #ffffff;\n" +
                "  font-size: 10px;\n" +
                "  background: #3498db;\n" +
                "  padding: 10px 20px 10px 20px;\n" +
                "  text-decoration: none;\n" +
                "\n" +
                "hover {\n" +
                "  text-decoration: none;\n" +
                "}\" value=\"Cancel\">\n" +
                "\n" +
                "</div>"+"</form>"+"</body> </html>";
        return htmlString;
    }
}