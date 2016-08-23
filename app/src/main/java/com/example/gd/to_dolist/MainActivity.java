package com.example.gd.to_dolist;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static TaskDbHelper mDbHelper;
    ArrayList<Task> tasks;
    Boolean edit = false;
    String desc, date, time, status, displayDate, displayTime;
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
                    args.putSerializable("EDIT", edit);
                    intent.putExtras(args);
                    startActivity(intent);

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mDbHelper = new TaskDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        tasks = new ArrayList<>();
        edit = false;

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_DESC,
                TaskContract.TaskEntry.COLUMN_NAME_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_TIME,
                TaskContract.TaskEntry.COLUMN_NAME_STATUS
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

                Task task = new Task(id, desc, date, time, status);

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                displayDate = dateFormatter.format(Long.parseLong(date));
                displayTime = timeFormatter.format(Long.parseLong(time));

                if(!status.equals("Done")){
                    if(task.checkOverdue())
                        task.setStatus("Overdue");
                    else
                        task.setStatus("");
                }

                tasks.add(task);

            } while (cursor.moveToNext());
        }

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

                    /*Log.d("the item id is", Long.toString(id));
                    Log.d("the task id is", Long.toString(task.getId()));
                    Log.d("the task desc is", task.getDesc());
                    Log.d("the task status is", status);*/

                    return true;
                }
            });


        }
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

                        int count = db.update(
                                TaskContract.TaskEntry.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);

                        onResume();
                        break;
                    }

                    case 1:{
                        editTask(task);
                        break;
                    }

                    case 2:{
                        mDbHelper = new TaskDbHelper(getApplicationContext());
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        tasks.remove(task);

                        //getApplicationContext().deleteDatabase("task.db");
                        db.delete(TaskContract.TaskEntry.TABLE_NAME, null, null);

                        ContentValues values = new ContentValues();

                        Collections.reverse(tasks);

                        for(Task t: tasks){
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_DESC, t.getDesc());
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, t.getDate());
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_TIME, t.getTime());
                            values.put(TaskContract.TaskEntry.COLUMN_NAME_STATUS, t.getStatus());

                            long id = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                        }

                        adapter.notifyDataSetChanged();
                        onResume();

                        //db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);

                        Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_LONG).show();

                        break;
                    }

                    case 3:
                        dialog.dismiss();
                        onResume();
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
        args.putSerializable("TASK", task);
        args.putSerializable("EDIT", edit);
        intent.putExtras(args);
        startActivity(intent);
    }
}


