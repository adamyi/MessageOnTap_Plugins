package edu.cmu.chimps.googledocsplugin;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.EntityAttributes;
import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Session;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;


import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.ALLDOCNAMEROOTID;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.ALLDOCROOT;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.ALLURLROOT;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.ALLURLROOTID;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.DOCNAMEROOTID;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.DOCROOT;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.URLROOT;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.URLROOTID;

import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.getHtml;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.getTimeString;
import static edu.cmu.chimps.messageontap_api.ParseTree.Direction;
import static edu.cmu.chimps.messageontap_api.ParseTree.Mood;



public class GoogleDocsPlugin extends MessageOnTapPlugin {

    public static final String TAG = "GoogleDoc plugin";
    private Long tidFindAllDocName, tidFindDocName, tidFindUrl1, tidFindUrl2, tidBubble, tidDetails, tidDocSend;
    ParseTree tree1, tree2, treeForSearch1, treeForSearch2;
    String DocTime1, DocTime2;
    StringBuilder selectedDocUrl = null;
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
        Trigger trigger1 = new Trigger("doc_trigger_one", mMandatory, mOptional, constraints,
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
        Trigger trigger2 = new Trigger("calendar_trigger_two", mMandatory, mOptional, constraints2,
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
        Trigger trigger3 = new Trigger("calendar_trigger_three", mMandatory, mOptional,
                constraints3, Mood.UNKNOWN, Direction.INCOMING);
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
        Trigger trigger4 = new Trigger("calendar_trigger_four", mMandatory, mOptional, constraints4,
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
            tree1 = (ParseTree) params.get(EntityAttributes.Graph.SYNTAX_TREE);
            DocTime1 = getTimeString(params);
            treeForSearch1 = AddNameRoot(tree1, ALLDOCNAMEROOTID, DocTime1);
            params.remove(EntityAttributes.Graph.SYNTAX_TREE);
            params.put(EntityAttributes.Graph.SYNTAX_TREE, treeForSearch1);
            tidFindAllDocName = newTaskRequest(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params);

        } else {
            tree2 = (ParseTree) params.get(EntityAttributes.Graph.SYNTAX_TREE);
            DocTime2 = getTimeString(params);
            treeForSearch2 = AddNameRoot(tree2, DOCNAMEROOTID, DocTime2);
            params.remove(EntityAttributes.Graph.SYNTAX_TREE);
            params.put(EntityAttributes.Graph.SYNTAX_TREE, treeForSearch2);
            tidFindDocName = newTaskRequest(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params);
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
                    for (int i=0; i < tree1.getNodeList().size(); i++){
                        ParseTree.Node node = tree1.getNodeList().get(i);
                        if (node.getWord().equals((String) card.get(EntityAttributes.Graph.Document.TITLE))){
                            Doc doc = new Doc();
                            doc.setDocName((String) card.get(EntityAttributes.Graph.Document.TITLE));
                            doc.setCreatedTime((Long)card.get(EntityAttributes.Graph.Document.CREATED_TIME));
                            //doc.setDocUrl((String)card.get(Graph.Document.URL));
                            DocList.add(doc);
                        }
                    }
                }
                if (!DocList.isEmpty()) {
                    tree1 = AddUrlRoot(tree1, ALLURLROOTID, DocTime1);
                    params.remove(EntityAttributes.Graph.SYNTAX_TREE);
                    params.put(EntityAttributes.Graph.SYNTAX_TREE, tree1);
                    tidFindUrl1 = newTaskRequest(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params);
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
                    doc.setDocName((String) card.get(EntityAttributes.Graph.Document.TITLE));
                    doc.setCreatedTime((Long)card.get(EntityAttributes.Graph.Document.CREATED_TIME));
                    //doc.setDocUrl((String)card.get(Graph.Document.URL));
                    DocList.add(doc);
                }
                if (!DocList.isEmpty()) {
                    tree2 = AddUrlRoot(tree2, URLROOTID, DocTime2);
                    params.remove(EntityAttributes.Graph.SYNTAX_TREE);
                    params.put(EntityAttributes.Graph.SYNTAX_TREE, tree2);
                    tidFindUrl2 = newTaskRequest(sid, MethodConstants.GRAPH_TYPE, MethodConstants.GRAPH_METHOD_RETRIEVE, params);
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
                        if (doc.getCreatedTime().equals(card.get(EntityAttributes.Graph.Document.CREATED_TIME))){
                            doc.setDocUrl((String)card.get(EntityAttributes.Graph.Document.URL));
                        }
                    }
                }
                if (!DocList.isEmpty()) {
                    params.put(BUBBLE_FIRST_LINE, "Show GoogleDocs name");
                    tidBubble = newTaskRequest(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_SHOW_BUBBLE, params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        }





        if (tid == tidBubble) {
            if (params.get(BUBBLE_STATUS) == 1) {
                try {
                    params.put("HTML Details", getHtml(DocList));
                    tidDetails = newTaskRequest(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_LOAD_WEBVIEW, params);
                } catch (Exception e) {
                    e.printStackTrace();
                    endSession(sid);
                }
            } else {
                endSession(sid);
            }
        } else if (tid == tidDetails){
            //get selected URL
            for (Doc doc:DocList){
            String status = (String) params.get(doc.getDocName());
                if (status.equals("on")){
                    selectedDocUrl.append(doc.getDocUrl());
                }
            }
            params.put("Action SetText", selectedDocUrl.toString());                      //send URL
            tidDocSend = newTaskRequest(sid, MethodConstants.ACTION_TYPE, MethodConstants.ACTION_METHOD_SETTEXT, params);
        } else if (tid == tidDocSend) {
            Log.e(TAG, "Ending session (triggerListShow)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }

    }


    private ParseTree AddNameRoot(ParseTree tree , int Id, String time){
        for (int i=0; i < tree.getNodeList().size(); i++){
            ParseTree.Node node = tree.getNodeList().get(i);
            if (node.getParentId() == 0){
                node.setParentId(Id);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(Id);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(EntityAttributes.Graph.Document.TITLE);
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

    private ParseTree AddUrlRoot(ParseTree tree, int Id, String time){
        for (int i=0; i < tree.getNodeList().size(); i++){
            ParseTree.Node node = tree.getNodeList().get(i);
            if (node.getParentId() == 0){
                node.setParentId(Id);
                ParseTree.Node newNode = new ParseTree.Node();
                newNode.setId(Id);
                newNode.setParentId(0);
                Set<Integer> set = new HashSet<>();
                set.add(node.getId());
                newNode.setChildrenIds(set);
                newNode.addTag(EntityAttributes.Graph.Document.URL);
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
    

}

