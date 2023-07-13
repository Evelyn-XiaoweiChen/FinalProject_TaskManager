package ca.xiaowei.chen2267127.CRUD;

import android.provider.BaseColumns;

public final class DBContract {
    private DBContract() {} // Private constructor to prevent instantiation

    public static class TaskEntry implements BaseColumns {
        public static final String _ID = BaseColumns._ID;
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
    }
}
