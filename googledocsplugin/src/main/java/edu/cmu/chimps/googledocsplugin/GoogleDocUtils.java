package edu.cmu.chimps.googledocsplugin;

import java.util.ArrayList;
import java.util.HashMap;

import static edu.cmu.chimps.messageontap_api.EntityAttributes.CURRENT_MESSAGE_EMBEDDED_TIME;

/**
 * Created by knight006 on 8/1/2017.
 */

public class GoogleDocUtils {

    public static final int ALL_DOCNAME_ROOT_ID = 111;
    public static final int FILTERED_DOCNAME_ROOT_ID = 222;
    public static final int ALL_URL_ROOT_ID = 333;
    public static final int FILTERED_URL_ROOT_ID = 444;

    public static String getTimeString(HashMap<String, Object> params){
        Long[] timeArray = (Long[])params.get(CURRENT_MESSAGE_EMBEDDED_TIME);
        String time =  timeArray[0]+ "," + timeArray[1];
        return time;
    }

    public static String getHtml(ArrayList<Doc> DocList) {
        String List = "";
        String html = "<html>" +
                "<body>" +
                "<style>" +
                "datashower{" +
                "border-radius: 10px;" +
                "background: #08AED8;" +
                "height:auto;" +

                "}" +
                ".doc{" +
                "text-align: center;" +
                "color: aliceblue;" +
                "padding: 10px;" +
                "}" +
                ".checkbox{" +
                "float: left;" +
                "</style>" +
                "<div class=\"Title\" style=\"border:  groove\">\n" +
                "\t<p class=\"Tiltle\" style=\"text-align: center;font-family:'aguafina-script';\">Related Google Doc</p>\n" +
                "</div>\n" + "<form id=\"data\">\n";
        for (Doc doc:DocList) {
            String docName = doc.getDocName();
            List = List +

                    "<div class= \"datashower\">\n" +
                    "<p class=\"doc\">\n" +
                    "<input name = \""+ docName + "\"type=\"checkbox\" class = \"checkbox\">\n" +
                    docName + "</p>\n"+
                    "</div>\n";

        }
        String btn =
                "</form>\n"+
                        "<div style=\"text-align: center\">\n" +
                        "<button data=\"\">\n" +
                        "\tOK\n" +
                        "</button>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";

        String finalHtml = html + List + btn;
        return finalHtml;
    }
}
