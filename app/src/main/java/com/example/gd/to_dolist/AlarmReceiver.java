package com.example.gd.to_dolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.io.Serializable;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle1 = intent.getExtras();
        String taskDesc = bundle1.getString("task_desc");
        //Task task = (Task)bundle1.getSerializable("task");
        //String overdue = (String)bundle1.getSerializable("overdue");

        Intent reminderService = new Intent(context, ReminderService.class);
        //Bundle bundle = new Bundle();
        //bundle.putSerializable("task", task);
        //bundle.putSerializable("overdue", overdue);
        //reminderService.putExtras(bundle);

        //context.startService(reminderService);

    }
}
