package ca.xiaowei.chen2267127.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ca.xiaowei.chen2267127.Adapter.TaskListAdapter;
import ca.xiaowei.chen2267127.CRUD.TaskDAO;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class TaskListActivity extends AppCompatActivity {
    private static final int CREATE_TASK_REQUEST_CODE = 1;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, profile, logout;
    RecyclerView recyclerView;
    TaskListAdapter taskListAdapter;
    FloatingActionButton createTaskBtn;
    TaskDAO taskDAO;
    FirebaseFirestore firestoreDB;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        initialize();


        firestoreDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            // User is signed in
            userId = user.getUid();
            // Store the userId in Firestore or use it as needed
        } else {
            // User is not signed in or an error occurred
            // Handle the case accordingly
        }
        setupRecyclerView();
        editTask();

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
                Intent intent = new Intent(TaskListActivity.this, CreateTaskActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_TASK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isNewTaskAdded = data.getBooleanExtra("isNewTaskAdded", false);
            if (isNewTaskAdded) {
                // Refresh the task list
                populateTaskList();
            }
        }
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
    @Override
    protected void onResume() {
        super.onResume();
        populateTaskList();
    }
    private void setupRecyclerView() {
        taskListAdapter = new TaskListAdapter(new ArrayList<Task>(), this);
        recyclerView.setAdapter(taskListAdapter);
    }

    private void populateTaskList() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            // Device is connected to the internet, fetch task list from Firestore
             getTaskListFromFirestore();
        } else {
            // Device is not connected to the internet, fetch task list from the local database
            getTaskListFromDatabase();
        }

    }

    private void getTaskListFromDatabase() {
     // Retrieve the list of tasks from your data source (e.g., database or API)
        List<Task> taskList = taskDAO.getAllTasks();

        // Update the task list in the adapter
        taskListAdapter.setTaskList(taskList);
        taskListAdapter.notifyDataSetChanged();
    }

    private void getTaskListFromFirestore() {
        List<Task> taskList = new ArrayList<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String loggedInUserId = currentUser.getUid();

            FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
            CollectionReference tasksCollection = firestoreDB.collection("tasks");

            Query query = tasksCollection.whereEqualTo("userId", loggedInUserId);

            query.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String taskId = documentSnapshot.getId();
                            String userId = documentSnapshot.getString("userId");
                            String title = documentSnapshot.getString("title");
                            String category = documentSnapshot.getString("category");
                            String address = documentSnapshot.getString("address");
                            String notes = documentSnapshot.getString("notes");
                            Timestamp timestamp = documentSnapshot.getTimestamp("date");

                            // Convert Firestore timestamp to Date
                            Date date = timestamp != null ? timestamp.toDate() : null;

                            Task task = new Task(taskId, userId, title, category, address, notes, date, null);
                            taskList.add(task);
                        }

                        // Update the task list in the adapter
                        taskListAdapter.setTaskList(taskList);
                        taskListAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TaskListActivity.this, "Failed to fetch tasks from Firestore", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // User is not logged in, handle accordingly
        }


    }

    private void editTask() {
        taskListAdapter.setOnEditClickListener(new TaskListAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(Task task) {
                showUpdateTaskDialog(task);
            }
        });
    }

    private void showUpdateTaskDialog(Task task) {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(this).inflate(R.layout.edit_task_dialog, null);
        builder.setView(view);

        // Get references to the views in the dialog
        EditText taskTitle = view.findViewById(R.id.updateTitle);
        EditText taskCategory = view.findViewById(R.id.updateCategory);
        EditText taskAddress = view.findViewById(R.id.updateAddress);
        EditText taskNotes = view.findViewById(R.id.updateNotes);

        // Set the initial values for the EditText fields
        taskTitle.setText(task.getTitle());
        taskCategory.setText(task.getCategory());
        taskAddress.setText(task.getAddress());
        taskNotes.setText(task.getNotes());

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the updated task information from the EditText fields
                String updatedTitle = taskTitle.getText().toString();
                String updatedCategory = taskCategory.getText().toString();
                String updatedAddress = taskAddress.getText().toString();
                String updatedNotes = taskNotes.getText().toString();

                // Update the task object with the new information
                task.setTitle(updatedTitle);
                task.setCategory(updatedCategory);
                task.setAddress(updatedAddress);
                task.setNotes(updatedNotes);

                // Perform the update operation in the local storage
                taskDAO.updateTask(task);
                // Update the task in Firestore
                updateTaskInFirestore(task);

                // Refresh the task list
                populateTaskList();

                Toast.makeText(TaskListActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateTaskInFirestore(Task task) {
        // Get a reference to the task document in Firestore
        DocumentReference taskDocument = firestoreDB.collection("tasks").document(task.getId());

        // Update the task document in Firestore
        taskDocument.set(task)
                .addOnSuccessListener(aVoid -> {
                    // Task successfully updated in Firestore
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occurred during the update operation
                    Toast.makeText(TaskListActivity.this, "Failed to update task in Firestore", Toast.LENGTH_SHORT).show();
                });
    }

}