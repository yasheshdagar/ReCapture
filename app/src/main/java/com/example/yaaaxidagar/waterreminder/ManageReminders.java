package com.example.yaaaxidagar.waterreminder;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.example.yaaaxidagar.waterreminder.data.classes.ReminderContract.ReminderEntry;

public class ManageReminders extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    CheckBox hoursCheckBox,minutesCheckBox;
    EditText editTextHours,editTextMinutes,title;
    Button reminderButton;
    RadioButton once,multipleTimes;
    Uri currentReminderUri=null;
    Uri newUri;
    String titleval;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_reminder_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(currentReminderUri==null) {
            MenuItem menuItem1 = menu.findItem(R.id.turn_off_reminder);
            MenuItem menuItem2 = menu.findItem(R.id.delete_reminder);
            menuItem1.setVisible(false);
            menuItem2.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reminders);

        Intent intent=getIntent();
        currentReminderUri=intent.getData();

        reminderButton=(Button)findViewById(R.id.set_reminder);

        if(currentReminderUri==null){
            reminderButton.setText("SET REMINDER");
            invalidateOptionsMenu();
        }
        else {
            reminderButton.setText("RE-SCHEDULE");
            getLoaderManager().initLoader(1,null,this);
        }

        hoursCheckBox=(CheckBox)findViewById(R.id.hours_check_box);
        minutesCheckBox=(CheckBox)findViewById(R.id.minutes_check_box);

        editTextHours=(EditText)findViewById(R.id.hours_edit_text);
        editTextHours.setEnabled(false);
        editTextMinutes=(EditText)findViewById(R.id.min_edit_text);
        editTextMinutes.setEnabled(false);
        title=(EditText)findViewById(R.id.edit_title);

        once=(RadioButton)findViewById(R.id.repeat_once);
        multipleTimes=(RadioButton)findViewById(R.id.repeat_multiple_times);


        hoursCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hoursCheckBox.isChecked()){
                    editTextHours.setEnabled(true);
                }
                else if(!hoursCheckBox.isChecked()){
                    editTextHours.setEnabled(false);
                }
            }
        });

        minutesCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(minutesCheckBox.isChecked()){
                    editTextMinutes.setEnabled(true);
                }
                else if(!minutesCheckBox.isChecked()){
                    editTextMinutes.setEnabled(false);
                }
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int returned=insertReminder();
                if(returned==0){
                    finish();
                }
            }
        });
    }

    private int insertReminder() {

        int HOURS,MINUTES;
        String REPETETION;
        String TITLE;

        //title testing
        if(title.getText().toString().equals("")){
            Toast.makeText(this,"Specify the title...",Toast.LENGTH_LONG).show();
            return -1;
        }else{
            TITLE=title.getText().toString().trim();
        }

        //timings testing
        if(hoursCheckBox.isChecked() && !(editTextHours.getText().toString().equals(""))){
            HOURS=Integer.parseInt(editTextHours.getText().toString().trim());
        }else {
            HOURS=0;
        }

        if (minutesCheckBox.isChecked() && !(editTextMinutes.getText().toString().equals(""))){
            MINUTES=Integer.parseInt(editTextMinutes.getText().toString().trim());
        }else {
            MINUTES=0;
        }

        if (HOURS==0 && MINUTES==0){
            Toast.makeText(ManageReminders.this,"Can't set reminder for these timings!",Toast.LENGTH_LONG).show();
            return -1;
        }

        //repetetion testing
        if(once.isChecked()){
            REPETETION="Once";
        }else if(multipleTimes.isChecked()){
            REPETETION="MultipleTimes";
        }else{
            Toast.makeText(ManageReminders.this,"Select a repetetion frequency...",Toast.LENGTH_LONG).show();
            return -1;
        }

        ContentValues values=new ContentValues();
        values.put(ReminderEntry.COLUMN_TITLE,TITLE);
        values.put(ReminderEntry.COLUMN_HOURS,HOURS);
        values.put(ReminderEntry.COLUMN_MINUTES,MINUTES);
        values.put(ReminderEntry.COLUMN_REPETETION,REPETETION);

        if(currentReminderUri==null) {
             newUri=getContentResolver().insert(ReminderEntry.CONTENT_URI, values);

            if(newUri==null){
                Toast.makeText(ManageReminders.this,"Reminder not set...",Toast.LENGTH_LONG).show();
            }else {
                if(REPETETION.equals("Once")){
                    MainActivity.scheduleJob(TITLE,HOURS,MINUTES,false);

                }else{
                    MainActivity.scheduleJob(TITLE,HOURS,MINUTES,true);
                }
                Toast.makeText(ManageReminders.this,"Reminder set successfully...",Toast.LENGTH_LONG).show();
            }
        }

        else {
            int rowsUpdated=getContentResolver().update(currentReminderUri,values,null,null);

            if(rowsUpdated==0){
                Toast.makeText(ManageReminders.this,"Reminder not re-scheduled...",Toast.LENGTH_LONG).show();
            }else {
                if(REPETETION.equals("Once")){
                    MainActivity.scheduleJob(TITLE,HOURS,MINUTES,false);

                }else{
                    MainActivity.scheduleJob(TITLE,HOURS,MINUTES,true);
                }
                Toast.makeText(ManageReminders.this,titleval+" has been re-scheduled...",Toast.LENGTH_LONG).show();
            }
        }

        return 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.delete_reminder:
                deleteDialog();
                break;

            case R.id.turn_off_reminder:
                turnOffReminder();
                break;
        }

        return true;
    }

    private void deleteReminder() {

        int rowsDeleted=getContentResolver().delete(currentReminderUri,null,null);

        if(rowsDeleted==0){
            Toast.makeText(this,"Reminder was not deleted...",Toast.LENGTH_LONG).show();
        }else{
            MainActivity.dispatcher.cancel(titleval);
            Toast.makeText(this,"Reminder deleted successfully...",Toast.LENGTH_LONG).show();
        }

    }

    private void deleteDialog() {

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Delete reminder?")
                .setMessage("You will not receive notification...")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteReminder();
                        finish();
                    }
                })
                .show();
    }

    private void turnOffReminder() {

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Turn off "+titleval+" ?")
                .setMessage("You will not receive notification after this...")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Turn Off", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.dispatcher.cancel(titleval);
                        Toast.makeText(ManageReminders.this,titleval+ " has been turned off...",Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection={ReminderEntry._ID,
                ReminderEntry.COLUMN_TITLE,
                ReminderEntry.COLUMN_HOURS,
                ReminderEntry.COLUMN_MINUTES,
                ReminderEntry.COLUMN_REPETETION};

        return new CursorLoader(this,currentReminderUri,projection,null,null,null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToNext()){

            titleval=cursor.getString(cursor.getColumnIndex(ReminderEntry.COLUMN_TITLE));
            int hoursVal=cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_HOURS));
            int minutesVal=cursor.getInt(cursor.getColumnIndex(ReminderEntry.COLUMN_MINUTES));
            String repetetionVal=cursor.getString(cursor.getColumnIndex(ReminderEntry.COLUMN_REPETETION));

            title.setText(titleval);
            title.setEnabled(false);
            editTextHours.setText(Integer.toString(hoursVal));
            editTextHours.setEnabled(true);
            editTextMinutes.setText(Integer.toString(minutesVal));
            editTextMinutes.setEnabled(true);

            if(repetetionVal.equals("Once")){
                once.setChecked(true);
            }else {
                multipleTimes.setChecked(true);
            }

            hoursCheckBox.setChecked(true);
            minutesCheckBox.setChecked(true);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        title.setText("");
        editTextHours.setText("");
        editTextHours.setEnabled(false);
        editTextMinutes.setText("");
        editTextMinutes.setEnabled(false);
        hoursCheckBox.setChecked(false);
        minutesCheckBox.setChecked(false);
        once.setChecked(false);
        multipleTimes.setChecked(false);

    }

}
