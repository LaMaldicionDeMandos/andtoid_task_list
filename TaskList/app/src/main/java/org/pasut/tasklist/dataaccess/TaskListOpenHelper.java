package org.pasut.tasklist.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.collect.Maps;

import org.pasut.tasklist.dataaccess.versions.DatabaseVersionHelper;
import org.pasut.tasklist.dataaccess.versions.DatabaseVersionHelper2;

import java.util.Map;

/**
 * Created by cala on 16/02/14.
 */
public class TaskListOpenHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "task_list.db";
    private final static int DB_VERSION = 2;

    private final static Map<Integer, DatabaseVersionHelper> versionHelpers = Maps.newHashMap();
    static {
        versionHelpers.put(2, new DatabaseVersionHelper2());
    }
    public TaskListOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TaskListTable.TABLE_NAME + " ("
                + TaskListTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TaskListTable.TASK_LIST_NAME + " TEXT NOT NULL);");
        db.execSQL("CREATE TABLE " + TaskTable.TABLE_NAME + " ("
                + TaskTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TaskTable.TASK_NAME + " TEXT NOT NULL);");
        db.execSQL("CREATE TABLE " + TasksRelationTable.TABLE_NAME + " ("
                + TasksRelationTable.TASK_ID + " INTEGER NOT NULL,"
                + TasksRelationTable.LIST_ID + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        DatabaseVersionHelper helper = versionHelpers.get(newVersion);
        helper.upgrade(sqLiteDatabase, oldVersion);
    }

}
