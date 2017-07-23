package edu.cmu.chimps.iamhome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import edu.cmu.chimps.iamhome.SharedPrefs.StringStorage;

public class IAmHomeSettingsActivity extends AppCompatActivity {

    private String sentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (StringStorage.getMessage(getBaseContext()) == null) {
            StringStorage.storeMessage(getBaseContext(), "");
        } else { sentText = StringStorage.getMessage(getBaseContext()); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_contact:
                Intent setContactIntent = new Intent(this, SelectContactActivity.class);
                startActivity(setContactIntent);
                //Toast.makeText(this, "set contact", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rest_wifi:
                //Toast.makeText(this, "reset wifi", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Reset Home Wifi");
                dialog.setMessage("Saved wifi will be replaced by the connected wifi");
                dialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Reset Wifi code here

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent closeNotificationDrawer = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(closeNotificationDrawer);
                    }
                });
                dialog.show();
                break;
            case R.id.ser_default_message:
                //Toast.makeText(this, "set message", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set message to send");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint(StringStorage.getMessage(getBaseContext()));
                input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        input.setHint(null);
                    }
                });
                builder.setView(input);
                builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sentText = input.getText().toString();
                        StringStorage.storeMessage(getBaseContext(), sentText);
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            default:
        }
        return true;
    }
}

