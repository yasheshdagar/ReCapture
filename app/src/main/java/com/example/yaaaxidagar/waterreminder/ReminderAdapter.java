package com.example.yaaaxidagar.waterreminder;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yaaaxidagar.waterreminder.data.classes.ReminderContract.ReminderEntry;

/**
 * Created by Yaaaxi Dagar on 1/20/2018.
 */

public class ReminderAdapter extends CursorAdapter {

    public ReminderAdapter(Context context,Cursor cursor){
        super(context,cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.reminders_list,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView title=view.findViewById(R.id.reminder_name);
        TextView repetetion=view.findViewById(R.id.frequency);

        int titleIndex=cursor.getColumnIndex(ReminderEntry.COLUMN_TITLE);
        int repetetionIndex=cursor.getColumnIndex(ReminderEntry.COLUMN_REPETETION);

        String TITLE=cursor.getString(titleIndex);
        String REPETETION=cursor.getString(repetetionIndex);

        title.setText(TITLE);
        repetetion.setText(REPETETION);
    }
}
