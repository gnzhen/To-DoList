package com.example.gd.to_dolist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text. DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static TaskDbHelper mDbHelper;
    ArrayList<Task> tasks;
    Boolean edit = false;
    String desc, date, time, status, displayDate, displayTime;
    int reminder;
    ListView listView;
    Cursor cursor;
    String sortOrder;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("edit", edit);
                    intent.putExtras(args);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        readFromDb();
        setListView();
        scheduleReminder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_add_task: {

            }*/
            case R.id.action_settings: {
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showFunctionDialog(Context context, Task t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final Task task = t;
        int stringArray;

        if(task.getStatus().equals("Done"))
            stringArray = R.array.function_array1;
        else if(task.getStatus().equals("Overdue"))
            stringArray = R.array.function_array3;
        else
            stringArray = R.array.function_array2;

        builder.setItems(stringArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:{
                        if(task.getStatus().equals("Done")){
                            if(task.checkOverdue())
                                task.setStatus("Overdue");
                            else
                                task.setStatus("");
                        }
                        else
                            task.setStatus("Done");

                        updateDatabase(task);
                        readFromDb();
                        setListView();
                        scheduleReminder();
                        break;
                    }

                    case 1:{
                        editTask(task);
                        break;
                    }

                    case 2:{
                        tasks.remove(task);

                        mDbHelper = new TaskDbHelper(getApplicationContext());
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        //getApplicationContext().deleteDatabase("task.db");
                        db.delete(TaskContract.TaskEntry.TABLE_NAME, null, null);

                        ContentValues values = new ContentValues();

                        Collections.reverse(tasks);

                        for(Task t: tasks){
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_DESC, t.getDesc());
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, t.getDate());
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_TIME, t.getTime());
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_STATUS, t.getStatus());
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_REMINDER, t.getReminder());


                            long id = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                        }

                        adapter.notifyDataSetChanged();

                        //db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);

                        Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_LONG).show();

                        readFromDb();
                        setListView();
                        scheduleReminder();

                        break;
                    }

                    case 3:{
                        dialog.dismiss();
                        break;
                    }

                    case 4:{
                        task.setReminder(0);
                        scheduleReminder();
                        break;
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editTask(Task task) {
        edit = true;

        Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        args.putSerializable("edit", edit);
        intent.putExtras(args);
        startActivity(intent);
    }

    public void readFromDb(){
        mDbHelper = new TaskDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        tasks = new ArrayList<>();
        edit = false;

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_DESC,
                TaskContract.TaskEntry.COLUMN_NAME_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_TIME,
                TaskContract.TaskEntry.COLUMN_NAME_STATUS,
                TaskContract.TaskEntry.COLUMN_NAME_REMINDER
        };

        sortOrder = TaskContract.TaskEntry._ID + " DESC"; //ascending

        cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry._ID));
                desc = cursor.getString(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_DESC));
                date = cursor.getString(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_DATE));
                time = cursor.getString(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_TIME));
                status = cursor.getString(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_STATUS));
                reminder = cursor.getInt(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_REMINDER));

                Task task = new Task(id, desc, date, time, status, reminder);

                displayDate = task.convertDate();
                displayTime = task.convertTime();

                if(!status.equals("Done")){
                    if(task.checkOverdue())
                        task.setStatus("Overdue");
                    else
                        task.setStatus("");
                }

                tasks.add(task);

            } while (cursor.moveToNext());
        }
        db.close();
    }

    public void setListView() {
        adapter = new TaskAdapter(this, 0, tasks);

        listView = (ListView) findViewById(R.id.list_view);
        if (adapter.getCount() != 0) {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(adapter);
                    listView.smoothScrollToPosition(0);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                    Task task = adapter.getItem(pos);

                    editTask(task);
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

                    Task task = adapter.getItem(pos);
                    showFunctionDialog(MainActivity.this, task);

                    return true;
                }

            });
        }
    }

    private void scheduleReminder() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

        for(Task task: tasks){
            if(task.getReminder() == 1){
                SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                String alarmTimeString = task.convertDate() + " " + task.convertTime();

                String alarmTime = "";

                try {
                    Date date = dateTimeFormatter.parse(alarmTimeString);
                    alarmTime = Long.toString(date.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String stringdatetime = dateTimeFormatter.format(Long.parseLong(alarmTime));
                Long now = Calendar.getInstance().getTimeInMillis();
                String stringnow = dateTimeFormatter.format(now);

                Long overdue = now - Long.parseLong(alarmTime);
                Long overdueMinute = (overdue / 1000) / 60;
                Long overdueHour = ((overdue / 1000)/60)/60;
                overdueMinute -= (overdueHour*60);
                String overdueHr = Long.toString(overdueHour);
                String overdueMin = Long.toString(overdueMinute);

                Log.d("stringdatetime", stringdatetime);
                Log.d("stringnow", stringnow);
                Log.d("overdue", Long.toString(overdue));
                Log.d("overdue min", overdueMin);
                Log.d("overdue hour", overdueHr);

                Intent alarmIntent = new Intent("com.example.gd.to_do_list.Task_to_do");
                Bundle bundle = new Bundle();
                bundle.putSerializable("task", task);
                bundle.putSerializable("overdue", overdueMin);
                alarmIntent.putExtras(bundle);
                sendBroadcast(alarmIntent);


                int id = Integer.parseInt(Long.toString(task.getId()));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        MainActivity.this, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                if(!alarmTime.equals("")) {
                    //alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(alarmTime), pendingIntent);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(alarmTime), pendingIntent);
                    //long repeatingTime=15*60*1000;
                    long repeatingTime = 1*60*1000;
                    long oneHour = 60*60*1000;

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP
                            ,Long.parseLong(alarmTime)-oneHour
                            ,repeatingTime, pendingIntent);

                    Log.d("pending id", Integer.toString(id));

                }
                intentArray.add(pendingIntent);
            }
        }

    }

    public void updateDatabase(Task task){
        Toast.makeText(getApplicationContext(), "Marked Done!", Toast.LENGTH_LONG).show();

        mDbHelper = new TaskDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskContract.TaskEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(Long.toString(task.getId())) };

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DESC, task.getDesc());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, task.getDate());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TIME, task.getTime());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_STATUS, task.getStatus());
        values.put(TaskContract.TaskEntry.COLUMN_NAME_REMINDER, task.getReminder());

        int count = db.update(
                TaskContract.TaskEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }
}


