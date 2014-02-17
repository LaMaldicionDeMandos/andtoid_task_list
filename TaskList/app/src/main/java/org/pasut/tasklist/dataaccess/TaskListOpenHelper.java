package org.pasut.tasklist.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cala on 16/02/14.
 */
public class TaskListOpenHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "task_list_db";
    private final static int DB_VERSION = 1;
    public TaskListOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TaskListTable.TABLE_NAME + " ("
                + TaskListTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TaskListTable.TASK_LIST_NAME + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
