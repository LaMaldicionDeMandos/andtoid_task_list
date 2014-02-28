package org.pasut.tasklist;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.pasut.tasklist.entity.TaskList;

import java.util.List;

public class TaskListsActivity extends Activity {
    private List<TaskList> taskLists;
    private TaskListEntityService service;
    private ArrayAdapter<TaskList> adapter;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_lists);

        service = new TaskListEntityService(this);

        taskLists = service.findAllTaskLists();
        adapter = new ArrayAdapter<TaskList>(this, android.R.layout.simple_list_item_1, taskLists);
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setTextFilterEnabled(true);

        final EditText text = (EditText)findViewById(R.id.list_name);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addList();
                    text.setVisibility(View.GONE);
                    handled = true;
                }
                return handled;
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.container);

        // set a custom shadow that overlays the main content when the drawer opens
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawer,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.task_list_view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.task_view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawer.setDrawerListener(drawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        int id = item.getItemId();
        if (id == R.id.action_new_list && drawer.isDrawerOpen(findViewById(R.id.drawer_view))) {
            EditText text = (EditText)findViewById(R.id.list_name);
            text.setVisibility(View.VISIBLE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addList() {
        TextView text = (TextView)findViewById(R.id.list_name);
        String newItem = text.getText().toString();
        if (newItem == null || newItem.isEmpty()) return;
        TaskList taskList = new TaskList(newItem);
        taskList = service.insertNewTaskList(taskList);
        taskLists.add(taskList);
        adapter.notifyDataSetChanged();
        text.setVisibility(View.INVISIBLE);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
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
