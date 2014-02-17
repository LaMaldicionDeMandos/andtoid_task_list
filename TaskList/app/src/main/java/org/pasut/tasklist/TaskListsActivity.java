package org.pasut.tasklist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.pasut.tasklist.dataaccess.TaskListContentProvider;

import java.util.ArrayList;
import java.util.List;

public class TaskListsActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_lists);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.task_list_view);
        Cursor cursor = getContentResolver().query(TaskListContentProvider.CONTENT_URI, null, null, null, null);
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, null, null, 0));
        getListView().setTextFilterEnabled(true);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_new_list) {
            addList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addList() {
//        String newItem = "New List" + lists.size();
//        lists.add(newItem);
//        ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
