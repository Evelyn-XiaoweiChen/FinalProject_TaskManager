package ca.xiaowei.chen2267127.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ca.xiaowei.chen2267127.Adapter.TaskListAdapter;
import ca.xiaowei.chen2267127.CRUD.TaskDAO;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class TaskListActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, profile, logout;
    RecyclerView recyclerView;
    TaskListAdapter taskListAdapter;
    FloatingActionButton createTaskBtn;
    EditText taskTitle, taskCategory, taskAddress, taskNotes;
    TaskDAO taskDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        initialize();
        setupRecyclerView();
        populateTaskList();

    }

    public void initialize() {
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.exitApp);
        createTaskBtn = findViewById(R.id.fabCreateTask);
        taskDAO = new TaskDAO(this);
        taskDAO.open();
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(TaskListActivity.this, HomeActivity.class);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(TaskListActivity.this, ProfileActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(TaskListActivity.this, LoginActivity.class);
            }
        });
        createTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTaskFormDialog();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the taskDAO object to release any resources
        taskDAO.close();
    }
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    private void setupRecyclerView() {
        taskListAdapter = new TaskListAdapter(new ArrayList<Task>(), this);
        recyclerView.setAdapter(taskListAdapter);
    }

    private void populateTaskList() {
        // Retrieve the list of tasks from your data source (e.g., database or API)
        List<Task> taskList = getTasks();

        // Update the task list in the adapter
        taskListAdapter.setTaskList(taskList);
        taskListAdapter.notifyDataSetChanged();
    }

    // Method to retrieve tasks from your data source
    private List<Task> getTasks() {
        // Implement your logic to fetch tasks from a data source (e.g., database or API)
        // Return the list of tasks
        return new ArrayList<Task>(); // Replace with your actual data retrieval logic
    }

    private void showTaskFormDialog() {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(this).inflate(R.layout.create_task_form, null);
        builder.setView(view);

        // Get references to the views in the dialog
        taskTitle = view.findViewById(R.id.taskFormTitle);
        taskCategory = view.findViewById(R.id.taskFormCategory);
        taskAddress = view.findViewById(R.id.taskFormAddress);
        taskNotes = view.findViewById(R.id.taskFormNotes);

        // Set up the dialog buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the task information from the EditText fields
                String title = taskTitle.getText().toString();
                String category = taskCategory.getText().toString();
                String address = taskAddress.getText().toString();
                String notes = taskNotes.getText().toString();

                // Create a new Task object with the gathered information
                Task newTask = new Task(title, category, address, notes);

                // Add the new task to your data source (e.g., database or API)
                taskDAO.addTask(newTask);

                // Update the task list in the adapter
                List<Task> updatedTaskList = taskDAO.getAllTasks();
                taskListAdapter.setTaskList(updatedTaskList);
                taskListAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }


}