package edu.cmu.chimps.iamhome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class IAmHomeSettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Toast.makeText(this, "set contact", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rest_wifi:
                Toast.makeText(this, "reset wifi", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(this, "set message", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set message to send");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mText = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

