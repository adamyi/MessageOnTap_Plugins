package edu.cmu.chimps.starbucksplugin;

import android.graphics.Color;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

/**
 * Created by knight006 on 7/18/2017.
 */

public class Script {
    private String Name;
    private boolean isFlag;
    public static ArrayList<Script> scriptList =  new ArrayList<>();

    public Script(String Name){
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }

    public boolean isFlag(){
        return isFlag;
    }

    public void setFlag(boolean flag){
        isFlag = flag;
    }

    public char getFirstC(){
        return this.Name.charAt(0);
    }
    public TextDrawable getContactPicture(){
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(getFirstC()), Color.GRAY);
        return drawable;
    }

    public static String getSelectedName(){
        for (int i = 0; i< scriptList.size(); i++){
            if (scriptList.get(i).isFlag()){
                return scriptList.get(i).getName();
            }
        }
        return "empty";
    }

    public static int SelectedItemCount(){
        int count = 0;
        for (int i = 0; i< scriptList.size(); i++){
            if (scriptList.get(i).isFlag()){
                count++;
            }
        }
        return count;
    }

    public  static void SetAllFlag(Boolean flag){
        for (int i = 0; i < scriptList.size(); i++) {
            scriptList.get(i).setFlag(flag);
        }
    }


    public static ArrayList<String> getSavedContactList(){
        ArrayList<String> savedContactList = new ArrayList<>();
        for (int i = 0; i < Script.scriptList.size(); i++){
            if (Script.scriptList.get(i).isFlag()){
                savedContactList.add(Script.scriptList.get(i).getName());
            }
        }
        return savedContactList;
    }


}
