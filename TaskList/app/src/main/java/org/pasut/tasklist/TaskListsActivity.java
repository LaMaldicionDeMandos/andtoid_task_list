package org.pasut.tasklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.contextualundo.ContextualUndoAdapter;
import com.nhaarman.listviewanimations.widget.DynamicListView;

import org.pasut.tasklist.entity.Task;
import org.pasut.tasklist.entity.TaskList;

import java.util.ArrayList;
import java.util.List;

public class TaskListsActivity extends Activity implements ContextualUndoAdapter.DeleteItemCallback {
    public final static String TASKS = "tasks";

    private final static String SHARED_PREFERENCES_NAME = "task_list_preferences";
    private final static String SELECTED_TASK_LIST = "selected_task_list";

    private final static String DRAWER_TUTORIAL = "drawer_tutorial";
    private final static String NEW_TASK_LIST_TUTORIAL = "new_task_list_tutorial";
    private final static String NEW_TASK_TUTORIAL = "new_task_tutorial";
    private final static String DELETE_TASK_TUTORIAL = "delete_task_tutorial";
    private final static String ORDER_TASK_TUTORIAL = "order_task_tutorial";

    private List<TaskList> taskLists;
    private List<Task> tasks;
    private List<Task> currentTasks;
    private TaskListEntityService service;
    private ArrayAdapter<TaskList> adapter;
    private com.nhaarman.listviewanimations.ArrayAdapter<Task> taskAdapter;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences sharePreferences;

    private TaskList selectedTaskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tasks = getIntent().getParcelableArrayListExtra(TASKS);

        setContentView(R.layout.activity_task_lists);

        service = new TaskListEntityService(this);

        sharePreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        selectedTaskList = findSelectedTaskList();

