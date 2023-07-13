package ca.xiaowei.chen2267127.CRUD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Time;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.xiaowei.chen2267127.Model.Task;

public class TaskDAO extends DBHelper{
    private SQLiteDatabase database;
    private DBHelper dbHelper;


    public TaskDAO (Context context) {
        super(context);
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(DBContract.TaskEntry.COLUMN_USER_ID, task.getUserId()); // Store the user ID
        values.put(DBContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(DBContract.TaskEntry.COLUMN_CATEGORY, task.getCategory());
        values.put(DBContract.TaskEntry.COLUMN_ADDRESS, task.getAddress());
        values.put(DBContract.TaskEntry.COLUMN_NOTES, task.getNotes());
        values.put(DBContract.TaskEntry.COLUMN_DATE, task.getDate().getTime()); // Store the date as milliseconds
        values.put(DBContract.TaskEntry.COLUMN_TIME, task.getTime().getTime()); // Store the time as milliseconds

        long insertedId = database.insert(DBContract.TaskEntry.TABLE_NAME, null, values);
        Log.d("TaskDAO", "Inserted ID: " + insertedId);
        if (insertedId != -1) {
            task.setId(String.valueOf(insertedId)); // Set the generated ID on the task object
        }

        return insertedId;

    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = database.query(DBContract.TaskEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int columnIndexId = cursor.getColumnIndex(DBContract.TaskEntry._ID);
            int columnIndexUserId = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_USER_ID);
            int columnIndexTitle = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_TITLE);
            int columnIndexCategory = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_CATEGORY);
            int columnIndexAddress = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_ADDRESS);
            int columnIndexNotes = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_NOTES);
            int columnIndexDate = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_DATE);
            int columnIndexTime = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_TIME);
            // Check if the column index is valid
            if (columnIndexUserId != -1 && columnIndexTitle != -1 && columnIndexCategory != -1 && columnIndexAddress != -1
                    && columnIndexNotes != -1 && columnIndexDate != -1 && columnIndexTime != -1) {
                String id = cursor.getString(columnIndexId);
                String userId = cursor.getString(columnIndexUserId);
                String title = cursor.getString(columnIndexTitle);
                String category = cursor.getString(columnIndexCategory);
                String address = cursor.getString(columnIndexAddress);
                String notes = cursor.getString(columnIndexNotes);
                long dateInMillis = cursor.getLong(columnIndexDate);
                long timeInMillis = cursor.getLong(columnIndexTime);
                Date date = new Date(dateInMillis);
                Time time = new Time(timeInMillis);

                Task task = new Task(id, userId,title, category, address, notes, date, time);
                taskList.add(task);
            }
        }

        cursor.close();
        return taskList;
    }

    public void deleteTask(Task task) {
        String selection = DBContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {task.getId()};
        int deletedRows = database.delete(DBContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateTask(Task task){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(DBContract.TaskEntry.COLUMN_CATEGORY, task.getCategory());
        values.put(DBContract.TaskEntry.COLUMN_ADDRESS, task.getAddress());
        values.put(DBContract.TaskEntry.COLUMN_NOTES, task.getNotes());
        values.put(DBContract.TaskEntry.COLUMN_DATE, task.getDate().getTime());
        values.put(DBContract.TaskEntry.COLUMN_TIME, task.getTime().getTime());


        String whereClause = DBContract.TaskEntry._ID + " = ?";
        String[] whereArgs = {task.getId()};

        db.update(DBContract.TaskEntry.TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }


    public Task getTaskById(String taskId) {
        SQLiteDatabase db = getReadableDatabase();
        Task task = null;

        String[] projection = {
                DBContract.TaskEntry._ID,
                DBContract.TaskEntry.COLUMN_USER_ID,
                DBContract.TaskEntry.COLUMN_TITLE,
                DBContract.TaskEntry.COLUMN_CATEGORY,
                DBContract.TaskEntry.COLUMN_ADDRESS,
                DBContract.TaskEntry.COLUMN_NOTES,
                DBContract.TaskEntry.COLUMN_DATE,
                DBContract.TaskEntry.COLUMN_TIME,
        };

        String selection = DBContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {taskId};

        Cursor cursor = db.query(
                DBContract.TaskEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBContract.TaskEntry._ID);
            int userIdIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_USER_ID);
            int titleIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_TITLE);
            int categoryIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_CATEGORY);
            int addressIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_ADDRESS);
            int notesIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_NOTES);
            int dateIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_DATE);
            int timeIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_TIME);


            String id = cursor.getString(idIndex);
            String userId = cursor.getString(userIdIndex);
            String title = cursor.getString(titleIndex);
            String category = cursor.getString(categoryIndex);
            String address = cursor.getString(addressIndex);
            String notes = cursor.getString(notesIndex);
            long dateInMillis = cursor.getLong(dateIndex);
            long timeInMillis = cursor.getLong(timeIndex);
            Time time = new Time(timeInMillis);

            task = new Task(id, userId, title, category, address, notes, new Date(dateInMillis), time);
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return task;
    }

    private Date parseStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Date parseStringToTime(String timeString) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            return timeFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
