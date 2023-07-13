package ca.xiaowei.chen2267127.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.xiaowei.chen2267127.Activity.DetailTaskActivity;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.TaskViewHolder>{
    private List<Task> taskList;
    private List<Task> filteredTaskList; // List to hold filtered tasks
    private Context context;

    public SearchListAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.filteredTaskList = new ArrayList<>(taskList);
        this.context = context;
    }
    @NonNull
    @Override
    public SearchListAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_task_list_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListAdapter.TaskViewHolder holder, int position) {
        final Task task = filteredTaskList.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.categoryTextView.setText(task.getCategory());

        // Convert Firestore timestamp to Date
//        Timestamp timestamp =  task.getDate();
//        Date date = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = dateFormat.format(task.getDate());
        holder.dateTextView.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open detail task activity
                Intent intent = new Intent(context, DetailTaskActivity.class);
                intent.putExtra("taskId", task.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredTaskList.size();
    }

    public void filter(String keyword) {
        keyword = keyword.toLowerCase(Locale.getDefault());
        filteredTaskList.clear();

        if (keyword.length() == 0) {
            filteredTaskList.addAll(taskList);
        } else {
            for (Task task : taskList) {
                if (task.getTitle().toLowerCase(Locale.getDefault()).contains(keyword)
                        || task.getCategory().toLowerCase(Locale.getDefault()).contains(keyword)) {
                    filteredTaskList.add(task);
                }
            }
        }

        notifyDataSetChanged();
    }
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        this.filteredTaskList = new ArrayList<>(taskList);
        notifyDataSetChanged();
    }
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView categoryTextView;
        TextView dateTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textTitle);
            categoryTextView = itemView.findViewById(R.id.textCategory);
            dateTextView = itemView.findViewById(R.id.textDate);
        }
    }
}
