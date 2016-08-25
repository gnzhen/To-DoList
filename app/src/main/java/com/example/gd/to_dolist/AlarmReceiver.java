package com.example.gd.to_dolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Task task = (Task)intent.getExtras().getSerializable("task");
        String overdue = (String)intent.getExtras().getSerializable("overdue");

        Intent reminderService = new Intent(context, ReminderService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);
        bundle.putSerializable("overdue", overdue);
        reminderService.putExtras(bundle);

        context.startService(reminderService);

    }
}
