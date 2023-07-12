package ca.xiaowei.chen2267127.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
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