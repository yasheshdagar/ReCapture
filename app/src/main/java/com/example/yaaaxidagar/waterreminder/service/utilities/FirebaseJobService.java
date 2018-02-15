package com.example.yaaaxidagar.waterreminder.service.utilities;

import android.os.AsyncTask;

import com.example.yaaaxidagar.waterreminder.ManageReminders;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Yaaaxi Dagar on 1/15/2018.
 */

public class FirebaseJobService extends JobService {

    private AsyncTask asyncTask;
    Notifications notifications;

    public FirebaseJobService(){
        notifications=new Notifications();
    }

    @Override
    public boolean onStartJob(final JobParameters job) {

        asyncTask= new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                notifications.pushNotification(FirebaseJobService.this,job.getTag());
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job,false);
            }
        };

        asyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(asyncTask!=null)asyncTask.cancel(true);
        return true;
    }
}
