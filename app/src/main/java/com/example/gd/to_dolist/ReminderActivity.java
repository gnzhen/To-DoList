package com.example.gd.to_dolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderActivity extends AppCompatActivity{
    Task task;
    String overdue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        task = (Task)bundle.getSerializable("task");
        overdue = (String)bundle.getSerializable("overdue");

        TextView text_desc = (TextView)findViewById(R.id.text_desc);
        TextView text_dateTime = (TextView)findViewById(R.id.text_dateTime);
        text_desc.setText(task.getDesc());
        text_dateTime.setText("Overdue for " +  overdue + "minutes");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_reminder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
                finish();

                CheckBox cbx_snooze = (CheckBox) findViewById(R.id.cbx_snooze);
                CheckBox cbx_markDone = (CheckBox) findViewById(R.id.cbx_markDone);

                if (cbx_snooze.isChecked()) {
                    Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("task", task);
                    alarmIntent.putExtras(bundle);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            ReminderActivity.this, Integer.parseInt(Long.toString(task.getId())), alarmIntent,0);

                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
                if(cbx_markDone.isChecked()){
                    task.setStatus("Done");
                }
            }
        });

    }

}
