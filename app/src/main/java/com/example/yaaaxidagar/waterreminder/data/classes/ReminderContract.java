package com.example.yaaaxidagar.waterreminder.data.classes;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yaaaxi Dagar on 1/18/2018.
 */

public class ReminderContract {

    public static final String CONTENT_AUTHORITY="com.example.yaaaxidagar.waterreminder";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_REMINDER="reminders";


    public static final class ReminderEntry implements BaseColumns{

        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_REMINDER);

        public static final String TABLE_NAME="reminders";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_HOURS="hours";
        public static final String COLUMN_MINUTES="minutes";
        public static final String COLUMN_REPETETION="repetetion";

    }


}
