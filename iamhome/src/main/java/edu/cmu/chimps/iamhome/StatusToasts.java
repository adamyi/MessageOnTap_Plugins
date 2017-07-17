package edu.cmu.chimps.iamhome;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wangyusen on 7/16/17.
 */

public class StatusToasts {

    public static void wifiConnectedToast(Context context){
        CharSequence text = "Wifi Connected";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void wifiDisconnectedToast(Context context){
        CharSequence text = "Wifi Disconnected";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void atHomeToast(Context context){
        CharSequence text = "You are at home";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void leaveHomeToast(Context context){
        CharSequence text = "You have left home";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public static void saveHomeToast(Context context){
        CharSequence text = "save home success";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
