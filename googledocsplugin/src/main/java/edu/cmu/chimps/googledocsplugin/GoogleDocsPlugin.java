package edu.cmu.chimps.googledocsplugin;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.DataUtils;

import edu.cmu.chimps.messageontap_api.JSONUtils;

import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;


import static android.R.attr.id;
import static android.bluetooth.BluetoothAssignedNumbers.GOOGLE;
import static android.os.Build.VERSION_CODES.M;
import static edu.cmu.chimps.googledocsplugin.StringUtils.ALLDOCNAMEROOTID;
import static edu.cmu.chimps.googledocsplugin.StringUtils.ALLDOCROOT;
import static edu.cmu.chimps.googledocsplugin.StringUtils.ALLURLROOT;
import static edu.cmu.chimps.googledocsplugin.StringUtils.ALLURLROOTID;
import static edu.cmu.chimps.googledocsplugin.StringUtils.DOCNAMEROOTID;
import static edu.cmu.chimps.googledocsplugin.StringUtils.DOCROOT;
import static edu.cmu.chimps.googledocsplugin.StringUtils.URLROOT;
import static edu.cmu.chimps.googledocsplugin.StringUtils.URLROOTID;
import static edu.cmu.chimps.messageontap_api.ParseTree.Direction;
import static edu.cmu.chimps.messageontap_api.ParseTree.Mood;
import static edu.cmu.chimps.messageontap_api.ParseTree.Node;



public class GoogleDocsPlugin extends MessageOnTapPlugin {

    public static final String TAG = "GoogleDoc plugin";
    private Long tidFindAllDocName, tidFindDocName, tidFindUrl1, tidFindUrl2, tidBubble, tidDetails, tidDocSend;
    ParseTree tree1, tree2, treeForSearch1, treeForSearch2;

    private Tag TAG_FILENAME;
    Tag tag_doc = new Tag("TAG_DOC", new HashSet<>(Collections.singletonList(
            "(file|doc|document)")));
    Tag tag_I = new Tag("TAG_I", new HashSet<>(Collections.singletonList("I")));
    Tag tag_me = new Tag("TAG_ME", new HashSet<>(Collections.singletonList(
            "(us|me)")));
    Tag tag_send = new Tag("TAG_SEND", new HashSet<>(Collections.singletonList(
            "(share|send|show|give)")));
    Tag tag_time = new Tag("TAG_TIME", new HashSet<>(Collections.singletonList(
            "(tomorrow|AM|PM|am|pm|today|morning|afternoon|evening|night)")));
    Tag tag_you = new Tag("TAG_You", new HashSet<>(Collections.singletonList("you")));
    public int MOOD = 0; // 0 statement
    public int DIRECTION = 0; // 0 incoming
    public int COMPLETE = 0; // 0 is complete

// doc, file
    // optional flag month, date, regular expression different format

    /**
     * Return the trigger criteria of this plug-in. This will be called when
     * MessageOnTap is started (when this plugin is already enabled) or when
     * this plugin is being enabled.
     *
     * @return PluginData containing the trigger
     */
    @Override
    protected PluginData iPluginData() {
        Log.e(TAG, "getting plugin data");
        ArrayList<Trigger> triggerArrayList = new ArrayList<>();
        HashSet<Tag> mMandatory = new HashSet<>();
        HashSet<Tag> mOptional = new HashSet<>();

        // Category one: with file name
        // trigger 1: Can you send me XXX (a file)?
        COMPLETE = 0;
        mOptional.add(tag_you);
        mMandatory.add(tag_send);
        mOptional.add(tag_me);
        mOptional.add(tag_time);
        DIRECTION = 0;
        HashSet<Trigger.Constraint> constraints= new HashSet<>();
        Trigger trigger1 = new Trigger("doc_trigger_one",mMandatory,mOptional,constraints,
                Mood.UNKNOWN, Direction.INCOMING);
        triggerArrayList.add(trigger1);
        clearLists(mMandatory, mOptional);
        //trigger 4: I can send you XXX
        mMandatory.add(tag_I);
        mMandatory.add(tag_send);
        mOptional.add(tag_you);
        mOptional.add(tag_time);
        MOOD = 0;
        DIRECTION = 1;
        HashSet<Trigger.Constraint> constraints2= new HashSet<>();
        Trigger trigger2 = new Trigger("calendar_trigger_two",mMandatory,mOptional,constraints2,
                Mood.IMPERATIVE, Direction.OUTGOING);
        triggerArrayList.add(trigger2);
        // Category two: without file name
        // trigger 2: Can you send me the file on this topic
        // second example: send me the file please
        mMandatory.add(tag_send);
        mOptional.add(tag_me);
        mMandatory.add(tag_doc);
        mOptional.add(tag_time);
        DIRECTION = 0;
        HashSet<Trigger.Constraint> constraints3= new HashSet<>();
        Trigger trigger3 = new Trigger("calendar_trigger_three",mMandatory,mOptional,constraints3,
                Mood.UNKNOWN, Direction.INCOMING);
        triggerArrayList.add(trigger3);
        clearLists(mMandatory, mOptional);
        // trigger 3: I want to send you the doc we talked about earlier
        // second example: I'll share my document
        mOptional.add(tag_I);
        mMandatory.add(tag_send);
        mOptional.add(tag_you);
        mMandatory.add(tag_doc);
        mOptional.add(tag_time);
        DIRECTION = 1;
        MOOD = 0;
        HashSet<Trigger.Constraint> constraints4= new HashSet<>();
        Trigger trigger4 = new Trigger("calendar_trigger_four",mMandatory,mOptional,constraints4,
                Mood.IMPERATIVE, Direction.OUTGOING);
        triggerArrayList.add(trigger4);
        clearLists(mMandatory, mOptional);
        return new PluginData().trigger(new Trigger());
    }

