package ca.xiaowei.chen2267127.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.xiaowei.chen2267127.CRUD.TaskDAO;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class DetailTaskActivity extends AppCompatActivity {
    TextView textTitle, textCategory, textAddress, textNotes,textDate;
    TaskDAO taskDAO;

    FirebaseFirestore firestoreDB;
    FirebaseAuth mAuth;
    String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);
        mAuth = FirebaseAuth.getInstance();
        bottomBarNavigate();
        getMyIntent();
        initialize();
        displayDetail();
    }

    public void initialize() {
        textTitle = findViewById(R.id.textTitle);
        textCategory = findViewById(R.id.textCategory);
        textAddress = findViewById(R.id.textAddress);
        textNotes = findViewById(R.id.textNotes);
        textDate = findViewById(R.id.textDate);
        taskDAO = new TaskDAO(this);
        taskDAO.open();
    }
public void displayDetail(){
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null
            && connectivityManager.getActiveNetworkInfo().isConnected()) {
        // Device is connected to the internet, fetch task list from Firestore
        getTaskListFromFirestore();
    } else {
        // Device is not connected to the internet, fetch task list from the local database
        getMyIntent();
    }
}

    private void getTaskListFromFirestore() {
        Log.d(taskId,"taskId");
        if (taskId != null) {
            firestoreDB = FirebaseFirestore.getInstance();
            CollectionReference tasksCollection = firestoreDB.collection("tasks");

            tasksCollection.document(taskId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String title = documentSnapshot.getString("title");
                            String category = documentSnapshot.getString("category");
                            String address = documentSnapshot.getString("address");
                            String notes = documentSnapshot.getString("notes");
                            Timestamp timestamp = documentSnapshot.getTimestamp("date");

                            // Convert Firestore timestamp to Date
                            Date date = timestamp != null ? timestamp.toDate() : null;
                            // Set the task details in the TextViews
                            // Format the date to display only the date portion
                            String formattedDate = formatDate(date);

                            textTitle.setText(title);
                            textCategory.setText(category);
                            textAddress.setText(address);
                            textNotes.setText(notes);
                            textDate.setText(formattedDate);
                        } else {
                            // Document does not exist
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Failed to fetch task details from Firestore
                    });
        }
    }
    private String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.format(date);
        } else {
            return "";
        }
    }
    public void getMyIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            taskId = intent.getStringExtra("taskId");  // Assuming you pass the taskId as an extra

            System.out.println("DetailTaskActivity received taskId: " + taskId);

            // Retrieve the task details from your data source (e.g., database or API)
//            Task task = taskDAO.getTaskById(taskId);
//
//            if (task != null) {
//                // Set the task details in the TextViews
//                textTitle.setText(task.getTitle());
//                textCategory.setText(task.getCategory());
//                textAddress.setText(task.getAddress());
//                textNotes.setText(task.getNotes());
//            }
        }
    }

    public void bottomBarNavigate() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomBar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.home_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.home_list) {
                startActivity(new Intent(getApplicationContext(), TaskListActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.home_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            return false;
        });
    }
}