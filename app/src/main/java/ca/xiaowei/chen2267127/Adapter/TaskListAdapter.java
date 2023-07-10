package ca.xiaowei.chen2267127.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.xiaowei.chen2267127.Activity.DetailTaskActivity;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;

    public TaskListAdapter(List<Task> taskList,Context context){
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public  TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListAdapter.TaskViewHolder holder, int position) {
        final Task task = taskList.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.categoryTextView.setText(task.getCategory());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open detail task activity
                Intent intent = new Intent(context, DetailTaskActivity.class);
                intent.putExtra("taskId", task.getId());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(task);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private void showDeleteConfirmationDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete task
                // You can implement your delete logic here
                // For example: taskList.remove(task);
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView categoryTextView;
        ImageView editIconImageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitle);
            categoryTextView = itemView.findViewById(R.id.taskCategory);
            editIconImageView = itemView.findViewById(R.id.editIconImageView);
        }
    }

    public void setTaskList(List<Task> taskList){
        this.taskList = taskList;
    }
}
