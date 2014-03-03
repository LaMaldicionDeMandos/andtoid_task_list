package org.pasut.tasklist.entity;

/**
 * Created by marcelo on 18/02/14.
 */
public class TaskList extends Identifiable{

    public TaskList(String name) {
        super(name);
    }

    public TaskList(Long id, String name) {
        super(id, name);
    }
}
