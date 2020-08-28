package com.kotov.smartnotes.activity.editor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.adapter.AdapterImage;
import com.kotov.smartnotes.adapter.draggable.AdapterCheck;
import com.kotov.smartnotes.adapter.draggable.DragItemTouchHelper;
import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.model.Item;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.utils.Utils;
import com.kotov.smartnotes.utils.alarm.AlarmReceiver;
import com.kotov.smartnotes.utils.alarm.AlarmUtils;
import com.kotov.smartnotes.utils.alarm.NotificationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmList;

import static android.view.View.GONE;
import static androidx.constraintlayout.widget.ConstraintSet.VISIBLE;
import static com.kotov.smartnotes.file.Save.saveFile;
import static com.kotov.smartnotes.utils.Utils.PR;
import static com.kotov.smartnotes.utils.Utils.PRIORITY;

public class Notes extends AppCompatActivity implements View {

    private com.google.android.material.textfield.TextInputEditText title;
    private com.google.android.material.textfield.TextInputEditText description;
    private String id, keys;
    private android.view.View view_priority;
    private TextView date;
    private AdapterImage mAdapter;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private ProgressDialog progressDialog;
    private Presenter presenter;
    private Uri pickedImage;
    private List<Item> rst = new ArrayList<>();
    private List<Check> checkList = new ArrayList<>();
    private Bitmap bitmap;
    private String key;
    private int single_choice_selected;
    private String password = null;
    private boolean fixed = false;
    private RealmList<Item> realmList = new RealmList<>();
    private RealmList<Check> checkRealmList = new RealmList<>();
    private RecyclerView recyclerView;
    private TextView textView;
    private int code;
    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /*
    List Drag
     */

    private AdapterCheck adapterCheck;
    private RecyclerView recyclerViewCheck;
    private ItemTouchHelper mItemTouchHelper;

