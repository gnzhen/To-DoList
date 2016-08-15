package com.example.gd.to_dolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Task> tasks;
    private static TaskDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onResume(){
        super.onResume();

        mDbHelper = new TaskDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_DESC,
                TaskContract.TaskEntry.COLUMN_NAME_DATE,
                TaskContract.TaskEntry.COLUMN_NAME_TIME
        };

        String sortOrder = TaskContract.TaskEntry.COLUMN_NAME_DESC + " DESC"; //ascending

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_DESC));
                String date = cursor.getString(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow
                        (TaskContract.TaskEntry.COLUMN_NAME_TIME));

                Task task = new Task(desc, date, time);
                tasks = new ArrayList<Task>();
                tasks.add(task);

            } while(cursor.moveToNext());

            for(Task ttask: tasks){
                //long id = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));
                //Log.d("How many task", Long.toString(id));
                Log.d("the desc", ttask.getDesc());
                Log.d("the date", ttask.getDate());
                Log.d("the time", ttask.getTime());
            }
        }

        TaskAdapter adapter = new TaskAdapter(this, 0, tasks);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showInsertActivity();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:{
                showInsertActivity();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showInsertActivity(){
        Intent intent = new Intent(this, InsertActivity.class);
        startActivity(intent);
    }
}
