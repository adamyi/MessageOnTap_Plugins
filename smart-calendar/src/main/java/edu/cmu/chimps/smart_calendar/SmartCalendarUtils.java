package edu.cmu.chimps.smart_calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
