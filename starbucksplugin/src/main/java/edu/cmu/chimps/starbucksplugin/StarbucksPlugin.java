package edu.cmu.chimps.starbucksplugin;

/**
 * Created by apple on 2017/8/10.
 */

import android.content.Intent;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.chimps.messageontap_api.JSONUtils;
import edu.cmu.chimps.messageontap_api.MessageOnTapPlugin;
import edu.cmu.chimps.messageontap_api.MethodConstants;
import edu.cmu.chimps.messageontap_api.PluginData;
import edu.cmu.chimps.messageontap_api.ServiceAttributes;
import edu.cmu.chimps.messageontap_api.Tag;
import edu.cmu.chimps.messageontap_api.Trigger;

/**
 * This is a sample plugin for MessageOnTap to facilitate developing plugins for MessageOnTap.
 */

public class StarbucksPlugin extends MessageOnTapPlugin{

    public static final String TAG = "StarbucksPlugin";
    Long mTidShowBubble;
    Tag tag_Coffee = new Tag("TAG_COFFEE", new HashSet<>(Collections.singletonList("(coffee|Coffee|StarbucksSettingActivity|starbucks)")));
    //Tag tag_verb = new Tag("TAG_COFFEE",new HashSet<>(Collections.singletonList("(order|Order)")));
    String result;
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
        //Set<String> mKeyList = new HashSet<>();
        //mKeyList.add(EntityAttributes.Graph.Person.NAME);
        //mKeyList.add(EntityAttributes.Graph.Person.NUMBER);
        //mKeyList.add(Globals.KEY_QUERY_SUBJECT);

        Set<Tag> tagList = new HashSet<>();
        Set<Trigger> triggerList = new HashSet<>();
        tagList.add(tag_Coffee);
       // tagList.add(tag_verb);
        Set<String> mMandatory = new HashSet<>();

        // Category one: show calendar
        // trigger1: are you free tomorrow? incoming
        mMandatory.add("TAG_COFFEE");
        mMandatory.add("TAG_VERB");
        Trigger trigger1 = new Trigger("calendar_trigger_one", mMandatory);

        triggerList.add(trigger1);

        return new PluginData().triggerSet(JSONUtils.simpleObjectToJson(triggerList, JSONUtils.TYPE_TRIGGER_SET))
                .tagSet(JSONUtils.simpleObjectToJson(tagList, JSONUtils.TYPE_TAG_SET));

    }

    @Override
    protected void initNewSession(long sid, HashMap<String, Object> params) throws Exception {
        Log.e(TAG, "Session created here!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        //Log.e(TAG, "parse tree: " + ((ParseTree) JSONUtils.jsonToSimpleObject((String) params.get("tree"), JSONUtils.TYPE_PARSE_TREE)).toString());
        HashMap<String, Object> reqParams = new HashMap<>();
        //reqParams.put("key1", "value1");
        //reqParams.put("key2", "value2");
        //reqParams.put("key3", "value3");
        params.put(ServiceAttributes.UI.BUBBLE_FIRST_LINE, "Starbucks Plugin");
        params.put(ServiceAttributes.UI.BUBBLE_SECOND_LINE,"Order Coffee?");
        params.put(ServiceAttributes.UI.ICON_TYPE_STRING,R.string.fa_calendar);

        // TID is something we might need to implement stateflow inside a plugin.
        mTidShowBubble = createTask(sid, MethodConstants.UI_TYPE, MethodConstants.UI_METHOD_SHOW_BUBBLE, params);
    }

    @Override
    protected void newTaskResponded(long sid, long tid, HashMap<String, Object> params) throws Exception {
        //request script
        //Perform action script
        Log.e(TAG, "Got task response!");
        Log.e(TAG, JSONUtils.hashMapToString(params));
        if (tid == mTidShowBubble) {
            Log.e(TAG, "TID is right " );
            if (params.get("status").equals("clicked")) {
                Log.e(TAG, "button clicked");
                //HashMap<String, Object> newParams = new HashMap<>();
                //newParams.put("perform script", "script");
                //createTask(sid, MethodConstants.ACTION_TYPE,"perform script", newParams);
                Intent sugiliteIntent = new Intent("edu.cmu.hcii.sugilite.COMMUNICATION");
                sugiliteIntent.addCategory("android.intent.category.DEFAULT");
                sugiliteIntent.putExtra("messageType", "RUN_SCRIPT");
                String scriptName = ScriptStorage.getScript(StarbucksPlugin.this);
                if (scriptName != "empty") {
                    sugiliteIntent.putExtra("arg1", scriptName);
                    sugiliteIntent.putExtra("arg2", "Run Script Complete");
                    startActivity(sugiliteIntent);}
                endSession(sid);
                Log.e(TAG, "Ending session " + sid);
                Log.e(TAG, "Action officially run" + sid);
                /*
                String scriptName = ScriptStorage.getScript(StarbucksPlugin.this);
                StarbucksIntent.SCRIPT_NAME = scriptName;
                Intent intent = new Intent(StarbucksPlugin.this,StarbucksIntent.class);
                startActivity(intent);
                */
            }
        }else{
            Log.e(TAG, "Ending session " + sid);
            endSession(sid);
            Log.e(TAG, "Session ended");
        }
    }


}
