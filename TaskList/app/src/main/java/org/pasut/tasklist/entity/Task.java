package org.pasut.tasklist.entity;

import java.io.Serializable;

/**
 * Created by marcelo on 03/03/14.
 */
public class Task extends Identifiable implements Serializable {
    public Task(String name) {
        super(name);
    }

    public Task(Long id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)) return false;
        Task t = (Task)o;
        return (getId() != null && t.getId() != null && getId().equals(t.getId()))
                || getName().equals(t.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
