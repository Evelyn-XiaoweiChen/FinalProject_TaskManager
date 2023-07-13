package ca.xiaowei.chen2267127.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.xiaowei.chen2267127.Adapter.SearchListAdapter;
import ca.xiaowei.chen2267127.CRUD.TaskDAO;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class SearchActivity extends AppCompatActivity {
    EditText searchEditText;
    RecyclerView recyclerView;
    SearchListAdapter searchListAdapter;
    List<Task> taskList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();
        bottomBarNavigate();

    }
    public void initialize(){
        searchEditText = findViewById(R.id.search_keyword);

        recyclerView = findViewById(R.id.search_recycleView);

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchListAdapter = new SearchListAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(searchListAdapter);

        // Retrieve the full task list from your data source (e.g., database or API)
        taskList = getTaskList();

        // Set the initial task list in the adapter
        searchListAdapter.setTaskList(taskList);
        doSearch();
    }
    public void doSearch(){
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();
                searchListAdapter.filter(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private List<Task> getTaskList() {
// fetch data from local
//         TaskDAO taskDAO = new TaskDAO(this);
//         taskDAO.open();
//         List<Task> taskList = taskDAO.getAllTasks();
//         taskDAO.close();
//        return taskList;


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            // Device is connected to the internet, fetch task list from Firestore
            return populateTaskListFromFirestore();
        } else {
            // Device is not connected to the internet, fetch task list from the local database
            return getTaskListFromDatabase();
        }
    }
    private List<Task> populateTaskListFromFirestore() {
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
                        searchListAdapter.setTaskList(taskList);
                        searchListAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SearchActivity.this, "Failed to fetch tasks from Firestore", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // User is not logged in, handle accordingly
        }

        return taskList;
    }

    private List<Task> getTaskListFromDatabase() {
        TaskDAO taskDAO = new TaskDAO(this);
        taskDAO.open();
        List<Task> taskList = taskDAO.getAllTasks();
        taskDAO.close();
        return taskList;
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