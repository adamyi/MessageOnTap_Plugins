package edu.cmu.chimps.starbucksplugin;

import android.graphics.Color;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

public class Script {
    private String mName;
    private boolean isFlag;
    public static ArrayList<Script> scriptList =  new ArrayList<>();

    public Script(String Name){
        this.mName = Name;
    }

    public String getName() {
        return mName;
    }

    public boolean isFlag(){
        return isFlag;
    }

    public void setFlag(boolean flag){
        isFlag = flag;
    }

    private char getFirstCharacter(){
        return this.mName.charAt(0);
    }
    public TextDrawable getContactPicture(){
        return TextDrawable.builder()
                .buildRound(String.valueOf(getFirstCharacter()), Color.GRAY);
    }

    public static String getSelectedName() {
      String selectedName = "";
        if (scriptList.size() != 0){
            for (int i = 0; i < scriptList.size(); i++) {
                if (scriptList.get(i).isFlag()) {
                    selectedName = scriptList.get(i).getName();
                }
            }
        }
        return selectedName;
    }


    public  static void SetAllFlag(Boolean flag){
        for (int i = 0; i < scriptList.size(); i++) {
            scriptList.get(i).setFlag(flag);
        }
    }


}
