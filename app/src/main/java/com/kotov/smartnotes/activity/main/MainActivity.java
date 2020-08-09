package com.kotov.smartnotes.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.activity.editor.ActionModeCallback;
import com.kotov.smartnotes.activity.editor.Notes;
import com.kotov.smartnotes.adapter.AdapterImage;
import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.adapter.AdapterList;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.model.Inbox;
import com.kotov.smartnotes.utils.Utils;

import java.util.List;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        action = new Action(getApplicationContext());
        initToolbar();
        findViewById(R.id.fab).setOnClickListener(v -> startActivity(new Intent(this, Notes.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
    }

    private void initComponent() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
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
                startActivity(new Intent(MainActivity.this, Notes.class).putExtra("id", item.getCreate_date()));
            }

            public void onItemLongClick(View view, Inbox inbox, int i) {
                enableActionMode(i);
            }
        });
        actionModeCallback = new ActionModeCallback(this) {
            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return super.onPrepareActionMode(actionMode, menu);
            }

            @Override
            public boolean onCreateActionMode(ActionMode action, Menu menu) {
                return super.onCreateActionMode(action, menu);
            }

            @Override
            public boolean onActionItemClicked(ActionMode action, MenuItem menuItem) {
                return super.onActionItemClicked(action, menuItem);
            }

            @Override
            public void onDestroyActionMode(ActionMode action) {
                mAdapter.clearSelections();
            }

            @Override
            public void deleteInbox() {
                List<Integer> selectedItems = mAdapter.getSelectedItems();
                for (int size = selectedItems.size() - 1; size >= 0; size--) {
                    mAdapter.removeData(selectedItems.get(size));
                }
                mAdapter.notifyDataSetChanged();
            }
        };
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
            actionMode = null;
        } else {
            actionMode.setTitle(String.valueOf(selectedItemCount));
            actionMode.invalidate();

        }
    }


}
