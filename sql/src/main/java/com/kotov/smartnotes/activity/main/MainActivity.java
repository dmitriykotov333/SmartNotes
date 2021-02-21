package com.kotov.smartnotes.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.activity.editor.ActionModeCallback;
import com.kotov.smartnotes.activity.editor.Notes;
import com.kotov.smartnotes.activity.main.animation.ViewAnimation;
import com.kotov.smartnotes.adapter.AdapterImage;
import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.adapter.AdapterList;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.model.Category;
import com.kotov.smartnotes.model.Note;
import com.kotov.smartnotes.utils.Utils;
import com.kotov.smartnotes.utils.alarm.AlarmReceiver;
import com.kotov.smartnotes.utils.alarm.AlarmUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2018
 */
public class MainActivity extends AppCompatActivity {

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private AdapterList mAdapter, mAdapterFixed;
    private Action action;

    private String category = null;
    public RecyclerView recyclerView_fixed;
    private ImageButton bt_toggle_input;
    private LinearLayout bt_toggle_input_lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        action = new Action(getApplicationContext());
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("key") != null) {
            category = intent.getStringExtra("key");
        } else {
            category = "All Notes";
        }
        initToolbar();
        findViewById(R.id.fab).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Notes.class).putExtra("key", category));
            finish();
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        initComponent(category);
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(category);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                int i = 1;
                for (Category m : action.getCategory()) {
                    popupMenu.getMenu().add(Menu.FLAG_APPEND_TO_GROUP, i++, i++, m.getName());
                }
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                       /* if (item.getTitle().equals("All notes")) {
                            category = Utils.CATEGORY_DEFAULT;
                            onResume();
                            return true;
                        }
                        if (item.getTitle().equals("Bookmarks")) {
                            category = Utils.CATEGORY_BOOKMAR$KS;
                            onResume();
                            return true;
                        }*/
                        for (Category m : action.getCategory()) {
                            if (m.getName().equals(item.getTitle().toString())) {
                                category = m.getName();
                                Objects.requireNonNull(getSupportActionBar()).setTitle(category);
                                onResume();
                                return true;
                            }
                        }
                        return false;
                    }

                });
            }
        });
    }

    private void initRecycler(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setHasFixedSize(true);
    }

    private void adapterAction(AdapterList mAdapter, int i) {
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(mAdapter, i);
            return;
        }
        Note item = mAdapter.getItem(i);
        if (item.getPassword().equals("null")) {
            startActivity(new Intent(MainActivity.this, Notes.class).putExtra("key", category).putExtra("id", item.getUpdate_date()));
            finish();
        } else {
            dialog(item);
        }
    }

    private void dialog(Note item) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_password);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        EditText editText = dialog.findViewById(R.id.password);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            dialog.dismiss();
        });
        (dialog.findViewById(R.id.bt_save)).setOnClickListener(v -> {
            if (editText.getText().toString().equals(item.getPassword())) {
                startActivity(new Intent(MainActivity.this, Notes.class).putExtra("key", category).putExtra("id", item.getUpdate_date()));
                //startActivity(new Intent(MainActivity.this, Notes.class).putExtra("key", category).putExtra("id", item.getCreate_date()));
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void initComponent(String key) {
        /**
         * Init expansion panel
         */
        List<Note> list = action.getFixedNotes(key);
        bt_toggle_input_lin = findViewById(R.id.bt_toggle_input_lin);
        if (list == null || list.isEmpty()) {
            bt_toggle_input_lin.setVisibility(View.GONE);
        } else {
            bt_toggle_input_lin.setVisibility(View.VISIBLE);
            bt_toggle_input = findViewById(R.id.bt_toggle_input);
            recyclerView_fixed = findViewById(R.id.recyclerView_fixed);
            recyclerView_fixed.setVisibility(View.GONE);
            initRecycler(recyclerView_fixed);
            bt_toggle_input_lin.setOnClickListener(view -> toggleSectionInput(bt_toggle_input));
            bt_toggle_input.setOnClickListener(view -> toggleSectionInput(bt_toggle_input));
            mAdapterFixed = new AdapterList(this, list);
            recyclerView_fixed.setAdapter(mAdapterFixed);
            mAdapterFixed.setOnClickListener(new OnClickListener<Note>() {
                public void onItemClick(View view, Note inbox, int i) {
                    adapterAction(mAdapterFixed, i);
                }

                public void onItemLongClick(View view, Note inbox, int i) {
                    enableActionMode(mAdapterFixed, i);
                }
            });
        }
        /**
         * Init main recyclerview notes
         */
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        initRecycler(recyclerView);
        mAdapter = new AdapterList(this, action.getNotes(key));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new OnClickListener<Note>() {
            public void onItemClick(View view, Note inbox, int i) {
                adapterAction(mAdapter, i);
            }

            public void onItemLongClick(View view, Note inbox, int i) {
                enableActionMode(mAdapter, i);
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
                if (mAdapterFixed != null) {
                    mAdapterFixed.clearSelections();
                    mAdapterFixed.notifyDataSetChanged();
                }
                Utils.setSystemBarColor(MainActivity.this, R.color.colorPrimaryDark);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void deleteInbox() {
                List<Integer> selectedItems = mAdapter.getSelectedItems();
                for (int size = selectedItems.size() - 1; size >= 0; size--) {
                    mAdapter.removeData(selectedItems.get(size));
                }
                if (mAdapterFixed != null) {
                    List<Integer> selectedItemsF = mAdapterFixed.getSelectedItems();
                    for (int size = selectedItemsF.size() - 1; size >= 0; size--) {
                        mAdapterFixed.removeData(selectedItemsF.get(size));
                    }

                    mAdapterFixed.notifyDataSetChanged();
                }
                mAdapter.notifyDataSetChanged();
                onResume();
            }
        };
    }

    public void enableActionMode(AdapterList adapterList, int i) {

        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(adapterList, i);
    }

    private void toggleSelection(AdapterList mAdapter, int i) {
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

    /**
     * Expansion panel input action
     *
     * @param view
     */
    public void toggleSectionInput(View view) {
        if (toggleArrow(view)) {
            ViewAnimation.expand(recyclerView_fixed, new ViewAnimation.AnimListener() {
                public void onFinish() {
                    //Utils.nestedScrollTo(ExpansionPanelBasic.this.nested_scroll_view, ExpansionPanelBasic.this.lyt_expand_input);
                }
            });
        } else {
            ViewAnimation.collapse(recyclerView_fixed);
        }
    }

    /**
     * Expansion panel input action animation arrow
     *
     * @param view
     */
    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0.0f) {
            view.animate().setDuration(200).rotation(180.0f);
            return true;
        }
        view.animate().setDuration(200).rotation(0.0f);
        return false;
    }


}