    @SuppressLint("WrongConstant")
    private void initComponentCheck() {

        recyclerViewCheck = (RecyclerView) findViewById(R.id.recyclerView_check);
        recyclerViewCheck.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCheck.setHasFixedSize(true);
        adapterCheck = new AdapterCheck(this, checkList);
        recyclerViewCheck.setAdapter(adapterCheck);
        adapterCheck.setOnItemClickListener(new AdapterCheck.OnItemClickListener() {
            @Override
            public void onItemClick(android.view.View view, Check social, int i) {
                Toast.makeText(Notes.this, social.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        adapterCheck.setOnClickListener(new OnClickListener<Check>() {
            @Override
            public void onItemClick(android.view.View view, Check inbox, int i) {
                adapterCheck.deleteItem(i);
                recyclerViewCheck.scrollToPosition(mAdapter.getItemCount());
            }

            @Override
            public void onItemLongClick(android.view.View view, Check inbox, int i) {

            }
        });
        adapterCheck.setDragListener(new AdapterCheck.OnStartDragListener() {
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        });
        mItemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(adapterCheck));
        mItemTouchHelper.attachToRecyclerView(recyclerViewCheck);
        textView = findViewById(R.id.add_check);
        if (!checkList.isEmpty()) {
            textView.setVisibility(VISIBLE);
        } else {
            textView.setVisibility(GONE);
        }
        textView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                adapterCheck.addItem(new Check("", false), adapterCheck.getItemCount());
                recyclerViewCheck.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    private List<Check> getCheck() {
        List<Check> list = new ArrayList<>();
        list.add(new Check("Title 1", false));
        list.add(new Check("Title 2", false));
        list.add(new Check("Title 3", false));
        list.add(new Check("Title 4", false));
        list.add(new Check("Title 5", false));
        return list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);
        presenter = new Presenter(this, getApplicationContext());
        initToolbar();
        initView();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        boolean check_close = intent.getBooleanExtra("close", false);
        int close_id = intent.getIntExtra("close_code", -1);
        keys = intent.getStringExtra("key");
        if (keys == null) {
            keys = Utils.CATEGORY_DEFAULT;
        }
        if (check_close) {
                AlarmUtils.cancelAlarm(getApplicationContext(), new Intent(getApplicationContext(), AlarmReceiver.class).putExtra("close_id", close_id), close_id);
        }
        code = Integer.parseInt(id.substring(11, id.length()).replace(":", ""));
        key = keys;
        single_choice_selected = PR[0];
        if (id != null) {
            title.setText(presenter.get(id).getTitle());
            password = presenter.get(id).getPassword();
            fixed = presenter.get(id).isFixNote();
            rst = presenter.get(id).getImage();
            checkList = presenter.get(id).getChecks();
            description.setText(presenter.get(id).getDescription());
            single_choice_selected = presenter.get(id).getPriority();
            date.setText(String.format("Create notes:\n%s\nUpdate notes:\n%s", presenter.get(id).getCreate_date(), presenter.get(id).getUpdate_date()));
        }
        initComponent(pickedImage);
        initComponentCheck();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
    }


    @Override
    protected void onPause() {
        super.onPause();
        pickedImage = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Utils.setSystemBarColor(Notes.this, R.color.colorPrimaryDark);
    }

    private void initView() {
        title = findViewById(R.id.notes_title);
        description = findViewById(R.id.notes_description);
        view_priority = findViewById(R.id.view_priority);
        view_priority.setVisibility(GONE);
        date = findViewById(R.id.date);
    }

    @SuppressLint("WrongConstant")
    private void initComponent(Uri pickedImage) {
        if (pickedImage != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Realm.getDefaultInstance().executeTransaction(realm -> {
                rst.add(new Item(byteArray));
                realm.insertOrUpdate(rst);
            });
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterImage(this, rst);
        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(GONE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            recyclerView.setAdapter(mAdapter);
        }
        mAdapter.setOnClickListener(new OnClickListener<Item>() {
            public void onItemClick(android.view.View view, Item inbox, int i) {
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(i);
                }
                //Item item = mAdapter.getItem(i);

            }

            public void onItemLongClick(android.view.View view, Item inbox, int i) {
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
                Utils.setSystemBarColor(Notes.this, R.color.colorPrimaryDark);
                actionMode = null;
                mAdapter.clearSelections();
                if (mAdapter.getItemCount() == 0) {
                    recyclerView.setVisibility(GONE);
                }
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @SuppressLint("WrongConstant")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_save) {
            String time = getDate();
            realmList.addAll(rst);
            checkRealmList.addAll(checkList);
            if (id == null) {
                presenter.saveNote(keys, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), time, time, single_choice_selected, password, fixed, realmList, checkRealmList);
            } else {
                String date = presenter.get(id).getCreate_date();
                if (!key.equals(keys)) {
                    presenter.deleteNote(/*key,*/ id);
                    presenter.saveNote(keys, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), date, time, single_choice_selected, password, fixed, realmList, checkRealmList);
                }
                presenter.replaceNote(keys, id, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), date, time, single_choice_selected, password, fixed, realmList, checkRealmList);
            }
            finish();
        }
        if (menuItem.getItemId() == R.id.action_priority) {
            showSingleChoiceDialog();
        }
        if (menuItem.getItemId() == R.id.action_to_txt) {
            saveFile(Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString());
        }
        if (menuItem.getItemId() == R.id.action_photo) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 123);
        }
        if (menuItem.getItemId() == R.id.action_sharing) {
            startActivity(Utils.shareNote(Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), date.getText().toString(), rst, getApplicationContext()));
        }
        if (menuItem.getItemId() == R.id.action_delete) {
            presenter.deleteNote(/*keys,*/ id);
            finish();
        }
        if (menuItem.getItemId() == R.id.action_password) {
            showCustomDialog();
        }
        if (menuItem.getItemId() == R.id.action_category) {
            showCustomDialogCategory();
        }
        if (menuItem.getItemId() == R.id.action_fix_note) {
            if (fixed == false) {
                fixed = true;
            } else {
                fixed = false;
            }
        }
        if (menuItem.getItemId() == R.id.action_checkbox) {
            adapterCheck.addItem(new Check("", false), adapterCheck.getItemCount());
            recyclerViewCheck.scrollToPosition(mAdapter.getItemCount() - 1);
            textView.setVisibility(VISIBLE);
        }
        if (menuItem.getItemId() == R.id.action_alarm) {
            openTimePickerDialog(true);
        }
        if (menuItem.getItemId() == R.id.action_notifaction) {
            NotificationUtils.notification(getApplicationContext(), getIntents());
        }
        return super.onOptionsItemSelected(menuItem);
    }

    // Слушатель выбора времени
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                // Если выбранное время на сегодня прошло,
                // то переносим на завтра
                calSet.add(Calendar.DATE, 1);
            }
            //setAlarm(calSet);

            AlarmUtils.addAlarm(getApplicationContext(), getIntents(), code, calSet);
        }
    };

    private Intent getIntents() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("title", title.getText().toString());
        intent.putExtra("id", id);
        intent.putExtra("close_id", code);
        return intent;
    }
    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("Выберите время");
        timePickerDialog.show();
    }


    public void showCustomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_password);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        EditText editText = dialog.findViewById(R.id.password);
        editText.setText(password);
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            dialog.dismiss();
            password = null;
        });
        (dialog.findViewById(R.id.bt_save)).setOnClickListener(v -> {
            if (editText.getText().toString().length() == 0) {
                password = null;
            } else {
                password = editText.getText().toString();
            }
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public void showCustomDialogCategory() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_password);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        EditText editText = dialog.findViewById(R.id.password);
        if (keys.equals(Utils.CATEGORY_DEFAULT)) {
            editText.setText("");
        } else {
            editText.setText(keys);
        }
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            dialog.dismiss();
            keys = "default";
        });
        (dialog.findViewById(R.id.bt_save)).setOnClickListener(v -> {
            if (editText.getText().toString().length() == 0) {
                keys = "default";
            } else {
                keys = editText.getText().toString();
            }
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            pickedImage = data.getData();
            initComponent(pickedImage);
        }
    }

    private void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.priority));
        builder.setSingleChoiceItems(PRIORITY, 0, (dialogInterface, i) -> single_choice_selected = PR[i]);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
        {
            if (single_choice_selected == Utils.PRIORITY_RED) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.red_600));
            }
            if (single_choice_selected == Utils.PRIORITY_YELLOW) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.yellow));
            }
            if (single_choice_selected == Utils.PRIORITY_GREEN) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.green));
            }
            if (single_choice_selected == Utils.PRIORITY_DEFAULT) {
                view_priority.setBackgroundColor(0);
                view_priority.setVisibility(GONE);
            }
            view_priority.setVisibility(android.view.View.VISIBLE);
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.hide();
    }

    @Override
    public void onAddSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        } else {
            actionMode.setTitle(String.valueOf(selectedItemCount));
            actionMode.invalidate();

        }
    }
}
