package org.pasut.tasklist;

import org.pasut.tasklist.entity.Task;
import org.pasut.tasklist.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class PlaceholderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_placeholder);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AsyncTask<Void, Void, List<Task>> asynkTask = new AsyncTask<Void, Void, List<Task>>() {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                TaskListEntityService service = new TaskListEntityService(PlaceholderActivity.this);
                return service.findAllTasks();
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                Intent intent = new Intent(PlaceholderActivity.this, TaskListsActivity.class);
                intent.putExtra(TaskListsActivity.TASKS, tasks.toArray(new Task[0]));
                startActivity(intent);
                finish();
            }
        };
        asynkTask.execute();
    }
}
