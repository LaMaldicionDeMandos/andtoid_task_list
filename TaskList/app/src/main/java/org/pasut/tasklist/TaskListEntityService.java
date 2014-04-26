package org.pasut.tasklist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.pasut.tasklist.dataaccess.TaskListContentProvider;
import org.pasut.tasklist.dataaccess.TaskListTable;
import org.pasut.tasklist.dataaccess.TaskTable;
import org.pasut.tasklist.dataaccess.TasksRelationTable;
import org.pasut.tasklist.entity.Task;
import org.pasut.tasklist.entity.TaskList;

import java.util.List;

/**
 * Created by marcelo on 18/02/14.
 */
public class TaskListEntityService {

    private static interface Converter<T> {
        List<T> convert(Cursor cursor);
    }

    private static class TaskListConverter implements Converter<TaskList> {
        @Override
        public List<TaskList> convert(Cursor cursor) {
            List<TaskList> list = Lists.newArrayList();
            while (cursor.moveToNext()) {
                TaskList taskList = new TaskList(cursor.getLong(TASK_LIST_ID_INDEX), cursor.getString(TASK_LIST_NAME_INDEX));
                list.add(taskList);
            }
            return list;
        }
    }

    private static class TaskConverter implements Converter<Task> {
        @Override
        public List<Task> convert(Cursor cursor) {
            List<Task> list = Lists.newArrayList();
            while (cursor.moveToNext()) {
                Task task = new Task(cursor.getLong(TASK_ID_INDEX), cursor.getString(TASK_NAME_INDEX));
                list.add(task);
            }
            return list;
        }
    }

    private final static Converter<TaskList> TASK_LIST_CONVERTER = new TaskListConverter();
    private final static Converter<Task> TASK_CONVERTER = new TaskConverter();
    private final static int TASK_LIST_ID_INDEX = 0;
    private final static int TASK_LIST_NAME_INDEX = 1;
    private final static int TASK_ID_INDEX = 0;
    private final static int TASK_NAME_INDEX = 1;

    private final Context context;

    public TaskListEntityService(Context context) {
        this.context = context;
    }

    public List<TaskList> findAllTaskLists() {
        Cursor cursor = context.getContentResolver().query(TaskListContentProvider.CONTENT_URI, null, null, null, null);
        return TASK_LIST_CONVERTER.convert(cursor);
    }

    public List<Task> findAllTasks() {
        Cursor cursor = context.getContentResolver().query(TaskListContentProvider.CONTENT_URI_TASKS, null, null, null, null);
        return TASK_CONVERTER.convert(cursor);
    }

    public TaskList findTaskListById(Long id) {
        Uri uri = TaskListContentProvider.CONTENT_URI_TASK_LIST_BY_ID;
        uri = ContentUris.withAppendedId(uri, id);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        List<TaskList> list = TASK_LIST_CONVERTER.convert(cursor);
        return Iterables.getFirst(list, null);
    }

    public List<Task> findTasksByListId(Long id) {
        Uri uri = TaskListContentProvider.CONTENT_URI_TASK_BY_TASK_LIST;
        uri = ContentUris.withAppendedId(uri, id);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        return TASK_CONVERTER.convert(cursor);
    }

    public TaskList insert(TaskList taskList) {
        ContentValues values = new ContentValues();
        values.put(TaskListTable.TASK_LIST_NAME, taskList.getName());
        return insertItem(taskList, values, TaskListContentProvider.CONTENT_URI_TASK_LISTS, TASK_LIST_CONVERTER);
    }

    public Task insert(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.TASK_NAME, task.getName());
        return insertItem(task, values, TaskListContentProvider.CONTENT_URI_TASKS, TASK_CONVERTER);
    }

    public void insertRelation(Long taskListId, Long taskId) {
        Uri uri = TaskListContentProvider.CONTENT_URI_RELATION_LAST_ORDER;
        uri = ContentUris.withAppendedId(uri, taskListId);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int order = 0;
        if(cursor.moveToNext()) {
            order = cursor.getInt(cursor.getColumnIndex(TasksRelationTable.ORDER)) + 1;
        }
        ContentValues values = new ContentValues();
        values.put(TasksRelationTable.LIST_ID, taskListId);
        values.put(TasksRelationTable.TASK_ID, taskId);
        values.put(TasksRelationTable.ORDER, order);
        context.getContentResolver().insert(TaskListContentProvider.CONTENT_URI_RELATION, values);
    }

    public void updateRelationOrder(TaskList taskList, List<Task> tasks) {
        Uri uri = TaskListContentProvider.CONTENT_URI_RELATION;
        for (int i = 0; i < tasks.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(TasksRelationTable.ORDER, i);
            Long taskId = tasks.get(i).getId();
            context.getContentResolver().update(uri, values, TaskListContentProvider.UPDATE_RELATION_TEMPLATE,
                    new String[]{taskList.getId().toString(), taskId.toString()});
        }
    }

    private <T> T insertItem(T item, ContentValues values, Uri uri, Converter<T> converter) {
        uri = context.getContentResolver().insert(uri, values);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        List<T> list = converter.convert(cursor);
        if (list.isEmpty()) throw new RuntimeException("An error ocurred during the insert and can't vave de object");
        return Iterables.getFirst(list, item);
    }

    public void deleteRelation(TaskList list, Task task) {
        context.getContentResolver().delete(TaskListContentProvider.CONTENT_URI_RELATION,
                TaskListContentProvider.DELETE_RELATION_TEMPLATE,
                new String[]{list.getId().toString(), task.getId().toString()});
    }

    public void deleteTaskList(TaskList list) {
        context.getContentResolver().delete(TaskListContentProvider.CONTENT_URI_TASK_LISTS,
                TaskListContentProvider.DELETE_TASK_LIST_TEMPLATE,
                new String[]{list.getId().toString()});
    }

    public void cleanTasks() {
        context.getContentResolver().delete(TaskListContentProvider.CONTENT_URI_TASKS,
                TaskListContentProvider.DELETE_UNUSED_TASKS_TEMPLATE,null);
    }
}
