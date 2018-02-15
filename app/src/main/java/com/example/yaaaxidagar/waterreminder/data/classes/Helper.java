package com.example.yaaaxidagar.waterreminder.data.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yaaaxidagar.waterreminder.data.classes.ReminderContract.ReminderEntry;

/**
 * Created by Yaaaxi Dagar on 1/18/2018.
 */

public class Helper extends SQLiteOpenHelper {

    private static String DATABASE_NAME="Reminder.db";
    private static int DATABASE_VERSION=1;

    public Helper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String REMINDER_TABLE_STATEMENT="CREATE TABLE "+ReminderEntry.TABLE_NAME+" ("
                                +ReminderEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                                +ReminderEntry.COLUMN_TITLE+" TEXT NOT NULL,"
                                +ReminderEntry.COLUMN_HOURS+" INTEGER NOT NULL DEFAULT 0,"
                                +ReminderEntry.COLUMN_MINUTES+" INTEGER NOT NULL DEFAULT 0,"
                                +ReminderEntry.COLUMN_REPETETION+" TEXT NOT NULL)";

        sqLiteDatabase.execSQL(REMINDER_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
