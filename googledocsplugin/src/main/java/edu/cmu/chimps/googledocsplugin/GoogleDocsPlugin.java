package edu.cmu.chimps.googledocsplugin;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.DataUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;

import static edu.cmu.chimps.messageontap_api.ParseTree.Direction;
import static edu.cmu.chimps.messageontap_api.ParseTree.Mood;
import static edu.cmu.chimps.messageontap_api.ParseTree.Node;


public class GoogleDocsPlugin extends MessageOnTapPlugin {

    public static final String TAG = "GoogleDoc plugin";
    private Long TidFindAllDoc, TidFindDoc, TidBubble, TidDetails, TidDocSend;
    ParseTree tree1, tree2, treeForSearch;

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
        Log.e(TAG, DataUtils.hashMapToString(params));
        // TID is something we might need to implement stateflow inside a plugin.

        DocList = new ArrayList<>();
        params.put("FindDoc", DocList);

        //todo: if root is not googleDoc, add it
        //能不能找GoogleDoc？
        if (triggerListHasName.contains(params.get(Session.TRIGGER_SOURCE))){
            treeForSearch = new ParseTree();                              //Initial a new tree, only has one node
            Node newNode = new Node();
            treeForSearch.setNodeById(GOOGLEDOC_URL, newNode);
            tree1 = params.get(Graph.SYNTAX_TREE);
            params.remove(Graph.SYNTAX_TREE);
            params.put(Graph.SYNTAX_TREE, tree1);
            TidFindAllDoc = newTaskResponsed(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);
        } else {
            tree2 = params.get(Graph.SYNTAX_TREE);
            tree2 = AddRoot(tree2);
            params.put(Graph.SYNTAX_TREE, tree2);
            TidFindDoc = newTaskResponsed(sid, MethodConstants.PKG, MethodConstants.GRAPH_RETRIEVAL, params);
        }

    }

    @Override
    protected void newTaskResponsed(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, DataUtils.hashMapToString(params));

        ArrayList<Doc> DocList = new ArrayList<>();
        if (tid == TidFindAllDoc) {
            //getCardMessage and put it into params
            try {
                ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                String MessageDocName = tree1.FindNodeById(Id);
                for (HashMap<String, Object> card : cardList) {
                    if (MessageDocName.equals((String) card.get(Graph.Document.Name))){
                        Doc doc = new Doc();
                        doc.setDocName((String) card.get(Graph.Document.Name));
                        doc.setDocUrl((String)card.get(Graph.Document.URL));
                        DocList.add(doc);
                    }
                }
                if (!AllDocList.isEmpty()) {
                    params.put(BUBBLE_FIRST_LINE, "Show URL");
                    params.put(BUBBLE_SECOND_LINE, "Event time:" + EventTime1);
                    TidBubble = newTaskRequest(sid, MethodConstants.UI_SHOW, "Bubble", params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidFindDoc){
            try{
                ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>) params.get("Card");
                for (HashMap<String, Object> card : cardList) {
                    Doc doc = new Doc();
                    doc.setDocName((String) card.get(Graph.Document.Name));
                    doc.setDocUrl((String)card.get(Graph.Document.URL));
                    DocList.add(doc);
                }
                if (!DocList.isEmpty()) {
                    params.put(BUBBLE_FIRST_LINE, "Show URL");
                    params.put(BUBBLE_SECOND_LINE, "Event time:" + EventTime1);
                    TidBubble = newTaskRequest(sid, MethodConstants.UI_SHOW, "Bubble", params);
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        }




        if (tid == TidBubble) {
            try {
                params.put("HTML Details", getHtml(DocList));
                TidDetails = newTaskRequest(sid, MethodConstants.UI_UPDATE, "html", params);
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == TidDetails){
            params.get("", );                     //get selected URL
            params.put("", );                      //send URL
            TidDocSend = newTaskRequest(sid, MethodConstants.ACTION, "Send Doc URL", params);
        } else if (tid == TidDocSend) {
            Log.e(TAG, "Ending session (triggerListShow)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }


    }


    private ParseTree AddRoot(ParseTree tree){
        for (int i; i<tree.){
            if (node.getParent() == 0){
                node.setParent(ROOT);
                Node newNode = new Node();
                newNode.setId(Event Name);
                newNode.setParent(0);
                newNode.setChildren(node.getId());
                node.addTag(Graph.Document.URL);
            }
        }
        return tree;
    }

}

