package edu.cmu.chimps.googledocsplugin;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.ParseTree;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.ServiceAttributes;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;


import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.ALL_DOC_NAME_ROOT_ID;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.ALL_URL_ROOT_ID;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.addNameRoot;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.addUrlRoot;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.FILTERED_URL_ROOT_ID;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.getHtml;
import static edu.cmu.chimps.googledocsplugin.GoogleDocUtils.getTimeString;
import static edu.cmu.chimps.messageontap_api.ParseTree.Direction;
import static edu.cmu.chimps.messageontap_api.ParseTree.Mood;



public class GoogleDocsPlugin extends MessageOnTapPlugin {

    public static final String TAG = "GoogleDoc plugin";
    HashMap<Long, Long> mTidFindAllDocName = new HashMap<>();
    HashMap<Long, Long> mTidFindDocName = new HashMap<>();
    HashMap<Long, Long> mTidFindUrl1 = new HashMap<>();
    HashMap<Long, Long> mTidFindUrl2 = new HashMap<>();
    HashMap<Long, Long> mTidBubble = new HashMap<>();
    HashMap<Long, Long> mTidDetails = new HashMap<>();
    HashMap<Long, Long> mTidDocSend = new HashMap<>();

    HashMap<Long, ParseTree> mTree1 = new HashMap<>();
    HashMap<Long, ParseTree> mTree2 = new HashMap<>();
    HashMap<Long, ParseTree> mTreeForSearch1 = new HashMap<>();
    HashMap<Long, ParseTree> mTreeForSearch2 = new HashMap<>();
    HashMap<Long, String> mDocTime1 = new HashMap<>();
    HashMap<Long, String> mDocTime2 = new HashMap<>();
    HashMap<Long, StringBuilder> mSelectedDocUrl = new HashMap<>();
    ArrayList<Trigger> mTriggerListHasName = new ArrayList<>();
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
    public int mood = 0; // 0 statement
    public int direction = 0; // 0 incoming
    public int complete = 0; // 0 is complete


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
        Set<Tag> tagList = new HashSet<>(Arrays.asList(tag_I, tag_doc, tag_me, tag_send,
                 tag_time, tag_you));
        HashSet<String> mMandatory = new HashSet<>();
        HashSet<String> mOptional = new HashSet<>();
        // Category one: with file name
        // trigger 1: Can you send me XXX (a file)?  incoming
        complete = 0;
        //mOptional.add("TAG_You");
        mMandatory.add("TAG_SEND");
        mMandatory.add("TAG_You");
        mOptional.add("TAG_ME");
        mOptional.add("TAG_TIME");
        direction = 0;
        HashSet<Trigger.Constraint> constraints = new HashSet<>();
        Trigger trigger1 = new Trigger("doc_trigger_one", mMandatory, mOptional);
        //triggerArrayList.add(trigger1);                  //message with file name is not available now
        clearLists(mMandatory, mOptional);
        //trigger 2: I can send you XXX
        mMandatory.add("TAG_I");
        mMandatory.add("TAG_SEND");
        mOptional.add("TAG_You");
        mOptional.add("TAG_TIME");
        mood = 0;
        direction = 1;
        HashSet<Trigger.Constraint> constraints2 = new HashSet<>();
        Trigger trigger2 = new Trigger("doc_trigger_two", mMandatory, mOptional, constraints2,
                Mood.IMPERATIVE, Direction.OUTGOING);
        //triggerArrayList.add(trigger2);
        clearLists(mMandatory, mOptional);

