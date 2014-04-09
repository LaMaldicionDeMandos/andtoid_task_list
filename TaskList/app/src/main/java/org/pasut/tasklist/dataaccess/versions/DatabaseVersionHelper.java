package org.pasut.tasklist.dataaccess.versions;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by marcelo on 08/04/14.
 */
public interface DatabaseVersionHelper {
    void upgrade(SQLiteDatabase db, int oldVersion);
}
