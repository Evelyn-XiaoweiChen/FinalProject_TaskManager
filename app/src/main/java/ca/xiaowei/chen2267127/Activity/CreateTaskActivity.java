package ca.xiaowei.chen2267127.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import ca.xiaowei.chen2267127.CRUD.TaskDAO;
import ca.xiaowei.chen2267127.Model.Task;
import ca.xiaowei.chen2267127.R;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener {
    EditText taskTitle, taskCategory, taskAddress, taskNotes;
    Button selectDateBtn, selectTimeBtn, addTaskBtn, cancelBtn;
    TaskDAO taskDAO;
    private FirebaseFirestore firestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        initialize();
    }

    public void initialize() {
        firestoreDB = FirebaseFirestore.getInstance();
        taskTitle = findViewById(R.id.taskFormTitle);
        taskCategory = findViewById(R.id.taskFormCategory);
        taskAddress = findViewById(R.id.taskFormAddress);
        taskNotes = findViewById(R.id.taskFormNotes);
        selectDateBtn = findViewById(R.id.selectDateBtn);
        selectDateBtn.setOnClickListener(this);
        selectTimeBtn = findViewById(R.id.selectTimeBtn);
        selectTimeBtn.setOnClickListener(this);
        addTaskBtn = findViewById(R.id.addNewTaskBtn);
        addTaskBtn.setOnClickListener(this);
        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(this);
        taskDAO = new TaskDAO(this);
        taskDAO.open();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.selectDateBtn) {
            setDate();
        } else if (v.getId() == R.id.selectTimeBtn) {
            setTime();
        } else if (v.getId() == R.id.addNewTaskBtn) {
            addNewTask();
        } else if (v.getId() == R.id.cancelBtn) {
            cancelCreateTask();
        }
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog to allow the user to select the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle the selected date
                        // You can store the selected date in a variable or update the UI accordingly
                        // For example, you can display the selected date in a TextView
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        selectDateBtn.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void setTime() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog to allow the user to select the time
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle the selected time
                        // Create a new Time object with the selected hour and minute
                        Time selectedTime = new Time(hourOfDay, minute, 0);

                        // Format the time for display in the button
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                        String formattedTime = timeFormat.format(selectedTime);

                        // Update the button text with the formatted time
                        selectTimeBtn.setText(formattedTime);
                    }
                }, hour, minute, false);

        // Show the TimePickerDialog
        timePickerDialog.show();

    }

    private void addNewTask() {

        // Retrieve the task information from the EditText fields
        String title = taskTitle.getText().toString();
        String category = taskCategory.getText().toString();
        String address = taskAddress.getText().toString();
        String notes = taskNotes.getText().toString();
        String taskId = UUID.randomUUID().toString();

        // Get the userId from the intent extras
        String userId = getIntent().getStringExtra("userId");

        // Set the selected date and time on the new task
        String selectedDate = selectDateBtn.getText().toString();
        String selectedTime = selectTimeBtn.getText().toString();

        // Convert the selected date and time to Date and Time objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = null;
        Time time = null;
        try {
            date = dateFormat.parse(selectedDate);
            time = new Time(timeFormat.parse(selectedTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create a new Task object with the gathered information
        Task newTask = new Task(taskId, userId, title, category, address, notes, date, time);


        // Add the new task to the local storage
        taskDAO.addTask(newTask);
        // Save the new task to Firestore
        saveTaskToFirestore(newTask,userId);

        // Finish the activity and return to the TaskListActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isNewTaskAdded", true);
        setResult(RESULT_OK, resultIntent);
        finish();

    }

    private void cancelCreateTask() {
        // Finish the activity and return to the TaskListActivity without creating a new task
        setResult(RESULT_CANCELED);
        finish();
    }

    private void saveTaskToFirestore(Task task,String userId) {
        // Get a reference to the "tasks" collection in Firestore
        CollectionReference tasksCollection = firestoreDB.collection("tasks");

        // Set the user ID in the task object
        task.setUserId(userId);
        // Add the task document to Firestore
        tasksCollection.document(task.getId()).set(task)
                .addOnSuccessListener(aVoid -> {
                    // Task successfully saved to Firestore
                    Toast.makeText(CreateTaskActivity.this, "Succeed to save task to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occurred during the save operation
                    Toast.makeText(CreateTaskActivity.this, "Failed to save task to Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
