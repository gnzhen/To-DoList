package com.example.gd.to_dolist;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReminderService extends IntentService {

    public static final String TASK = "task";

    public ReminderService() {super("ReminderService");}

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle bundle1 = intent.getExtras();
        Task task = (Task) bundle1.getSerializable(TASK);
        //String overdue = (String)bundle1.getSerializable("overdue");

        int id = Integer.parseInt(Long.toString(task.getId()));

        Intent reminderIntent = new Intent(this, ReminderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TASK, task);
        //bundle.putSerializable("overdue", overdue);
        reminderIntent.putExtras(bundle);

        PendingIntent reminderPendingIntent =
                PendingIntent.getActivity(this, id,reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification addAction
        //NotificationCompat.Action actionMarkDone = new NotificationCompat.Action.Builder(R.drawable.ic_done_white_24dp, "Mark done", pendingIntent).build();
        //NotificationCompat.Action actionSnooze = new NotificationCompat.Action.Builder(R.drawable.ic_alarm_off_white_24dp, "Snooze", pendingIntent).build();

        NotificationCompat.Builder nBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                        .setContentTitle(task.getDesc())
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentText("Overdue: "  +"min");
        //.setContentIntent(reminderPendingIntent);
        //.addAction(actionMarkDone)
        //.addAction(actionSnooze);

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.notify(id, nBuilder.build());
    }
}
