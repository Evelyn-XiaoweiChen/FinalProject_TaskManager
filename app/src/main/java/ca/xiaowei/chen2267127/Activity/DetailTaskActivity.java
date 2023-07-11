package ca.xiaowei.chen2267127.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ca.xiaowei.chen2267127.CRUD.TaskDAO;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class DetailTaskActivity extends AppCompatActivity {
TextView textTitle,textCategory,textAddress,textNotes;
TaskDAO taskDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);
        bottomBarNavigate();
        initialize();
        getMyIntent();

    }
    public void initialize(){
         textTitle = findViewById(R.id.textTitle);
         textCategory = findViewById(R.id.textCategory);
         textAddress = findViewById(R.id.textAddress);
         textNotes = findViewById(R.id.textNotes);
         taskDAO = new TaskDAO(this);
         taskDAO.open();
    }
    public void getMyIntent(){
        Intent intent = getIntent();
        if (intent != null) {
            int taskId = intent.getIntExtra("taskId", -1);  // Assuming you pass the taskId as an extra
            // Retrieve the task details from your data source (e.g., database or API)
            Task task = taskDAO.getTaskById(taskId);

            // Set the task details in the TextViews
            if (task != null) {
                textTitle.setText(task.getTitle());
                textCategory.setText(task.getCategory());
                textAddress.setText(task.getAddress());
                textNotes.setText(task.getNotes());
            }
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