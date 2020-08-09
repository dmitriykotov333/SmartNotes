package com.kotov.smartnotes.action;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.kotov.smartnotes.R;
import com.kotov.smartnotes.Tools;
import com.kotov.smartnotes.action.imageadapter.AdapterImage;
import com.kotov.smartnotes.action.imageadapter.Item;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.utils.Util;

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
import static com.kotov.smartnotes.utils.Util.PR;
import static com.kotov.smartnotes.utils.Util.PRIORITY;

public class AddNotes extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText title;
    private com.google.android.material.textfield.TextInputEditText description;
    private String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    private String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    private String id;
    private View view_priority;
    private TextView date;
    private AdapterImage mAdapter;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;


    Realm mRealm;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);
        mRealm = Realm.getDefaultInstance();
        initToolbar();
        initView();

        Action action = new Action(getApplicationContext());
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        single_choice_selected = PR[0];
        if (id != null) {
            title.setText(action.getParameters(id).getTitle());
            description.setText(action.getParameters(id).getDescription());
            single_choice_selected = action.getParameters(id).getPriority();
            date.setText(String.format("Create notes:\n%s\nUpdate notes:\n%s", action.getParameters(id).getCreate_date(), action.getParameters(id).getUpdate_date()));
            rst = action.getParameters(id).getImage();
        }
        rstt.addAll(rst);
        initComponent(pickedImage);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pickedImage = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
       // initComponent(pickedImage);
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
        Tools.setSystemBarColor(AddNotes.this, R.color.colorPrimaryDark);

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
            byteImage.add(byteArray);
            mRealm.executeTransaction(realm -> {
                rst.add(new Item(byteArray));
                realm.insertOrUpdate(rst);
            });
        }
        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterImage(this, rst);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new OnClickListener<Item>() {
            public void onItemClick(View view, Item inbox, int i) {
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(i);
                }
                //Item item = mAdapter.getItem(i);

            }

            public void onItemLongClick(View view, Item inbox, int i) {
                enableActionMode(i);
            }
        });
        actionModeCallback = new ActionModeCallback();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_save) {
            Intent intent = new Intent();
            intent.putExtra("id", id);
            intent.putExtra("title", Objects.requireNonNull(title.getText()).toString());
            intent.putExtra("desc", Objects.requireNonNull(description.getText()).toString());
            intent.putExtra("date", String.format("%s\n%s", currentDate, currentTime));
            intent.putExtra("priority", single_choice_selected);
            setResult(RESULT_OK, intent);
            finish();
        }
        if (menuItem.getItemId() == R.id.action_priority) {
            showSingleChoiceDialog();
        }
        if (menuItem.getItemId() == R.id.action_to_txt) {
            saveFile(Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString());
            /*Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            startActivity(Intent.createChooser(sharingIntent, "share file with"));*/

        }
        if (menuItem.getItemId() == R.id.action_photo) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 123);

        }
        if (menuItem.getItemId() == R.id.action_sharing) {
            ArrayList<Uri> imageUri = new ArrayList<>();
            for (int i = 0; i < rstt.size(); i++) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(rstt.get(i).getImage(), 0, rstt.get(i).getImage().length);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                imageUri.add(Uri.parse(path));
            }
            Intent intent = null;
            if (rstt.size() > 1) {
                intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUri);
            } else if (rstt.size() == 1) {
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, imageUri.get(0));
            }
            Objects.requireNonNull(intent).setType("image/*");
            intent.putExtra(Intent.EXTRA_TEXT, String.format("Title:\n%s\nDescription:\n%s\n%s", Objects.requireNonNull(title.getText()).toString(),
                    Objects.requireNonNull(description.getText()).toString(), date.getText().toString()) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());

            startActivity(Intent.createChooser(intent, "Share with"));


        }
        return super.onOptionsItemSelected(menuItem);
    }






    List<byte[]> byteImage = new ArrayList<>();
    List<Item> rst = new ArrayList<>();
    List<Item> rstt = new ArrayList<>();

    Bitmap bitmap;


    Uri pickedImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            pickedImage = data.getData();
            initComponent(pickedImage);
        }
    }

    public int single_choice_selected;

    private void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Priority");
        builder.setSingleChoiceItems(PRIORITY, 0, (dialogInterface, i) -> single_choice_selected = PR[i]);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
        {
            if (single_choice_selected == Util.PRIORITY_RED) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.red_600));
            }
            if (single_choice_selected == Util.PRIORITY_YELLOW) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.yellow));
            }
            if (single_choice_selected == Util.PRIORITY_GREEN) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.green));
            }
            if (single_choice_selected == Util.PRIORITY_DEFAULT) {
                view_priority.setBackgroundColor(0);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
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
            Tools.setSystemBarColor(AddNotes.this, R.color.blue_grey_700);
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
            Tools.setSystemBarColor(AddNotes.this, R.color.colorPrimaryDark);
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
