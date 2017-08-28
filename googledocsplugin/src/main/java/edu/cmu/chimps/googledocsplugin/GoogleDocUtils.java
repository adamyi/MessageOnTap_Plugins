package edu.cmu.chimps.googledocsplugin;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.ServiceAttributes;
import edu.cmu.chimps.messageontap_api.Tag;


public class GoogleDocUtils {

    public static final int ALL_DOC_NAME_ROOT_ID = 111;
    public static final int ALL_URL_ROOT_ID = 333;
    public static final int FILTERED_URL_ROOT_ID = 444;
    public static final String TAG = "GoogleDocPlugin";

    public static String getTimeString(HashMap<String, Object> params){
        ArrayList<Long> timeList = (ArrayList<Long>)params.get(ServiceAttributes.PMS.CURRENT_MESSAGE_EMBEDDED_TIME);
        return timeList.get(0)+ "," + timeList.get(1);
    }

    public static ParseTree addNameRoot(ParseTree tree , int Id, String time, Tag tag_time){
        SparseArray<ParseTree.Node> nodeList = tree.getNodeList();
        int key = 0;
        ParseTree.Node newNode = new ParseTree.Node();
        for(int i = 0; i < nodeList.size(); i++) {
            key = nodeList.keyAt(i);
            ParseTree.Node node = nodeList.get(key);
            if (node.getParentId() == -1) {
                node.setParentId(Id);
                newNode.setId(Id);
                newNode.setParentId(-1);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(ServiceAttributes.Graph.Document.TITLE);          //need to be changed to document.TITLE
                break;
            }
        }
        for(int i = 0; i < nodeList.size(); i++) {
            key = nodeList.keyAt(i);
            ParseTree.Node node = nodeList.get(key);
            if (node.getTagList().contains(tag_time) && !time.equals("")){
                node.getTagList().clear();
                node.setWord(time);
                node.addTag(ServiceAttributes.Graph.Document.CREATED_TIME);
                node.addTag(ServiceAttributes.Graph.Document.MODIFIED_TIME);
                break;
            } else {
                nodeList.delete(key);
            }
        }
        nodeList.put(Id, newNode);
        tree.setNodeList(nodeList);
        Log.e(TAG, "AddNameRoot:    (root added)the new tree is " + tree.getNodeList().toString());
        return tree;
    }

    public static ParseTree addUrlRoot(ParseTree tree, int Id, String time, Tag tag_time){
        for (int i=0; i < tree.getNodeList().size(); i++){
            ParseTree.Node node = tree.getNodeList().get(i);
            if (node.getParentId() == -1){
                node.setParentId(Id);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(Id);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(ServiceAttributes.Graph.Document.DESCRIPTION);           //need to change to Url
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

    public static String getHtml(ArrayList<Doc> DocList) {
        String list = "";
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
            list = list +

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

        return html + list + btn;

    }
}
