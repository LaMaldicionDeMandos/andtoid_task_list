package org.pasut.tasklist.dataaccess;

import android.provider.BaseColumns;

/**
 * Created by marcelo on 03/03/14.
 */
public interface TaskTable extends BaseColumns {
    public final static String TABLE_NAME = "task";

    public final static String TASK_NAME = "name";
}
