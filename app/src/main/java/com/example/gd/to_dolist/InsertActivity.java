package com.example.gd.to_dolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InsertActivity extends AppCompatActivity {

    public static TaskDbHelper mDbHelper;
    public static String desc, date, time, status, displayDate, displayTime;
    public static Boolean edit;
    public static TextView text_time;
    public static TextView text_date;
    public static EditText edit_desc;

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
    public static Date convertedDate = new Date();
    public static Date convertedTime = new Date();
    public static Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_clear_mtrl_alpha);
        }

        desc = ""; date = ""; time = "";

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        edit = (Boolean)args.getSerializable("EDIT");

        if(edit){
            task = (Task)args.getSerializable("TASK");

            edit_desc = (EditText) findViewById(R.id.edit_desc);
            text_date = (TextView) findViewById(R.id.text_date);
            text_time = (TextView) findViewById(R.id.text_time);

            date = task.getDate();
            time = task.getTime();

            displayDate = dateFormatter.format(Long.parseLong(date));
            displayTime = timeFormatter.format(Long.parseLong(time));

            edit_desc.setText(task.getDesc());
            text_date.setText(displayDate);
            text_time.setText(displayTime);

            status = task.getStatus();
        }

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        if (fabSave != null) {
            fabSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calendar c = Calendar.getInstance();
                    edit_desc = (EditText) findViewById(R.id.edit_desc);
                    desc = edit_desc.getText().toString();

                    if(!edit)
                        status = "";

                    if ((!edit && (TextUtils.isEmpty(desc) || date.equals("") || time.equals("")))
                            || (edit && TextUtils.isEmpty(desc))) {
                        showAlertDialog(InsertActivity.this, R.string.dialog_message1);
                    }
                    else if(!status.equals("Overdue") && Long.parseLong(date) <= c.getTimeInMillis()
                            && Long.parseLong(time) <= c.getTimeInMillis()){

                        showAlertDialog(InsertActivity.this, R.string.dialog_message);
                    }
                    else {

                        mDbHelper = new TaskDbHelper(getApplicationContext());
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        //write to dbs
                        values.put(TaskContract.TaskEntry.COLUMN_NAME_DESC, desc);
                        values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, date);
                        values.put(TaskContract.TaskEntry.COLUMN_NAME_TIME, time);
                        values.put(TaskContract.TaskEntry.COLUMN_NAME_STATUS, status);

                        if(edit){
                            String selection = TaskContract.TaskEntry._ID + " LIKE ?";
                            String[] selectionArgs = { String.valueOf(Long.toString(task.getId())) };

                            int count = db.update(
                                    TaskContract.TaskEntry.TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);
                        }
                        else{
                            //insert new row to dbs
                            long id = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                        }
                        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();

                        finish();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DatePickerFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener(){

                @Override
                public void onDateSet(DatePicker view, int year, int month, int day){

                    text_date = (TextView) getActivity().findViewById(R.id.text_date);

                    /*pastDate = (year < c.get(Calendar.YEAR))
                            && (month < c.get(Calendar.MONTH))
                            && (day < c.get(Calendar.DAY_OF_MONTH));

                    today = (year == c.get(Calendar.YEAR))
                            && (month == c.get(Calendar.MONTH))
                            && (day == c.get(Calendar.DAY_OF_MONTH));

                    if(!timePickerPress)
                        pastTime = Double.parseDouble(task.getTime())
                                <= Double.parseDouble(Long.toString(c.getTimeInMillis()));

                    if (pastDate && pastTime && !time.equals("")){
                        showAlertDialog(getActivity(), R.string.dialog_message);
                        text_date.setText("");
                        date = "";
                    }*/
                    c.set(year, month, day);

                    date = Long.toString(c.getTimeInMillis());

                    text_date.setText(dateFormatter.format(Long.parseLong(date)));
                }
            },year, month, day);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);

            return datePickerDialog;
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            final TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hour, int minute) {

                            text_time = (TextView) getActivity().findViewById(R.id.text_time);

                            /*pastTime = Double.parseDouble(task.getTime())
                                    <= Double.parseDouble(Long.toString(c.getTimeInMillis()));

                            if(!datePickerPress){
                                pastDate = Double.parseDouble(task.getDate())
                                        <= Double.parseDouble(Long.toString(c.getTimeInMillis()));

                                today = (year == c.get(Calendar.YEAR))
                                        && (month == c.get(Calendar.MONTH))
                                        && (day == c.get(Calendar.DAY_OF_MONTH));
                            }

                            if (pastDate || today && pastTime && !date.equals("")) {
                                showAlertDialog(getActivity(), R.string.dialog_message);
                                text_time.setText("");
                                time = "";
                            } else {*/
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, minute);

                                time = Long.toString(c.getTimeInMillis());

                                text_time.setText(timeFormatter.format(Long.parseLong(time)));
                        }
                    }, hour, minute, DateFormat.is24HourFormat(getActivity()));

            timePickerDialog.updateTime(0, 0);

            return timePickerDialog;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static void showAlertDialog(Context context, int message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}





