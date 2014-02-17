package org.pasut.tasklist.dataaccess;

import android.provider.BaseColumns;

/**
 * Created by cala on 16/02/14.
 */
public interface TaskListTable extends BaseColumns {
    public final static String TABLE_NAME = "task_list";

    public final static String TASK_LIST_NAME = "name";
}
