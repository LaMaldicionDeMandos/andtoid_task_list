package org.pasut.tasklist.dataaccess;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cala on 16/02/14.
 */
public class TaskListContentProvider extends ContentProvider {
    private static final String ROWID = "ROWID = %d";
    private static final UriMatcher uriMatcher;
    private static final Map<String, String> taskListProjection;

    private static final String SCHEMA = "content://";
    private static final String AUTHORITY = "org.pasut.tasklist.provider.TaskList";
    private static final String TASK_LIST = "task_list";
    private static final String TASK_LIST_ID = "task_list/#";

    public static final Uri CONTENT_URI = Uri.parse(SCHEMA + AUTHORITY + "/" + TASK_LIST);
    public static final Uri CONTENT_URI_TASK_LISTS = CONTENT_URI;
    public static final Uri CONTENT_URI_TASK_LIST_BY_ID = Uri.parse(SCHEMA + AUTHORITY + "/" + TASK_LIST_ID);
    private static final int TASK_LIST_INT = 1;
    private static final int TASK_LIST_ID_INT = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TASK_LIST, TASK_LIST_INT);
        uriMatcher.addURI(AUTHORITY, TASK_LIST_ID, TASK_LIST_ID_INT);

        taskListProjection = new HashMap<String, String>();
        taskListProjection.put(TaskListTable._ID, TaskListTable._ID);
        taskListProjection.put(TaskListTable.TASK_LIST_NAME, TaskListTable.TASK_LIST_NAME);
    }

    private TaskListOpenHelper helper;

    @Override
    public boolean onCreate() {
        helper = new TaskListOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case TASK_LIST_INT:
                return findAllTaskList();
            case TASK_LIST_ID_INT:
                return findTaskListById(Long.valueOf(uri.getPathSegments().get(1)));
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (uriMatcher.match(uri) == TASK_LIST_INT) {
            SQLiteDatabase database = helper.getWritableDatabase();
            long rowId = database.insert(TaskListTable.TABLE_NAME, null, contentValues);
            Uri newListUri = ContentUris.withAppendedId(CONTENT_URI_TASK_LIST_BY_ID, rowId);
            return newListUri;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private Cursor findAllTaskList() {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TaskListTable.TABLE_NAME);
        SQLiteDatabase db = helper.getReadableDatabase();
        return builder.query(db, null, null, null, null, null, null);
    }

    private Cursor findTaskListById(Long id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TaskListTable.TABLE_NAME);
        SQLiteDatabase db = helper.getReadableDatabase();
        builder.appendWhere(String.format(ROWID, id));
        return builder.query(db, null, null, null, null, null, null);
    }
}
