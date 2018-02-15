package com.example.yaaaxidagar.waterreminder;

import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yaaaxidagar.waterreminder.data.classes.ReminderContract.ReminderEntry;
import com.example.yaaaxidagar.waterreminder.service.utilities.FirebaseJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ImageView imageView;
    static FirebaseJobDispatcher dispatcher;
    ReminderAdapter reminderAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.delete_all:
                deleteDialog();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view=findViewById(R.id.empty_view);

        getLoaderManager().initLoader(0,null,this);
        reminderAdapter=new ReminderAdapter(this,null);
        dispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

        ListView listview=(ListView)findViewById(R.id.listview);
        listview.setAdapter(reminderAdapter);
        listview.setEmptyView(view);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent toManageReminders=new Intent(MainActivity.this,ManageReminders.class);
                Uri uri= ContentUris.withAppendedId(ReminderEntry.CONTENT_URI,l);
                toManageReminders.setData(uri);
                startActivity(toManageReminders);
            }
        });

        imageView=(ImageView)findViewById(R.id.add_notification);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ManageReminders.class);
                startActivity(intent);
            }
        });
    }


    private void deleteAllPets() {

        int rowsDeleted=getContentResolver().delete(ReminderEntry.CONTENT_URI,null,null);

        if(rowsDeleted==0){
            Toast.makeText(this,"No reminders to delete...",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Reminders deleted successfully...",Toast.LENGTH_LONG).show();
        }

        dispatcher.cancelAll();

    }

    private void deleteDialog() {

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Delete all reminders?")
                .setMessage("You will not receive any notifications...")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAllPets();
                    }
                })
                .show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={ReminderEntry._ID, ReminderEntry.COLUMN_TITLE,ReminderEntry.COLUMN_REPETETION};

        return new CursorLoader(this,ReminderEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        reminderAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reminderAdapter.swapCursor(null);
    }

    static void scheduleJob(String tag,int hours,int minutes,boolean recurring){

        int hoursToSec=hours*60*60;
        int minToSec=minutes*60;

        int totalSec=hoursToSec+minToSec;

        Job job=dispatcher.newJobBuilder()
                .setService(FirebaseJobService.class)
                .setLifetime(Lifetime.FOREVER)
                .setTag(tag)
                .setRecurring(recurring)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(totalSec-20,totalSec))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();

        dispatcher.schedule(job);
    }
}
