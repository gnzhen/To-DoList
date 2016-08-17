package com.example.gd.to_dolist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GD on 8/14/2016.
 */
public class TaskAdapter extends ArrayAdapter {

    private Activity activity;
    private ArrayList<Task> tasks;
    private static LayoutInflater inflater = null;

    public TaskAdapter (Activity activity, int textViewResourceId, ArrayList<Task> tasks) {
        super(activity, textViewResourceId, tasks);
        try {
            this.activity = activity;
            this.tasks = tasks;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return tasks.size();
    }

    public Task getItem(int position) {

        Task itemTask = new Task();

        for(Task task: tasks){
            if (Long.toString(task.getId()).equals(Integer.toString(getCount() - position)))
                itemTask = task;
        }
        return itemTask;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView taskDesc;
        public TextView taskDate;
        public TextView taskTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        try {
            if (convertView == null) {
                view = inflater.inflate(R.layout.task_list_item, null);

                holder = new ViewHolder();
                holder.taskDesc = (TextView) view.findViewById(R.id.task_desc);
                holder.taskDate = (TextView) view.findViewById(R.id.task_date);
                holder.taskTime = (TextView) view.findViewById(R.id.task_time);

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
            }

            holder.taskDesc.setText(tasks.get(position).getDesc());
            holder.taskDesc.setText(tasks.get(position).getDesc());
            holder.taskDate.setText(tasks.get(position).getDate());
            holder.taskTime.setText(tasks.get(position).getTime());


        } catch (Exception e) {


        }
        return view;
    }
}
