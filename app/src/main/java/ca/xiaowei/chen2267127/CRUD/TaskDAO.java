package ca.xiaowei.chen2267127.CRUD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.xiaowei.chen2267127.Model.Task;

public class TaskDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public TaskDAO (Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(DBContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(DBContract.TaskEntry.COLUMN_CATEGORY, task.getCategory());
        values.put(DBContract.TaskEntry.COLUMN_ADDRESS, task.getAddress());
        values.put(DBContract.TaskEntry.COLUMN_NOTES, task.getNotes());
        long insertedId = database.insert(DBContract.TaskEntry.TABLE_NAME, null, values);

    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = database.query(DBContract.TaskEntry.TABLE_NAME, null, null, null, null, null, null);



        while (cursor.moveToNext()) {
            int columnIndexTitle = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_TITLE);
            int columnIndexCategory = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_CATEGORY);
            int columnIndexAddress = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_ADDRESS);
            int columnIndexNotes = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_NOTES);
            // Check if the column index is valid
            if (columnIndexTitle != -1 && columnIndexCategory != -1 && columnIndexAddress != -1 && columnIndexNotes != -1) {
                String title = cursor.getString(columnIndexTitle);
                String category = cursor.getString(columnIndexCategory);
                String address = cursor.getString(columnIndexAddress);
                String notes = cursor.getString(columnIndexNotes);

                Task task = new Task(title, category, address, notes);
                taskList.add(task);
            }
        }

        cursor.close();
        return taskList;
    }
}