    public void clearLists(HashSet<Tag> mMandatory, HashSet<Tag> mOptional) {
        mMandatory.clear();
        mOptional.clear();
    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        // TID is something we might need to implement stateflow inside a plugin.

        if (triggerListHasName.contains(params.get(Session.TRIGGER_SOURCE))){
            tree1 = params.get(Graph.SYNTAX_TREE);
            treeForSearch1 = AddNameRoot(tree1, ALLDOCNAMEROOTID);
            params.remove(Graph.SYNTAX_TREE);
            params.put(Graph.SYNTAX_TREE, treeForSearch1);
            tidFindAllDocName = newTaskResponsed(sid, MethodConstants.PERSONAL_GRAPE_TYPE, MethodConstants.GRAPH_RETRIEVAL, params);
        } else {
            tree2 = params.get(Graph.SYNTAX_TREE);
            treeForSearch2 = AddNameRoot(tree2, DOCNAMEROOTID);
            params.remove(Graph.SYNTAX_TREE);
            params.put(Graph.SYNTAX_TREE, treeForSearch2);
            tidFindDocName = newTaskResponsed(sid, MethodConstants.PERSONAL_GRAPE_TYPE, MethodConstants.GRAPH_RETRIEVAL, params);
        }
    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, JSONUtils.hashMapToString(params));

        ArrayList<Doc> DocList = new ArrayList<>();
        if (tid == tidFindAllDocName) {
            //getCardMessage and put it into params
            try {
                ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                for (HashMap<String, Object> card : cardList) {
                    for (Node node : tree1.getNodeList){
                        if (node.getWord().equals((String) card.get(Graph.Document.Name))){
                            Doc doc = new Doc();
                            doc.setDocName((String) card.get(Graph.Document.Name));
                            doc.setCreatedTime((Long)card.get(Graph.Document.CREATED_TIME));
                            //doc.setDocUrl((String)card.get(Graph.Document.URL));
                            DocList.add(doc);
                        }
                    }
                }
                if (!DocList.isEmpty()) {
                    tree1 = AddUrlRoot(tree1, ALLURLROOTID);
                    params.remove(Graph.SYNTAX_TREE);
                    params.put(Graph.SYNTAX_TREE, tree1);
                    tidFindUrl1 = newTaskResponsed(sid, MethodConstants.PERSONAL_GRAPE_TYPE, MethodConstants.GRAPH_RETRIEVAL, params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == tidFindDocName){
            try{
                ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                for (HashMap<String, Object> card : cardList) {
                    Doc doc = new Doc();
                    doc.setDocName((String) card.get(Graph.Document.TITLE));
                    doc.setCreatedTime((Long)card.get(Graph.Document.CREATED_TIME));
                    //doc.setDocUrl((String)card.get(Graph.Document.URL));
                    DocList.add(doc);
                }
                if (!DocList.isEmpty()) {
                    tree2 = AddUrlRoot(tree2, URLROOTID);
                    params.remove(Graph.SYNTAX_TREE);
                    params.put(Graph.SYNTAX_TREE, tree2);
                    tidFindUrl2 = newTaskResponsed(sid, MethodConstants.PERSONAL_GRAPE_TYPE, MethodConstants.GRAPH_RETRIEVAL, params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        }



        if ((tid == tidFindUrl1)||(tid == tidFindUrl2)){
            try{
                ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                for (HashMap<String, Object> card : cardList) {
                    for (Doc doc : DocList){
                        if (doc.getCreatedTime().equals(card.get(Graph.Document.CREATED_TIME))){
                            doc.setDocUrl((String)card.get(Graph.Document.URL));
                        }
                    }
                }
                if (!DocList.isEmpty()) {
                    params.put(BUBBLE_FIRST_LINE, "Show GoogleDocs name");
                    tidBubble = newTaskRequest(sid, MethodConstants.UI_SHOW, "Bubble", params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        }





        if (tid == tidBubble) {
            try {
                params.put("HTML Details", getHtml(DocList));
                tidDetails = newTaskRequest(sid, MethodConstants.UI_UPDATE, "html", params);
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == tidDetails){
            params.get("", );                     //get selected URL
            params.put("", );                      //send URL
            tidDocSend = newTaskRequest(sid, MethodConstants.ACTION, "Send Doc URL", params);
        } else if (tid == tidDocSend) {
            Log.e(TAG, "Ending session (triggerListShow)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }

    }

    private String getHtml(ArrayList<Doc> DocList) {
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
                            "<input name = \"GoogleDoc\" type=\"checkbox\" class = \"checkbox\">\n" +
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

    private ParseTree AddNameRoot(ParseTree tree , int Id){
        for (ParseTree.Node node : tree.getNodeList){
            if (node.getParentId() == 0){
                node.setParentId(Id);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(Id);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(Graph.Document.TITLE);
            }
            if (node.getTagList().contains(tag_time)){
                node.getTagList().clear();
                node.addTag(Graph.Document.CREATED_TIME);
                node.addTag(Graph.Document.MODIFIED_TIME);
                node.addTag(Graph.Document.DESCRIPTION_TIME);
            }
        }
        return tree;
    }

    private ParseTree AddUrlRoot(ParseTree tree, int Id){
        for (ParseTree.Node node : tree.getNodeList){
            if (node.getParentId() == 0){
                node.setParentId(Id);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(Id);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(Graph.Document.URL);
            }
            if (node.getTagList().contains(tag_time)){
                node.getTagList().clear();
                node.addTag(Graph.Document.CREATED_TIME);
                node.addTag(Graph.Document.MODIFIED_TIME);
                node.addTag(Graph.Document.DESCRIPTION_TIME);
            }
        }
        return tree;
    }
    

}

