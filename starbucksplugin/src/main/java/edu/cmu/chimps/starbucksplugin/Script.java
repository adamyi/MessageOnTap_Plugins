package edu.cmu.chimps.starbucksplugin;

import android.graphics.Color;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

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


    public  static void SetAllFlag(Boolean flag){
        for (int i = 0; i < scriptList.size(); i++) {
            scriptList.get(i).setFlag(flag);
        }
    }


}
