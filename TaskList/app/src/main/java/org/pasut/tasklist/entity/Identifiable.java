package org.pasut.tasklist.entity;

/**
 * Created by marcelo on 03/03/14.
 */
public class Identifiable {
    private final Long id;
    private final String name;

    public Identifiable(String name) {
        this(null, name);
    }

    public Identifiable(Long id, String name) {
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
