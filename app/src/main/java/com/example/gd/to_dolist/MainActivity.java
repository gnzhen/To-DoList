package com.example.gd.to_dolist;

import android.content.ContentValues;
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

public class MainActivity extends AppCompatActivity {

    private static TaskDbHelper mDbHelper;
    ArrayList<Task> tasks;
    Boolean edit = false;
    String desc, date, time, status;

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

        String sortOrder = TaskContract.TaskEntry._ID + " DESC"; //ascending

        Cursor cursor = db.query(
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

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                date = dateFormatter.format(Long.parseLong(date));
                time = timeFormatter.format(Long.parseLong(time));

                Task task = new Task(id, desc, date, time, status);

                if(status != "Done"){
                    if(task.checkOverdue())
                        task.setStatus("Overdue");
                    else
                        task.setStatus("");

                    Log.d("after check overdue=", task.getStatus());
                }

                tasks.add(task);

            } while (cursor.moveToNext());
        }

        final TaskAdapter adapter = new TaskAdapter(this, 0, tasks);
        final ListView listView = (ListView) findViewById(R.id.list_view);
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
                    long itemId = listView.getItemIdAtPosition(pos);

                    Task task = adapter.getItem(pos);
                    edit = true;

                    Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("TASK", task);
                    args.putSerializable("EDIT", edit);
                    intent.putExtras(args);
                    startActivity(intent);

                    Log.d("the item id is", Long.toString(itemId));
                    Log.d("the task id is", Long.toString(task.getId()));
                    Log.d("the task desc is", task.getDesc());
                    Log.d("the task status is", status);
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
            case R.id.action_add_task: {

            }
            case R.id.action_settings: {
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

