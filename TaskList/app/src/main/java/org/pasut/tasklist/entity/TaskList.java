package org.pasut.tasklist.entity;

/**
 * Created by marcelo on 18/02/14.
 */
public class TaskList {
    private final Long id;
    private final String name;

    public TaskList(String name) {
        this(null, name);
    }

    public TaskList(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
