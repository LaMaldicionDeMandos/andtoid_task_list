package org.pasut.tasklist.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by marcelo on 03/03/14.
 */
public class Task extends Identifiable implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(getId());
        dest.writeString(getName());
    }

    private Task(Parcel in) {
        this((Long) in.readValue(Long.class.getClassLoader()), in.readString());
    }

    public static Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
