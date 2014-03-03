package org.pasut.tasklist;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.pasut.tasklist.entity.TaskList;

import java.util.List;

public class TaskListsActivity extends Activity {
    private final static String SHARED_PREFERENCES_NAME = "task_list_preferences";
    private final static String SELECTED_TASK_LIST = "selected_task_list";
    private List<TaskList> taskLists;
    private TaskListEntityService service;
    private ArrayAdapter<TaskList> adapter;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences sharePreferences;

    private TaskList selectedTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_lists);

        service = new TaskListEntityService(this);

        sharePreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        selectedTaskList = findSelectedTaskList();


        configureActionBar();
        configureDrawer();
        configureTaskListsView();
        configureTaskView();
    }

    private TaskList findSelectedTaskList() {
        Long id = sharePreferences.getLong(SELECTED_TASK_LIST, -1l);
        return (id > -1l) ? service.findTaskListById(id) : null;
    }

    private void configureTaskView() {
        configureTaskViewPlaceholder();
        configureTaskList();
    }

    private void configureTaskList() {
    }

    private void configureTaskViewPlaceholder() {
        TextView placeholder = (TextView)findViewById(R.id.task_list_placeholder);
        if (selectedTaskList == null) {
            placeholder.setText(R.string.no_task_list_selected);
        } else {
            placeholder.setText(R.string.empty_tasks_view);
        }
    }

    private void configureDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.container);

        // set a custom shadow that overlays the main content when the drawer opens
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

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
                if (selectedTaskList == null) {
                    getActionBar().setTitle(R.string.task_list_view);
                } else {
                    getActionBar().setTitle(selectedTaskList.getName());
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.task_view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawer.setDrawerListener(drawerToggle);
    }

    private void configureActionBar() {
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (selectedTaskList != null) {
            getActionBar().setTitle(selectedTaskList.getName());
        }
    }

    private void configureTaskListsView() {
        configureTaskLists();
        configureTaskListsPlaceholder();
        configureNewTaskListText();
    }

    private void configureTaskListsPlaceholder() {
        TextView placeholder = (TextView)findViewById(R.id.task_lists_placeholder);
        if (taskLists.isEmpty()) {
            placeholder.setText(R.string.empty_task_lists_view);
        } else {
            placeholder.setVisibility(View.GONE);
        }
    }

    private void configureNewTaskListText() {
        final EditText text = (EditText)findViewById(R.id.list_name);
        text.setVisibility( taskLists.isEmpty() ? View.VISIBLE : View.GONE);
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
    }

    private void configureTaskLists() {
        taskLists = service.findAllTaskLists();
        adapter = new ArrayAdapter<TaskList>(this, android.R.layout.simple_list_item_1, taskLists);
        ListView list = (ListView)findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTaskList = (TaskList) parent.getAdapter().getItem(position);
                saveAsSelected(selectedTaskList);
                closeDrawer();
                refreshView();
            }
        });
        list.setAdapter(adapter);
        list.setTextFilterEnabled(true);
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
        if (id == R.id.action_new_list) {
            onNew();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onNew() {
        if (isOpenDrawer()) {
            populateNewTaskList();
        } else {
            populateNewTask();
        }
    }

    private void populateNewTaskList() {
        showNewTaskList();
    }

    private void showNewTaskList() {
        EditText text = (EditText)findViewById(R.id.list_name);
        text.setVisibility(View.VISIBLE);
    }

    private void populateNewTask() {
        if (selectedTaskList == null) {
            openDrawer();
            showNewTaskList();
        }
    }

    public void onNewTask(View view) {
        populateNewTask();
    }

    private void openDrawer() {
        drawer.openDrawer(findViewById(R.id.drawer_view));
    }

    private void closeDrawer() {
        drawer.closeDrawer(findViewById(R.id.drawer_view));
    }

    private boolean isOpenDrawer() {
        return drawer.isDrawerOpen(findViewById(R.id.drawer_view));
    }

    private void refreshView() {
        configureTaskListsPlaceholder();
        configureTaskView();
    }

    private void addList() {
        String newItem = getItemName(R.id.list_name);
        if (newItem == null || newItem.isEmpty()) return;
        cleanTextView(R.id.list_name);
        addToLists(newItem);
        hideView(R.id.list_name);
        closeDrawer();
        refreshView();
    }

    private void hideView(int id) {
        View view = findViewById(id);
        view.setVisibility(View.GONE);
    }

    private void addToLists(String listName) {
        TaskList taskList = new TaskList(listName);
        taskList = service.insertNewTaskList(taskList);
        selectedTaskList = taskList;
        taskLists.add(taskList);
        taskLists = Lists.reverse(taskLists);
        adapter.notifyDataSetChanged();
        saveAsSelected(taskList);
    }

    private void saveAsSelected(TaskList list) {
        SharedPreferences.Editor editor = sharePreferences.edit();
        editor.putLong(SELECTED_TASK_LIST, list.getId());
        editor.commit();
    }

    private void cleanTextView(int id) {
        TextView textView = (TextView)findViewById(id);
        textView.setText("");
    }

    private String getItemName(int id) {
        TextView textView = (TextView)findViewById(id);
        String text = textView.getText().toString();
        return text;
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
