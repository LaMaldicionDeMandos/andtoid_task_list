package org.pasut.tasklist.entity;

/**
 * Created by marcelo on 03/03/14.
 */
public class Task extends Identifiable {
    public Task(String name) {
        super(name);
    }

    public Task(Long id, String name) {
        super(id, name);
    }
}