        configureActionBar();
        configureDrawer();
        configureTaskListsView();
        configureTaskView();
        configureNewTaskText();
        configureBanner();
        launchDrawerTutorial();
    }

    private void launchDrawerTutorial() {
        if (!sharePreferences.contains(DRAWER_TUTORIAL)) {
            new HelpDialog.Builder(this, R.layout.open_drawer_tutorial)
                    .withDismissListener(new OnDismissTutotial(DRAWER_TUTORIAL))
                    .addAnimation(R.id.slider_touch, R.anim.help_slide_right, true)
                    .build().show();
        }
    }

    private void launchNewTaskListTutorial() {
        if (!sharePreferences.contains(NEW_TASK_LIST_TUTORIAL)) {
            new HelpDialog.Builder(this, R.layout.new_task_list_tutorial)
                    .withDismissListener(new OnDismissTutotial(NEW_TASK_LIST_TUTORIAL))
                    .build().show();
        }
    }

    private void launchNewTaskTutorial() {
        if (!sharePreferences.contains(NEW_TASK_TUTORIAL) && sharePreferences.contains(NEW_TASK_LIST_TUTORIAL)) {
            new HelpDialog.Builder(this, R.layout.new_task_tutorial)
                    .withDismissListener(new OnDismissTutotial(NEW_TASK_TUTORIAL))
                    .build().show();
        }
    }

    private void launchDeleteTaskTutorial() {
        if (!sharePreferences.contains(DELETE_TASK_TUTORIAL)) {
            new HelpDialog.Builder(this, R.layout.delete_task_tutorial)
                    .withDismissListener(new OnDismissTutotial(DELETE_TASK_TUTORIAL))
                    .addAnimation(R.id.slider_touch, R.anim.help_slide, true)
                    .build().show();
        }
    }

    private void launchOrderTaskTutorial() {
        if (!sharePreferences.contains(ORDER_TASK_TUTORIAL)) {
            new HelpDialog.Builder(this, R.layout.order_tasks_tutorial)
                    .withDismissListener(new OnDismissTutotial(ORDER_TASK_TUTORIAL))
                    //.addAnimation(R.id.slider_touch, R.anim.help_slide, true)
                    .build().show();
        }
    }

    private void configureBanner() {
        AdView banner = getBanner();
        AdRequest request = new AdRequest.Builder()
                .build();
        banner.loadAd(request);
    }

    private TaskList findSelectedTaskList() {
        Long id = sharePreferences.getLong(SELECTED_TASK_LIST, -1l);
        return (id > -1l) ? service.findTaskListById(id) : null;
    }

    private List<Task> findSelectedTasks() {
        return selectedTaskList == null ? new ArrayList<Task>()
                : service.findTasksByListId(selectedTaskList.getId());
    }

    private void configureTaskView() {
        configureTaskList();
        configureTaskViewPlaceholder();
    }

    private boolean selectedTaskListIsEmpty() {
        return currentTasks == null || currentTasks.isEmpty();
    }

    private void configureNewTaskText() {
        final AutoCompleteTextView text = (AutoCompleteTextView)findViewById(R.id.task_name);
        final ArrayAdapter<Task> textAdapter = new ArrayAdapter<Task>(this, android.R.layout.simple_dropdown_item_1line, tasks);
        text.setAdapter(textAdapter);
        if (tasks.isEmpty() && selectedTaskList != null) {
            showInputText(text, findViewById(R.id.task_list));
        } else {
            //hideInputText(text, findViewById(R.id.task_list));
        }
        text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = textAdapter.getItem(position);
                addTask(task);
                hideInputText(text, findViewById(R.id.task_list));
            }
        });
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    int position = text.getListSelection();
                    Task task = null;
                    if (position != ListView.INVALID_POSITION) {
                        task = textAdapter.getItem(position);
                    } else {
                        task = new Task(text.getText().toString());
                        if (!tasks.contains(task)) {
                            task = service.insert(task);
                            tasks.add(task);
                        } else {
                            task = tasks.get(tasks.indexOf(task));
                        }
                        ArrayAdapter<Task> newAdapter = new ArrayAdapter<Task>(TaskListsActivity.this, android.R.layout.simple_dropdown_item_1line, tasks);
                        text.setAdapter(newAdapter);

                    }
                    addTask(task);
                    handled = true;
                }
                hideInputText(text, findViewById(R.id.task_list));
                return handled;
            }
        });
    }

    private void addTask(Task task) {
        addToTasks(task);
        refreshView();
    }

    private void addToTasks(Task task) {
        service.insertRelation(selectedTaskList.getId(), task.getId());
        currentTasks.add(task);
        ListView list = (ListView)findViewById(R.id.task_list);
        taskAdapter.notifyDataSetChanged();
        if (currentTasks.size()==2) {
            launchDeleteTaskTutorial();
        }
        if (currentTasks.size()==4) {
            launchOrderTaskTutorial();
        }
    }

    private void configureTaskList() {
        currentTasks = findSelectedTasks();
        taskAdapter = new MyAdapter(this, currentTasks);
        DynamicListView list = (DynamicListView)findViewById(R.id.task_list);
        ContextualUndoAdapter adapter = new ContextualUndoAdapter(taskAdapter, R.layout.view_undo, R.id.undo, getResources().getInteger(R.integer.time_to_live_undo),this);
        adapter.setAbsListView(list);
        list.setAdapter(adapter);
        list.setTextFilterEnabled(true);
    }

    private void configureTaskViewPlaceholder() {
        TextView placeholder = (TextView)findViewById(R.id.task_list_placeholder);
        placeholder.setOnClickListener(null);
        if (selectedTaskList == null) {
            showView(R.id.task_list_placeholder);
            placeholder.setText(R.string.no_task_list_selected);
            placeholder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDrawer();
                    populateNewTask();
                }
            });
        } else if (selectedTaskListIsEmpty()) {
            showView(R.id.task_list_placeholder);
            placeholder.setText(R.string.empty_tasks_view);
            placeholder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    populateNewTask();
                }
            });
        } else {
            hideView(R.id.task_list_placeholder);
        }
    }

    private void configureDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.container);

        // set a custom shadow that overlays the main content when the drawer opens
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);
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
                launchNewTaskTutorial();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.task_view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                launchNewTaskListTutorial();
            }
        };
        drawer.setDrawerListener(drawerToggle);
    }

    private void configureActionBar() {
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (selectedTaskList != null) {
            getActionBar().setTitle(selectedTaskList.getName());
        } else {
            getActionBar().setTitle(R.string.app_name);
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
            hideView(R.id.task_lists_placeholder);
        }
    }

    private void showInputText(final TextView text, View objectBelow) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(text.getWindowToken(), InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        text.requestFocus();
        TranslateAnimation animation = (TranslateAnimation)AnimationUtils.loadAnimation(this, R.anim.show_text);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) { text.requestFocus(); }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        text.startAnimation(animation);
        text.setVisibility(View.VISIBLE);
        objectBelow.startAnimation(animation);
    }

    private void hideInputText(final TextView text, View objectBelow) {
        text.clearFocus();
        text.setText("");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
        TranslateAnimation animation = (TranslateAnimation)AnimationUtils.loadAnimation(this, R.anim.hide_text);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) { text.setVisibility(View.GONE); }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        text.startAnimation(animation);
        objectBelow.startAnimation(animation);

    }

    private void configureNewTaskListText() {
        final EditText text = (EditText)findViewById(R.id.list_name);
        if (taskLists.isEmpty()) {
            showInputText(text, findViewById(R.id.task_list));
        } else {
            //hideInputText(text, findViewById(R.id.list));
        }
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addList();
                    hideInputText(text, findViewById(R.id.list));
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

        //No funciona para los drawers a la derecha
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
        // Handle action buttons
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isOpenDrawer()) {
                closeDrawer();
            } else {
                openDrawer();
            }
        }

        if (id == R.id.action_new_list) {
            onNew();
            return true;
        }
        if (id == R.id.action_clean_tasks) {
            onCleanTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCleanTasks() {
        service.cleanTasks();
        tasks = service.findAllTasks();
        configureNewTaskText();
    }

    @Override
    public void onBackPressed() {
        if (isNewTextVisible()) {
            hideVisibleTextInput();
        } else {
            super.onBackPressed();
        }
    }

    private void hideVisibleTextInput() {
        hideIfVisible((TextView)findViewById(R.id.list_name), findViewById(R.id.list));
        hideIfVisible((TextView)findViewById(R.id.task_name), findViewById(R.id.task_list));

    }

    private void hideIfVisible(TextView text, View list) {
        if (View.VISIBLE == text.getVisibility()) {
            hideInputText(text, list);
        }
    }

    private boolean isNewTextVisible() {
        View newListText = findViewById(R.id.list_name);
        View newTastText = findViewById(R.id.task_name);
        return View.VISIBLE == newListText.getVisibility()
                || View.VISIBLE == newTastText.getVisibility();
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
        showInputText(text, findViewById(R.id.list));
    }

    private void populateNewTask() {
        if (selectedTaskList == null) {
            openDrawer();
            showNewTaskList();
        } else {
            showInputText((TextView)findViewById(R.id.task_name), findViewById(R.id.task_list));
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
        configureActionBar();
        configureTaskListsPlaceholder();
        configureTaskViewPlaceholder();
        configureTaskView();
    }

    private void addList() {
        String newItem = getItemName(R.id.list_name);
        if (newItem == null || newItem.isEmpty()) return;
        addToLists(newItem);
        closeDrawer();
        refreshView();
    }

    private void showView(int id) {
        View view = findViewById(id);
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(int id) {
        View view = findViewById(id);
        view.setVisibility(View.INVISIBLE);
    }

    private void addToLists(String listName) {
        TaskList taskList = new TaskList(listName);
        taskList = service.insert(taskList);
        selectedTaskList = taskList;
        taskLists.add(taskList);
        adapter.notifyDataSetChanged();
        saveAsSelected(taskList);
    }

    private void saveAsSelected(TaskList list) {
        SharedPreferences.Editor editor = sharePreferences.edit();
        editor.putLong(SELECTED_TASK_LIST, list.getId());
        editor.commit();
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

    @Override
    public void deleteItem(int position) {
        final Task task = taskAdapter.getItem(position);
        taskAdapter.remove(task);
        taskAdapter.notifyDataSetChanged();
        service.deleteRelation(selectedTaskList, task);
        service.updateRelationOrder(selectedTaskList, currentTasks);
        if (taskAdapter.isEmpty()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TaskListsActivity.this);
            dialogBuilder.setTitle(R.string.empty_tasks_view);
            dialogBuilder.setMessage(String.format(getString(R.string.empty_list_message), selectedTaskList.getName()));
            dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    service.deleteTaskList(selectedTaskList);
                    taskLists.remove(selectedTaskList);
                    ListView listView = (ListView) findViewById(R.id.list);
                    ArrayAdapter<TaskList> listAdapter = new ArrayAdapter<TaskList>(TaskListsActivity.this, android.R.layout.simple_list_item_1, taskLists);
                    listView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();
                    selectedTaskList = null;
                    refreshView();
                }
            });
            dialogBuilder.setNegativeButton(R.string.no, null);
            dialogBuilder.create().show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        AdView banner = getBanner();
        if (banner!=null) {
            banner.resume();
        }
    }

    @Override
    public void onPause() {
        AdView banner = getBanner();
        if (banner!=null) {
            banner.pause();
        }
        super.onPause();
    }

    /** Called before the activity is destroyed. */
    @Override
    public void onDestroy() {
        AdView banner = getBanner();
        if (banner!=null) {
            banner.destroy();
        }
        super.onDestroy();
    }

    private AdView getBanner() {
        return (AdView)findViewById(R.id.adView);
    }

    private class OnDismissTutotial implements DialogInterface.OnDismissListener {
        private final String key;

        public OnDismissTutotial(String key) {
            this.key = key;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            SharedPreferences.Editor editor = sharePreferences.edit();
            editor.putBoolean(key, true);
            editor.commit();
        }
    }

    private class MyAdapter extends com.nhaarman.listviewanimations.ArrayAdapter<Task> {

        private Context mContext;

        public MyAdapter(Context context, List<Task> items) {
            super(items);
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public void swapItems(int positionOne, int positionTwo) {
            super.swapItems(positionOne, positionTwo);
            service.updateRelationOrder(selectedTaskList, currentTasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) convertView;
            if (tv == null) {
                tv = (TextView) LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            tv.setText(getItem(position).toString());
            return tv;
        }
    }
}
