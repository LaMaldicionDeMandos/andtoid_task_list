package org.pasut.tasklist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.common.collect.Iterables;

import org.pasut.tasklist.dataaccess.TaskListContentProvider;
import org.pasut.tasklist.dataaccess.TaskListTable;
import org.pasut.tasklist.entity.TaskList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelo on 18/02/14.
 */
public class TaskListEntityService {
    private final static int TASK_LIST_ID_INDEX = 0;
    private final static int TASK_LIST_NAME_INDEX = 1;

    private final Context context;

    public TaskListEntityService(Context context) {
        this.context = context;
    }

    public List<TaskList> findAllTaskLists() {
        Cursor cursor = context.getContentResolver().query(TaskListContentProvider.CONTENT_URI, null, null, null, null);
        return convertToTaskList(cursor);
    }

    public TaskList findTaskListById(Long id) {
        Uri uri = TaskListContentProvider.CONTENT_URI_TASK_LIST_BY_ID;
        uri = ContentUris.withAppendedId(uri, id);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        List<TaskList> list = convertToTaskList(cursor);
        return Iterables.getFirst(list, null);
    }

    private List<TaskList> convertToTaskList(Cursor cursor) {
        List<TaskList> list = new ArrayList<TaskList>();
        while (cursor.moveToNext()) {
            TaskList taskList = new TaskList(cursor.getLong(TASK_LIST_ID_INDEX), cursor.getString(TASK_LIST_NAME_INDEX));
            list.add(taskList);
        }
        return list;
    }

    public TaskList insertNewTaskList(TaskList taskList) {
        ContentValues values = new ContentValues();
        values.put(TaskListTable.TASK_LIST_NAME, taskList.getName());
        Uri uri = context.getContentResolver().insert(TaskListContentProvider.CONTENT_URI_TASK_LISTS, values);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        List<TaskList> list = convertToTaskList(cursor);
        if (list.isEmpty()) throw new RuntimeException("An error ocurred during the insert and can't vave de object");
        return list.get(0);
    }
}
