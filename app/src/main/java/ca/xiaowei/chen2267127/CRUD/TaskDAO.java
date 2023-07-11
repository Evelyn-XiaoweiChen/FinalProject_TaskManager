package ca.xiaowei.chen2267127.CRUD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
        values.put(DBContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(DBContract.TaskEntry.COLUMN_CATEGORY, task.getCategory());
        values.put(DBContract.TaskEntry.COLUMN_ADDRESS, task.getAddress());
        values.put(DBContract.TaskEntry.COLUMN_NOTES, task.getNotes());
        long insertedId = database.insert(DBContract.TaskEntry.TABLE_NAME, null, values);

        if (insertedId != -1) {
            task.setId((int) insertedId); // Set the generated ID on the task object
        }

        return insertedId;

    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = database.query(DBContract.TaskEntry.TABLE_NAME, null, null, null, null, null, null);



        while (cursor.moveToNext()) {
            int columnIndexId = cursor.getColumnIndex(DBContract.TaskEntry._ID);
            int columnIndexTitle = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_TITLE);
            int columnIndexCategory = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_CATEGORY);
            int columnIndexAddress = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_ADDRESS);
            int columnIndexNotes = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_NOTES);
            // Check if the column index is valid
            if (columnIndexTitle != -1 && columnIndexCategory != -1 && columnIndexAddress != -1 && columnIndexNotes != -1) {
                int id = cursor.getInt(columnIndexId);
                String title = cursor.getString(columnIndexTitle);
                String category = cursor.getString(columnIndexCategory);
                String address = cursor.getString(columnIndexAddress);
                String notes = cursor.getString(columnIndexNotes);

                Task task = new Task(title, category, address, notes);
                task.setId(id);
                taskList.add(task);
            }
        }

        cursor.close();
        return taskList;
    }

    public void deleteTask(Task task) {

        String selection = DBContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};
        int deletedRows = database.delete(DBContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateTask(Task task){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(DBContract.TaskEntry.COLUMN_CATEGORY, task.getCategory());
        values.put(DBContract.TaskEntry.COLUMN_ADDRESS, task.getAddress());
        values.put(DBContract.TaskEntry.COLUMN_NOTES, task.getNotes());

        String whereClause = DBContract.TaskEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(task.getId())};

        db.update(DBContract.TaskEntry.TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }

    public Task getTaskById(int taskId) {
        SQLiteDatabase db = getReadableDatabase();
        Task task = null;

        String[] projection = {
                DBContract.TaskEntry._ID,
                DBContract.TaskEntry.COLUMN_TITLE,
                DBContract.TaskEntry.COLUMN_CATEGORY,
                DBContract.TaskEntry.COLUMN_ADDRESS,
                DBContract.TaskEntry.COLUMN_NOTES
        };

        String selection = DBContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};

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
            int titleIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_TITLE);
            int categoryIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_CATEGORY);
            int addressIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_ADDRESS);
            int notesIndex = cursor.getColumnIndex(DBContract.TaskEntry.COLUMN_NOTES);

            int id = cursor.getInt(idIndex);
            String title = cursor.getString(titleIndex);
            String category = cursor.getString(categoryIndex);
            String address = cursor.getString(addressIndex);
            String notes = cursor.getString(notesIndex);

            task = new Task(title, category, address, notes);
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return task;
    }
}
