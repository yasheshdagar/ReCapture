package com.example.yaaaxidagar.waterreminder.service.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.yaaaxidagar.waterreminder.MainActivity;
import com.example.yaaaxidagar.waterreminder.R;

/**
 * Created by Yaaaxi Dagar on 1/15/2018.
 */

public class Notifications{

    NotificationCompat.Builder notificationBuilder;

    NotificationManager notificationManager;


    public void pushNotification(Context context,String title){

        notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent=new Intent(context,MainActivity.class);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.appicon)
                .setContentTitle(title)
                .setContentText("Hey, I am here to remind you...")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        notificationManager.notify(1,notificationBuilder.build());
    }

}
