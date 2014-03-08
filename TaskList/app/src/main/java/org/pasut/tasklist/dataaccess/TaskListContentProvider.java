package org.pasut.tasklist.dataaccess;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by cala on 16/02/14.
 */
public class TaskListContentProvider extends ContentProvider {
    private static final String EQUALS_NUMBER_TEMPLATE = "%s = %d";
    private static final String EQUALS_STRING_TEMPLATE = "%s = %s";
    private static final String ROWID = "ROWID";
    private static final UriMatcher uriMatcher;

    private static final String SCHEMA = "content://";
    private static final String AUTHORITY = "org.pasut.tasklist.provider.TaskList";
    private static final String TASK_LIST = "task_list";
    private static final String TASK_LIST_ID = "task_list/#";
    private static final String TASK = "task";
    private static final String TASKS_BY_TASK_LIST = "tasks_by_task_list/#";
    private static final String TASK_ID = "task/#";
    private static final String RELATION = "relation";

    public static final Uri CONTENT_URI = Uri.parse(SCHEMA + AUTHORITY + "/" + TASK_LIST);
    public static final Uri CONTENT_URI_TASK_LISTS = CONTENT_URI;
    public static final Uri CONTENT_URI_TASK_LIST_BY_ID = Uri.parse(SCHEMA + AUTHORITY + "/" + TASK_LIST_ID);
    public static final Uri CONTENT_URI_TASKS = Uri.parse(SCHEMA + AUTHORITY + "/" + TASK);
    public static final Uri CONTENT_URI_TASK_BY_ID = Uri.parse(SCHEMA + AUTHORITY + "/" + TASK_ID);
    public static final Uri CONTENT_URI_TASK_BY_TASK_LIST = Uri.parse(SCHEMA + AUTHORITY + "/" + TASKS_BY_TASK_LIST);
    public static final Uri CONTENT_URI_RELATION = Uri.parse(SCHEMA + AUTHORITY + "/" + RELATION);
    public static final String DELETE_TASK_LIST_TEMPLATE = TaskListTable._ID + "=?";
    public static final String DELETE_RELATION_TEMPLATE = TasksRelationTable.LIST_ID + "=? and "
            + TasksRelationTable.TASK_ID + "=?";
    private static final int TASK_LIST_INT = 1;
    private static final int TASK_LIST_ID_INT = 2;
    private static final int TASK_INT = 3;
    private static final int TASK_ID_INT = 4;
    private static final int TASKS_BY_TASK_LIST_ID_INT = 5;
    private static final int RELATION_INT = 6;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TASK_LIST, TASK_LIST_INT);
        uriMatcher.addURI(AUTHORITY, TASK_LIST_ID, TASK_LIST_ID_INT);
        uriMatcher.addURI(AUTHORITY, TASK, TASK_INT);
        uriMatcher.addURI(AUTHORITY, TASK_ID, TASK_ID_INT);
        uriMatcher.addURI(AUTHORITY, TASKS_BY_TASK_LIST, TASKS_BY_TASK_LIST_ID_INT);
        uriMatcher.addURI(AUTHORITY, RELATION, RELATION_INT);
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
            case TASK_INT:
                return findAllTasks();
            case TASK_ID_INT:
                return findTaskById(Long.valueOf(uri.getPathSegments().get(1)));
            case TASKS_BY_TASK_LIST_ID_INT:
                return findTasksByTaskListId(Long.valueOf(uri.getPathSegments().get(1)));
            default:
                return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case TASK_LIST_INT:
                return insert(database, contentValues, TaskListTable.TABLE_NAME, CONTENT_URI_TASK_LIST_BY_ID);
            case TASK_INT:
                return insert(database, contentValues, TaskTable.TABLE_NAME, CONTENT_URI_TASK_BY_ID);
            case RELATION_INT:
                return insert(database, contentValues, TasksRelationTable.TABLE_NAME, CONTENT_URI_RELATION);
            default:
                return uri;
        }
    }

    private Uri insert(SQLiteDatabase database, ContentValues values, String tableName, Uri uri) {
        long rowId = database.insert(tableName, null, values);
        Uri newListUri = ContentUris.withAppendedId(uri, rowId);
        return newListUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] args) {
        SQLiteDatabase database = helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case RELATION_INT:
                return database.delete(TasksRelationTable.TABLE_NAME, where, args);
            case TASK_LIST_INT:
                return database.delete(TaskListTable.TABLE_NAME, where, args);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private Cursor findAll(String tableName) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        SQLiteDatabase db = helper.getReadableDatabase();
        return builder.query(db, null, null, null, null, null, null);
    }

    private Cursor findAllTaskList() {
        return findAll(TaskListTable.TABLE_NAME);
    }

    private Cursor findAllTasks() {
        return findAll(TaskTable.TABLE_NAME);
    }

    private Cursor findByProperty(String tableName, String propertyName, Long propertyValue) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        SQLiteDatabase db = helper.getReadableDatabase();
        builder.appendWhere(String.format(EQUALS_NUMBER_TEMPLATE, propertyName, propertyValue));
        return builder.query(db, null, null, null, null, null, null);
    }

    private Cursor findById(Long id, String tableName) {
        return findByProperty(tableName, ROWID, id);
    }

    private Cursor findTaskListById(Long id) {
        return findById(id, TaskListTable.TABLE_NAME);
    }

    private Cursor findTaskById(Long id) {
        return findById(id, TaskTable.TABLE_NAME);
    }

    private Cursor findTasksByTaskListId(Long id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Joiner.on(",").join(TasksRelationTable.TABLE_NAME, TaskTable.TABLE_NAME));
        SQLiteDatabase db = helper.getReadableDatabase();
        Map<String, String> map = Maps.newHashMap();
        map.put(TaskTable._ID, TaskTable._ID);
        map.put(TaskTable.TASK_NAME, TaskTable.TASK_NAME);
        builder.setProjectionMap(map);
        builder.appendWhere(String.format(EQUALS_NUMBER_TEMPLATE, TasksRelationTable.LIST_ID, id)
        + " AND " + String.format(EQUALS_STRING_TEMPLATE, TasksRelationTable.TASK_ID, TaskTable._ID));
        return builder.query(db, null, null, null, null, null, null);
    }
}
