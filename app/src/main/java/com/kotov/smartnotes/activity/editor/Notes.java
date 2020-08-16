package com.kotov.smartnotes.activity.editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.adapter.AdapterImage;
import com.kotov.smartnotes.model.Item;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;

import static com.kotov.smartnotes.file.Save.saveFile;
import static com.kotov.smartnotes.utils.Utils.PR;
import static com.kotov.smartnotes.utils.Utils.PRIORITY;

public class Notes extends AppCompatActivity implements View {

    private com.google.android.material.textfield.TextInputEditText title;
    private com.google.android.material.textfield.TextInputEditText description;
    private String id;
    private android.view.View view_priority;
    private TextView date;
    private AdapterImage mAdapter;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    private ProgressDialog progressDialog;
    private Presenter presenter;
    private Uri pickedImage;
    private List<Item> rst = new ArrayList<>();
    private Bitmap bitmap;
    private int single_choice_selected;

    private String CURRENT_DATE = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    private String CURRENT_TIME = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);
        presenter = new Presenter(this, getApplicationContext());
        initToolbar();
        initView();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        single_choice_selected = PR[0];
        if (id != null) {
            title.setText(presenter.get(id).getTitle());
            //description.setText(getSpannable(presenter.get(id).getDescription(), presenter.get(id).getImage()));
            description.setText(presenter.get(id).getDescription());
            single_choice_selected = presenter.get(id).getPriority();
            date.setText(String.format("Create notes:\n%s\nUpdate notes:\n%s", presenter.get(id).getCreate_date(), presenter.get(id).getUpdate_date()));
            rst = presenter.get(id).getImage();
            initComponent(pickedImage);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
    }

    /*private SpannableStringBuilder getSpannable(String string, List<Item> rst) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(string);
        Drawable drawable;
        for (int i = 0; i < rst.size(); i++) {
            Bitmap smiley = BitmapFactory.decodeByteArray(rst.get(i).getImage(), 0, rst.get(i).getImage().length);
            drawable = new BitmapDrawable(getResources(), smiley);
            drawable.setBounds(0, 0, 400, 400);

            String newStr = drawable.toString() + "\n";
            //ssb.append(newStr);
            ssb.setSpan(new ImageSpan(drawable),
                    ssb.length() - newStr.length(),
                    ssb.length() - "\n".length() ,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return ssb;
    }*/

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
        date = findViewById(R.id.date);
    }

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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterImage(this, rst);
        recyclerView.setAdapter(mAdapter);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_save) {
            if (id == null) {
                presenter.saveNote(Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), String.format("%s\n%s", CURRENT_DATE, CURRENT_TIME), single_choice_selected);
            } else {
                presenter.replaceNote(id, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), presenter.get(id).getCreate_date(), String.format("%s\n%s", CURRENT_DATE, CURRENT_TIME), single_choice_selected);
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
            presenter.deleteNote(id);
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
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
            }
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
            actionMode = null;
        } else {
            actionMode.setTitle(String.valueOf(selectedItemCount));
            actionMode.invalidate();

        }
    }
}
