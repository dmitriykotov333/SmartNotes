package com.kotov.smartnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.RealmList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.kotov.smartnotes.action.Action;
import com.kotov.smartnotes.action.AddNotes;
import com.kotov.smartnotes.action.imageadapter.Item;
import com.kotov.smartnotes.adapter.AdapterList;
import com.kotov.smartnotes.adapter.OnClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class MainActivity extends AppCompatActivity {

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private AdapterList mAdapter;
    private Action action;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        action = new Action(getApplicationContext());
        initToolbar();
        initComponent();
        findViewById(R.id.fab).setOnClickListener(v -> startActivityForResult(new Intent(this, AddNotes.class), 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String id = data.getStringExtra("id");
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("desc");
            String date = data.getStringExtra("date");
            int priority = data.getIntExtra("priority", -1);
            if (requestCode == 1) {
                action.add(new Inbox(title, description, date, "", priority));
                mAdapter.notifyDataSetChanged();
            } else {
                action.replace(id, new Inbox(title, description, "", date, priority));
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Inbox");
        //Tools.setSystemBarColor(this, R.color.red_600);

    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterList(this, action.getNotes());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new OnClickListener<Inbox>() {
            public void onItemClick(View view, Inbox inbox, int i) {
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(i);
                    return;
                }
                Inbox item = mAdapter.getItem(i);
                startActivityForResult(new Intent(MainActivity.this, AddNotes.class).putExtra("id", item.getCreate_date()), 2);
            }

            public void onItemLongClick(View view, Inbox inbox, int i) {
                enableActionMode(i);
            }
        });
        actionModeCallback = new ActionModeCallback();
    }

    public void enableActionMode(int i) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(i);
    }

    private void toggleSelection(int i) {
        mAdapter.toggleSelection(i);
        int selectedItemCount = mAdapter.getSelectedItemCount();
        if (selectedItemCount == 0) {
            actionMode.finish();
            return;
        }
        actionMode.setTitle(String.valueOf(selectedItemCount));
        actionMode.invalidate();
    }

    private class ActionModeCallback implements ActionMode.Callback {
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        private ActionModeCallback() {
        }

        public boolean onCreateActionMode(ActionMode action, Menu menu) {
            Tools.setSystemBarColor(MainActivity.this, R.color.blue_grey_700);
            action.getMenuInflater().inflate(R.menu.menu_delete, menu);

            return true;
        }

        public boolean onActionItemClicked(ActionMode action, MenuItem menuItem) {
            if (menuItem.getItemId() != R.id.action_delete) {
                return false;
            }
            deleteInboxes();
            action.finish();

            return false;
        }

        public void onDestroyActionMode(ActionMode action) {
            mAdapter.clearSelections();
            Tools.setSystemBarColor(MainActivity.this, R.color.colorPrimaryDark);
            actionMode = null;
        }
    }

    public void deleteInboxes() {
        List<Integer> selectedItems = mAdapter.getSelectedItems();
        for (int size = selectedItems.size() - 1; size >= 0; size--) {
            mAdapter.removeData(selectedItems.get(size));
        }
        mAdapter.notifyDataSetChanged();
    }


}
