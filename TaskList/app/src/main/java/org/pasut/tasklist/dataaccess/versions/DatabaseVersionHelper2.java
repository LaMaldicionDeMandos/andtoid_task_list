package org.pasut.tasklist.dataaccess.versions;

import android.database.sqlite.SQLiteDatabase;

import org.pasut.tasklist.dataaccess.TasksRelationTable;

/**
 * Created by marcelo on 08/04/14.
 */
public class DatabaseVersionHelper2 implements DatabaseVersionHelper {
    private final static int DB_VERSION = 2;
    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion) {
      if (oldVersion == DB_VERSION) return;
        db.execSQL("ALTER TABLE " + TasksRelationTable.TABLE_NAME +
                " ADD COLUMN " + TasksRelationTable.ORDER +
                " INTEGER DEFAULT 0");
    }
}
