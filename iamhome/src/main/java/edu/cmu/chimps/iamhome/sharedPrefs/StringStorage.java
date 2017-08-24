package edu.cmu.chimps.iamhome.sharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class StringStorage {

    private static final String APP_DEFAULT_MESSAGE = "Hey! I just arrived home!";
    private static final String KEY_POSITION = "IAmHomeDefaultMessage";

    public static void storeMessage(Context context, String inputText, Boolean mute) {
        SharedPreferences.Editor editor = context.getSharedPreferences("message", Context.MODE_PRIVATE).edit();
        if (inputText.replaceAll(" ", "").equals("")) {
            if(!mute) {
                Toast.makeText(context, "Message has been reset to default", Toast.LENGTH_SHORT).show();
            }
            editor.putString(KEY_POSITION, APP_DEFAULT_MESSAGE);
            editor.apply();
        } else {
            editor.putString(KEY_POSITION, inputText);
            editor.apply();
            if(!mute) {
                Toast.makeText(context, "Successfully save", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getMessage(Context context) {
        SharedPreferences msg = context.getSharedPreferences("message", Context.MODE_PRIVATE);
        return msg.getString(KEY_POSITION, "");
    }
}
