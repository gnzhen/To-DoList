package com.example.gd.to_dolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
        if (text_desc != null) {
            text_desc.setText(task.getDesc());
        }
        if (text_dateTime != null) {
            text_dateTime.setText("Overdue for " +  overdue + "minutes");
        }

        CheckBox cbx_markDone = (CheckBox)findViewById(R.id.cbx_markDone);
        CheckBox cbx_offNoti = (CheckBox)findViewById(R.id.cbx_offNoti);

        if(task.getStatus().equals("Done"))
            if (cbx_markDone != null) {
                cbx_markDone.setChecked(true);
            }
        if(task.getReminder() == 0)
            if (cbx_offNoti != null) {
                cbx_offNoti.setChecked(true);
            }

        Log.d("reminder", Integer.toString(task.getReminder()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_reminder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
                finish();

                CheckBox cbx_offNoti= (CheckBox) findViewById(R.id.cbx_offNoti);
                CheckBox cbx_markDone = (CheckBox) findViewById(R.id.cbx_markDone);

                int id = Integer.parseInt(Long.toString(task.getId()));

                if (cbx_offNoti != null && cbx_offNoti.isChecked()) {
                    task.setReminder(0);
                }
                if (cbx_markDone != null && cbx_markDone.isChecked()) {
                    task.setStatus("Done");
                    task.setReminder(0);
                }
                MainActivity mainActivity = new MainActivity();
                mainActivity.updateDatabase(task, ReminderActivity.this);
            }
        });

    }

}
