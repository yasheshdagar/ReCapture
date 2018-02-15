package com.example.yaaaxidagar.waterreminder.data.classes;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.yaaaxidagar.waterreminder.data.classes.ReminderContract.ReminderEntry;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Yaaaxi Dagar on 1/20/2018.
 */

public class ReminderProvider extends ContentProvider {

    private Helper mHelper;

    private static final int REMINDER=100;
    private static final int REMINDER_ID=101;

    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static{
        mUriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,ReminderContract.PATH_REMINDER,REMINDER);
        mUriMatcher.addURI(ReminderContract.CONTENT_AUTHORITY,ReminderContract.PATH_REMINDER+"/#",REMINDER_ID);
    }


    @Override
    public boolean onCreate() {
        mHelper=new Helper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteDatabase db=mHelper.getReadableDatabase();

        Cursor cursor;

        int match=mUriMatcher.match(uri);

        switch (match){

            case REMINDER:
                cursor=db.query(ReminderEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;

            case REMINDER_ID:
                s=ReminderEntry._ID + "=?";
                strings1=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=db.query(ReminderEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;

            default:
                throw new IllegalArgumentException("Can't query uri:"+uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int match=mUriMatcher.match(uri);

        switch (match){

            case REMINDER:
                return insertReminder(uri,contentValues);

            default:
                throw new IllegalArgumentException("Insertion Not Supported for: "+uri);
        }
    }

    private Uri insertReminder(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db=mHelper.getWritableDatabase();

         long id=db.insert(ReminderEntry.TABLE_NAME,null,contentValues);

        if(id==-1){
            Log.e(ReminderProvider.class.getSimpleName(),"Error with saving pet");
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int rowsDeleted;

        SQLiteDatabase db=mHelper.getWritableDatabase();

        int match=mUriMatcher.match(uri);

        switch (match){
            case REMINDER:
                rowsDeleted=db.delete(ReminderEntry.TABLE_NAME,s,strings);
                break;

            case REMINDER_ID:
                s=ReminderEntry._ID+"=?";
                strings=new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=db.delete(ReminderEntry.TABLE_NAME,s,strings);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        String s1;
        String[] strings1;
        SQLiteDatabase db=mHelper.getWritableDatabase();

        int match=mUriMatcher.match(uri);

        int rowsAffected=0;

        switch (match){

            case REMINDER_ID:
                s1 =ReminderEntry._ID + "=?";
                strings1=new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsAffected=db.update(ReminderEntry.TABLE_NAME,contentValues,s1,strings1);
                break;

            default:
                throw new IllegalArgumentException("Can't update reminder for: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return rowsAffected;
    }
}