        // Category two: without file name
        // trigger 3: Can you send me the file on this topic
        // second example: send me the file please
        mMandatory.add("TAG_SEND");
        mOptional.add("TAG_ME");
        mMandatory.add("TAG_DOC");
        mOptional.add("TAG_TIME");
        direction = 0;
        HashSet<Trigger.Constraint> constraints3 = new HashSet<>();
        Trigger trigger3 = new Trigger("doc_trigger_three", mMandatory, mOptional);//, constraints3, mood.UNKNOWN, direction.INCOMING);
        triggerArrayList.add(trigger3);
        clearLists(mMandatory, mOptional);
        // trigger 4: I want to send you the doc we talked about earlier
        // second example: I'll share my document
        mOptional.add("TAG_I");
        mMandatory.add("TAG_SEND");
        mOptional.add("TAG_You");
        mMandatory.add("TAG_DOC");
        mOptional.add("TAG_TIME");
        direction = 1;
        mood = 0;
        HashSet<Trigger.Constraint> constraints4 = new HashSet<>();
        Trigger trigger4 = new Trigger("doc_trigger_four", mMandatory, mOptional, constraints4,
                Mood.IMPERATIVE, Direction.OUTGOING);
        triggerArrayList.add(trigger4);
        mTriggerListHasName.add(trigger1);
        mTriggerListHasName.add(trigger2);
        clearLists(mMandatory, mOptional);
        Log.e(TAG, "returning plugin data");
        return new PluginData().tagSet(JSONUtils.simpleObjectToJson(tagList, JSONUtils.TYPE_TAG_SET))
                .triggerSet(JSONUtils.simpleObjectToJson(triggerArrayList, JSONUtils.TYPE_TRIGGER_SET));

    }

    public void clearLists(HashSet<String> mMandatory, HashSet<String> mOptional) {
        mMandatory.clear();
        mOptional.clear();
    }


    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        // TID is something we might need to implement stateflow inside a plugin.

        /*
         * Divide all triggers into two groups, include those whose message contains a whole DocName
         * and those whose message only contains terms like doc or file.
         * No matter which group was triggered, plugin is requested to query twice. In the first time
         * the root is DocName, and in the second time the root is DocUrl.
         * The difference between two groups is, if the message contains DocName, plugin have to
         * query all the user's DocNames, and judge whether the message contains one of them, after that can
         * the plugin step forward.
         */
        if ((ContactsReceiver.contactList==null) || !ContactsReceiver.contactList.contains((String)params.get(ServiceAttributes.PMS.CURRENT_MESSAGE_CONTACT_NAME))){
            //Toast.makeText(this, ContactsReceiver.contactList.toString(), Toast.LENGTH_SHORT).show();
            try{
                Log.e(TAG, " contact not matched ..... contactList is " + ContactsReceiver.contactList.toString());
            } catch (Exception e){
                Log.e(TAG, " contact not matched ..... contactlist is empty");
            }
            endSession(sid);
        }
        Log.e(TAG, "initNewSession: contact matched");
        if (params.get(ServiceAttributes.PMS.TRIGGER_SOURCE).equals("doc_trigger_one")||
                params.get(ServiceAttributes.PMS.TRIGGER_SOURCE).equals("doc_trigger_two")){

            mTree1.put(sid, (ParseTree)JSONUtils.jsonToSimpleObject((String)params
                    .get(ServiceAttributes.PMS.PARSE_TREE), JSONUtils.TYPE_PARSE_TREE));

            try{
                mDocTime1.put(sid, getTimeString(params));
            } catch (Exception e){
                mDocTime1.put(sid, "");
            }

            mTreeForSearch1.put(sid, addNameRoot(mTree1.get(sid), ALL_DOC_NAME_ROOT_ID, mDocTime1.get(sid), tag_time));
            params.remove(ServiceAttributes.PMS.PARSE_TREE);

            params.put(ServiceAttributes.PMS.PARSE_TREE,
                    JSONUtils.simpleObjectToJson(mTreeForSearch1.get(sid), JSONUtils.TYPE_PARSE_TREE));

            mTidFindAllDocName.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE,
                    MethodConstants.GRAPH_METHOD_RETRIEVE, params));
        } else {
            mTree2.put(sid, (ParseTree)JSONUtils.jsonToSimpleObject((String)params
                    .get(ServiceAttributes.PMS.PARSE_TREE), JSONUtils.TYPE_PARSE_TREE));

            Log.e(TAG, "initNewSession:    original mTree2 is : " + params.get(ServiceAttributes.PMS.PARSE_TREE).toString());

            try{
                mDocTime2.put(sid, getTimeString(params));
            } catch (Exception e){
                mDocTime2.put(sid, "");
            }

            final int timeNodeID = 1567;
            final int nameNodeID = 3726;
            ParseTree.Node timeNode = new ParseTree.Node();
            timeNode.setWord(mDocTime2.get(sid));
            Log.e(TAG,getTimeString(params));
            Set<String> set = new HashSet<>();
            set.add(ServiceAttributes.Graph.Event.TIME);
            timeNode.setTagList(set);
            timeNode.setId(timeNodeID);
            timeNode.setParentId(nameNodeID);
            ParseTree.Node nameNode = new ParseTree.Node();


            Set<String> set2 = new HashSet<>();
            set2.add(ServiceAttributes.Graph.Event.NAME);
            nameNode.setTagList(set2);
            nameNode.setId(nameNodeID);
            nameNode.setParentId(-1);

            Set<Integer> set3 = new HashSet<>();
            set3.add(timeNodeID);
            nameNode.setChildrenIds(set3);

            SparseArray<ParseTree.Node> array = new SparseArray<>();
            array.put(timeNodeID, timeNode);
            array.put(nameNodeID, nameNode);
            mTree2.get(sid).setNodeList(array);

//            mTreeForSearch2.put(sid, AddNameRoot(mTree2.get(sid), FILTERED_DOCNAME_ROOT_ID, mDocTime2.get(sid), tag_time));
//            params.remove(ServiceAttributes.PMS.PARSE_TREE);

            params.put(ServiceAttributes.PMS.PARSE_TREE,
                    JSONUtils.simpleObjectToJson(mTree2.get(sid), JSONUtils.TYPE_PARSE_TREE));

            mTidFindDocName.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE,
                    MethodConstants.GRAPH_METHOD_RETRIEVE, params));
        }
    }

    @Override
    protected void newTaskResponded(long sid, long tid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Got task response!");
        Log.e(TAG, "params is : " + JSONUtils.hashMapToString(params));

        ArrayList<Doc> DocList = new ArrayList<>();
        if (tid == mTidFindAllDocName.get(sid)) {
            //getCardMessage and put it into params
            try {
                ArrayList<HashMap<String, Object>> cardList = (ArrayList<HashMap<String, Object>>)
                        JSONUtils.jsonToSimpleObject((String)params.get(ServiceAttributes.Graph.CARD_LIST), JSONUtils.TYPE_CARD_LIST) ;
                for (HashMap<String, Object> card : cardList) {
                    for (int i = 0; i < mTree1.get(sid).getNodeList().size(); i++) {
                        ParseTree.Node node = mTree1.get(sid).getNodeList().get(i);
                       // if (node.getWord().equals((String) card.get(ServiceAttributes.Graph.Document.TITLE))) {
                            Doc doc = new Doc();
                            doc.setDocName((String) card.get(ServiceAttributes.Graph.Document.TITLE));
                            doc.setCreatedTime((Long) card.get(ServiceAttributes.Graph.Document.CREATED_TIME));
                            //doc.setDocUrl((String)card.get(Graph.Document.URL));
                            DocList.add(doc);
                       // }
                    }
                }
                if (!DocList.isEmpty()) {
                    mTree1.put(sid, addUrlRoot(mTree1.get(sid), ALL_URL_ROOT_ID, mDocTime1.get(sid), tag_time));
                    params.remove(ServiceAttributes.PMS.PARSE_TREE);
                    params.put(ServiceAttributes.PMS.PARSE_TREE, mTree1);
                    mTidFindUrl1.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE,
                            MethodConstants.GRAPH_METHOD_RETRIEVE, params));
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        } else if (tid == mTidFindDocName.get(sid)) {
            try {
                ArrayList<HashMap<String, Object>> cardList =
                        (ArrayList<HashMap<String, Object>>) params.get(ServiceAttributes.Graph.CARD_LIST);
                for (HashMap<String, Object> card : cardList) {
                    Doc doc = new Doc();
                    doc.setDocName((String) card.get(ServiceAttributes.Graph.Document.TITLE));
                    doc.setCreatedTime((Long) card.get(ServiceAttributes.Graph.Document.CREATED_TIME));
                    //doc.setDocUrl((String)card.get(Graph.Document.URL));
                    DocList.add(doc);
                }
                if (!DocList.isEmpty()) {
                    mTree2.put(sid, addUrlRoot(mTree2.get(sid), FILTERED_URL_ROOT_ID, mDocTime2.get(sid), tag_time));
                    params.remove(ServiceAttributes.PMS.PARSE_TREE);
                    params.put(ServiceAttributes.PMS.PARSE_TREE, mTree2);
                    mTidFindUrl2.put(sid, createTask(sid, MethodConstants.GRAPH_TYPE,
                            MethodConstants.GRAPH_METHOD_RETRIEVE, params));
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        }

        if ((tid == mTidFindUrl1.get(sid))||(tid == mTidFindUrl2.get(sid))){
            try{
                ArrayList<HashMap<String, Object>> cardList =
                        (ArrayList<HashMap<String, Object>>) params.get(ServiceAttributes.Graph.CARD_LIST);
                for (HashMap<String, Object> card : cardList) {
                    for (Doc doc : DocList){
                        if (doc.getCreatedTime().equals(card.get(ServiceAttributes.Graph.Document.CREATED_TIME))){
                            doc.setDocUrl((String)card.get(ServiceAttributes.Graph.Document.TITLE));           //Todo:change to URL
                        }
                    }
                }
                if (!DocList.isEmpty()) {
                    //params.put(BUBBLE_FIRST_LINE, "Show GoogleDocs name");
                    mTidBubble.put(sid, createTask(sid, MethodConstants.UI_TYPE,
                            MethodConstants.UI_METHOD_SHOW_BUBBLE, params));
                }
            } catch (Exception e) {
                e.printStackTrace();
                endSession(sid);
            }
        }

        if (tid == mTidBubble.get(sid)) {
            if ((Integer)params.get(ServiceAttributes.UI.STATUS) == 1) {
                try {
                    params.put("HTML Details", getHtml(DocList));
                    mTidDetails.put(sid, createTask(sid, MethodConstants.UI_TYPE,
                    MethodConstants.UI_METHOD_LOAD_WEBVIEW, params));
                } catch (Exception e) {
                    e.printStackTrace();
                    endSession(sid);
                }
            } else {
                endSession(sid);
            }
        } else if (tid == mTidDetails.get(sid)){
            //get selected URL
            for (Doc doc:DocList){
            String status = (String) params.get(doc.getDocName());
                if (status.equals("on")){
                    mSelectedDocUrl.get(sid).append(doc.getDocUrl());
                }
            }
            params.put("Action SetText", mSelectedDocUrl.toString());                      //send URL
            mTidDocSend.put(sid, createTask(sid, MethodConstants.ACTION_TYPE,
            ServiceAttributes.Action.SET_TEXT_EXTRA_MESSAGE, params));
        } else if (tid == mTidDocSend.get(sid)) {
            Log.e(TAG, "Ending session (triggerListShow)");
            endSession(sid);
            Log.e(TAG, "Session ended");
        }

    }

    @Override
    protected void endSession(long sid) {
        mTidFindAllDocName.remove(sid); mTidFindDocName.remove(sid); mTidFindUrl1.remove(sid);
        mTidFindUrl2.remove(sid); mTidBubble.remove(sid); mTidDetails.remove(sid); mTidDocSend.remove(sid);
        mTree1.remove(sid); mTree2.remove(sid); mTreeForSearch1.remove(sid); mTreeForSearch2.remove(sid);
        mDocTime1.remove(sid); mDocTime2.remove(sid); mSelectedDocUrl.remove(sid);
        super.endSession(sid);
    }

}


