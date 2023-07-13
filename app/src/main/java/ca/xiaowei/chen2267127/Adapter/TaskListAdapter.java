package ca.xiaowei.chen2267127.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ca.xiaowei.chen2267127.Activity.DetailTaskActivity;
import ca.xiaowei.chen2267127.CRUD.TaskDAO;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private OnEditClickListener onEditClickListener;
    private TaskDAO taskDAO;

    public interface OnEditClickListener {
        void onEditClick(Task task);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }
    public TaskListAdapter(List<Task> taskList,Context context){
        this.taskList = taskList;
        this.context = context;
        taskDAO = new TaskDAO(context);
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

                System.out.println("adapter taskId"+task.getId());
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

        holder.editIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(task);
                }
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
                taskDAO.open();
                 // Delete task from the database
                taskDAO.deleteTask(task);
                Log.d("TaskListAdapter", "Task ID to delete: " + task.getId());



                // Refresh the task list from the database
                List<Task> updatedTaskList = taskDAO.getAllTasks();
                setTaskList(updatedTaskList);

                //delete from firestore
                deleteTaskFromFirestore(task);


            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void deleteTaskFromFirestore(Task task){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tasks").document(task.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document successfully deleted
                        // Perform any additional operations or UI updates
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while deleting the document
                        // Handle the error appropriately
                    }
                });
        notifyDataSetChanged();
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
            editIconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onEditClickListener != null) {
                        Task task = taskList.get(position);
                        onEditClickListener.onEditClick(task);
                    }
                }
            });
        }
    }

    public void setTaskList(List<Task> taskList){
        this.taskList = taskList;
    }

}
