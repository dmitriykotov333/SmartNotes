package com.kotov.smartnotes.activity.editor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kotov.smartnotes.R;
import com.kotov.smartnotes.activity.main.MainActivity;
import com.kotov.smartnotes.adapter.AdapterImage;
import com.kotov.smartnotes.adapter.draggable.AdapterCheck;
import com.kotov.smartnotes.adapter.draggable.DragItemTouchHelper;
import com.kotov.smartnotes.audiorecord.AudioListAdapter;
import com.kotov.smartnotes.audiorecord.RecordFragment;
import com.kotov.smartnotes.model.Audio;
import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.model.Images;
import com.kotov.smartnotes.model.Note;
import com.kotov.smartnotes.utils.Utils;
import com.kotov.smartnotes.utils.alarm.AlarmReceiver;
import com.kotov.smartnotes.utils.alarm.AlarmUtils;
import com.kotov.smartnotes.utils.alarm.NotificationUtils;
import com.kotov.smartnotes.utils.drawingview.DrawingViewActivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static androidx.constraintlayout.widget.ConstraintSet.VISIBLE;
import static com.kotov.smartnotes.file.Save.saveFile;
import static com.kotov.smartnotes.utils.Utils.PR;
import static com.kotov.smartnotes.utils.Utils.PRIORITY;

public class Notes extends AppCompatActivity implements View, AudioListAdapter.onItemListClick {

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
    private List<Images> listImages = new ArrayList<>();
    private List<Check> checkList = new ArrayList<>();
    private List<Audio> audioArrayList = new ArrayList<>();
    private String key;
    private Integer single_choice_selected;
    private String password = null;
    private Integer fixed = -1;
    private RecyclerView recyclerView;
    private TextView textView;
    private int code;
    private String category;
    private boolean getSelectedItemCount;
    /**
     * CheckBox
     */
    private AdapterCheck adapterCheck;
    private RecyclerView recyclerViewCheck;
    private ItemTouchHelper mItemTouchHelper;
    /**
     * AudioRecorder
     */
    private RecyclerView audioList;
   // private File[] allFiles;
    private AudioListAdapter audioListAdapter;
    private boolean isPlaying = false;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    private MediaPlayer mediaPlayer = null;

    @Override
    public void onClickListener(Audio file, int position) {
        File file1 = new File(file.getDirectory());//fileToPlay = file;
        if (isPlaying) {
            stopAudio();
            playAudio(file1);
        } else {
            playAudio(file1);
        }
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        isPlaying = true;

        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void stopAudio() {
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Play the audio
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

//        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
//                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }

    @SuppressLint("WrongConstant")
    private void initComponentAudio() {
        //String path = Objects.requireNonNull(getExternalFilesDir("/")).getAbsolutePath();
        //File directory = new File(path);
        //allFiles = directory.listFiles();
        audioListAdapter = new AudioListAdapter(audioArrayList, this);
        audioList.setAdapter(audioListAdapter);
        adapterCheck.setOnItemClickListener((view, social, i) -> Toast.makeText(Notes.this, social.getTitle(), Toast.LENGTH_SHORT).show());
        adapterCheck.setOnClickListener(new OnClickListener<Check>() {
            @Override
            public void onItemClick(android.view.View view, Check inbox, int i) {
               // adapterCheck.deleteItem(inbox, i);
                recyclerViewCheck.scrollToPosition(mAdapter.getItemCount());
            }

            @Override
            public void onItemLongClick(android.view.View view, Check inbox, int i) {

            }
        });
    }

    @SuppressLint("WrongConstant")
    private void initComponentCheck() {
        adapterCheck = new AdapterCheck(this, checkList, id);
        recyclerViewCheck.setAdapter(adapterCheck);
        adapterCheck.setOnItemClickListener((view, social, i) -> Toast.makeText(Notes.this, social.getTitle(), Toast.LENGTH_SHORT).show());
        adapterCheck.setOnClickListener(new OnClickListener<Check>() {
            @Override
            public void onItemClick(android.view.View view, Check inbox, int i) {
                adapterCheck.deleteItem(i);
                recyclerViewCheck.scrollToPosition(adapterCheck.getItemCount());
                if (adapterCheck.getItemCount() == 0) {
                    textView.setVisibility(GONE);
                }
            }

            @Override
            public void onItemLongClick(android.view.View view, Check inbox, int i) {

            }
        });
        adapterCheck.setDragListener(viewHolder -> mItemTouchHelper.startDrag(viewHolder));
        mItemTouchHelper = new ItemTouchHelper(new DragItemTouchHelper(adapterCheck));
        mItemTouchHelper.attachToRecyclerView(recyclerViewCheck);
        if (!checkList.isEmpty()) {
            textView.setVisibility(VISIBLE);
        } else {
            textView.setVisibility(GONE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_notes);
        initToolbar();
        initView();
        getExtraIntent(getIntent());
        single_choice_selected = 4;
        if (id != null) {
            title.setText(presenter.get(id).getTitle());
            password = presenter.get(id).getPassword();
            fixed = presenter.get(id).isFixNote();
            listImages = presenter.getAllImages(id);
            checkList = presenter.getAllChecks(id);
            audioArrayList = presenter.getAllAudios(id);
            Toast.makeText(this, "" + audioArrayList.size(), Toast.LENGTH_SHORT).show();
            category = presenter.getCategory(presenter.get(id).getUpdate_date()).getName();
            description.setText(presenter.get(id).getDescription());
            single_choice_selected = presenter.get(id).getPriority();
            date.setText(String.format("Create notes:\n%s\nUpdate notes:\n%s", presenter.get(id).getCreate_date(), presenter.get(id).getUpdate_date()));
            code = Integer.parseInt(id.substring(11).replace(":", ""));
        } else {
            category = "All Notes";
        }
        initRecyclerView();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void initRecyclerView() {
        audioList = findViewById(R.id.audio_list_view);
        audioList.setLayoutManager(new LinearLayoutManager(this));
        audioList.setHasFixedSize(true);
        recyclerViewCheck = findViewById(R.id.recyclerView_check);
        recyclerViewCheck.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCheck.setHasFixedSize(true);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        recyclerView.setHasFixedSize(true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        listImages.clear();
        listImages = presenter.getAllImages(id);
        listImages.addAll(presenter.getAllImagesISNull());
        initComponent(pickedImage);
        pickedImage = null;
        initComponentCheck();
        audioArrayList.clear();
        audioArrayList = presenter.getAllAudios(id);
        audioArrayList.addAll(presenter.getAllAudiosISNull());
        initComponentAudio();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pickedImage = null;
    }

    @Override
    public void onBackPressed() {
        presenter.imagesIsNullNotesId();
        presenter.checksIsNullNotesId();
        presenter.audiosIsNullNotesId();
        startActivity(new Intent(Notes.this, MainActivity.class).putExtra("key", key));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getExtraIntent(Intent intent) {
        if (intent != null) {
            boolean check_close_notification = intent.getBooleanExtra("close", false);
            int close_id_notification = intent.getIntExtra("close_code", -1);
            id = intent.getStringExtra("id");
            key = intent.getStringExtra("key");
            //if (keys == null) {
            //    keys = "All Notes";
            //}
            if (check_close_notification) {
                AlarmUtils.cancelAlarm(getApplicationContext(), new Intent(getApplicationContext(), AlarmReceiver.class).putExtra("close_id", close_id_notification), close_id_notification);
            }
            //key = keys;
        }
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
        presenter = new Presenter(this, getApplicationContext());
        title = findViewById(R.id.notes_title);
        description = findViewById(R.id.notes_description);
        view_priority = findViewById(R.id.view_priority);
        view_priority.setVisibility(GONE);
        date = findViewById(R.id.date);
        textView = findViewById(R.id.add_check);
        textView.setOnClickListener(v -> {
            adapterCheck.addItem(new Check("", -1, getDate(), getDate()), adapterCheck.getItemCount(), id);
            recyclerViewCheck.scrollToPosition(adapterCheck.getItemCount() - 1);
        });
    }


    public byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream rst = new ByteArrayOutputStream();
        try (ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            rst = byteBuffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rst.toByteArray();
    }

    @SuppressLint("WrongConstant")
    private void initComponent(Uri pickedImage) {
        if (pickedImage != null) {
            String time = getDate();
                try (InputStream stream = getContentResolver().openInputStream(pickedImage)) {
                    if (stream != null) {
                        byte[] inputData = getBytes(stream);
                        listImages.add(new Images(inputData, time, time));
                        runOnUiThread(() -> presenter.saveImages(listImages));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        mAdapter = new AdapterImage(this, listImages);
        if (mAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(GONE);
        } else {
            recyclerView.setVisibility(VISIBLE);
            recyclerView.setAdapter(mAdapter);
        }
        mAdapter.setOnClickListener(new OnClickListener<Images>() {
            public void onItemClick(android.view.View view, Images inbox, int i) {
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(i);
                } else {
                    Toast.makeText(Notes.this, "click", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Notes.this, DetailActivity.class);

                    //if (id == null) {
                    //    Bundle bundle = new Bundle();
                    //    bundle.putSerializable("list", (Serializable) listImages);
                    //    intent.putExtras(bundle);
                    //  } else {
                    intent.putExtra("update_date", id);
                    //}
                    intent.putExtra("position", i);
                    startActivity(intent);
                }
            }

            public void onItemLongClick(android.view.View view, Images inbox, int i) {
                enableActionMode(i);
                getSelectedItemCount = true;
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
                onResume();
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
            //realmList.addAll(rst);
            //checkRealmList.addAll(checkList);
            if (id == null) {
                presenter.saveNote(key, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), time, time, single_choice_selected, password, fixed, listImages, checkList, audioArrayList);//checkList);
            } else {
                String date = presenter.get(id).getUpdate_date();
                String dateCreate = presenter.get(id).getCreate_date();
                if (!key.equals(category)) {
                    presenter.deleteNote(/*key,*/ date);
                    presenter.saveNote(key, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), dateCreate, time, single_choice_selected, password, fixed, listImages, checkList, audioArrayList);
                    Toast.makeText(this, category + " " + key, Toast.LENGTH_SHORT).show();
                } else {
                    presenter.replaceNote(category, id, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), time, single_choice_selected, password, fixed, listImages, checkList, audioArrayList);
                    Toast.makeText(this, category + " " + key, Toast.LENGTH_SHORT).show();
                }
            }
            startActivity(new Intent(Notes.this, MainActivity.class).putExtra("key", key));
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
            // startActivity(Utils.shareNote(Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), date.getText().toString(), rst, getApplicationContext()));
        }
        if (menuItem.getItemId() == R.id.action_delete) {
            presenter.deleteNote(/*keys,*/ id);
            startActivity(new Intent(Notes.this, MainActivity.class).putExtra("key", key));
            finish();
        }
        if (menuItem.getItemId() == R.id.action_password) {
            showCustomDialog();

        }
        if (menuItem.getItemId() == R.id.action_category) {
            showCustomDialogCategory();
        }
        if (menuItem.getItemId() == R.id.action_fix_note) {
            if (fixed == -1) {
                fixed = 1;
            } else {
                fixed = -1;
            }
        }
        if (menuItem.getItemId() == R.id.action_checkbox) {
            adapterCheck.addItem(new Check("", -1, getDate(), getDate()), adapterCheck.getItemCount(), id);
            recyclerViewCheck.scrollToPosition(adapterCheck.getItemCount() - 1);
            textView.setVisibility(VISIBLE);
            checkList = adapterCheck.get();
        }
        if (menuItem.getItemId() == R.id.action_alarm) {
            openTimePickerDialog(true);
        }
        if (menuItem.getItemId() == R.id.action_notifaction) {
            NotificationUtils.notification(getApplicationContext(), getIntents());
        }
        if (menuItem.getItemId() == R.id.action_drawing) {
            Intent intent = new Intent(Notes.this, DrawingViewActivity.class);
            startActivityForResult(intent, 111);
        }
        if (menuItem.getItemId() == R.id.action_read_aloud) {
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> intActivities =
                    packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            if (intActivities.size() != 0) {
                // если поддерживается, то обрабатываем нажатие кнопки
                listenToSpeech();
                // подготовим движок TTS, чтобы проговорить выбранное слово
                Intent checkTTSIntent = new Intent();
                // проверяем данные для TTS
                checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                // запускаем намерение - результат получим в методе onActivityResult
                startActivityForResult(checkTTSIntent, 0);
            } else {
                //распознавание не поддерживается, делаем кнопку недоступной и выводим сообщение
                //speechButton.setEnabled(false);
                Toast.makeText(this, "Сожалеем, но ваше устройство не поддерживает распознавание речи!", Toast.LENGTH_LONG).show();
            }
        }
        if (menuItem.getItemId() == R.id.action_read) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String Url = "https://translate.google.com/translate_tts?ie=UTF-8";
                    String pronouce = "&q=" + description.getText().toString().replaceAll(" ", "%20");
                    String language = "&tl=" + "en";
                    String web = "&client=tw-ob";

                    String fullUrl = Url + pronouce + language + web;

                    Uri uri = Uri.parse(fullUrl);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(Notes.this, uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
           /* mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    // TODO Auto-generated method stub

                    if (status == TextToSpeech.SUCCESS) {
                        mTextToSpeech.setLanguage(new Locale("ru", "RU"));
                        mTextToSpeech.isLanguageAvailable(Locale.ENGLISH);
                        mTextToSpeech.isLanguageAvailable(Locale.UK);
                        mTextToSpeech.isLanguageAvailable(Locale.FRANCE);
                        mTextToSpeech.isLanguageAvailable(Locale.GERMAN);
                        mTextToSpeech.isLanguageAvailable(Locale.GERMANY);
                        mTextToSpeech.isLanguageAvailable(new Locale("spa", "ESP"));


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mTextToSpeech.speak(Objects.requireNonNull(description.getText()).toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            mTextToSpeech.speak(Objects.requireNonNull(description.getText()).toString(), TextToSpeech.QUEUE_FLUSH, null);
                        }
                    } else
                        Log.e("error", "Initilization Failed!");
                }
            });*/
        }
        if (menuItem.getItemId() == R.id.action_audio_record) {
            startActivity(new Intent(Notes.this, RecordFragment.class));
            //finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void listenToSpeech() {
        // запускаем намерение для распознавания речи
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, Objects.requireNonNull(getClass().getPackage()).getName());
        //Текст-приглашение
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажи что-нибудь!");
        //set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // задаем число получаемых результатов
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        // начинаем слушать
        startActivityForResult(listenIntent, 999);
    }

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
            AlarmUtils.addAlarm(getApplicationContext(), getIntents(), code, calSet);
        }
    };

    private Intent getIntents() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("title", Objects.requireNonNull(title.getText()).toString());
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

    private Dialog getDialogCategoryOrPassword() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_password);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        dialog.getWindow().setAttributes(layoutParams);
        return dialog;
    }

    private void showCustomDialog() {
        Dialog dialog = getDialogCategoryOrPassword();
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
    }

    private void showCustomDialogCategory() {
        Dialog dialog = getDialogCategoryOrPassword();
        EditText editText = dialog.findViewById(R.id.password);
        if (key.equals("All Notes")) {
            editText.setText("All Notes");
            // editText.setText(category);
        } else {
            editText.setText(key);
        }
        (dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            dialog.dismiss();
            key = category;
        });
        (dialog.findViewById(R.id.bt_save)).setOnClickListener(v -> {
            if (editText.getText().toString().length() == 0) {
                key = category;
            } else {
                key = editText.getText().toString();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 111) {
                String time = getDate();
                listImages.add(new Images(data.getByteArrayExtra("image"), time, time));
                presenter.saveImages(listImages);
                //action drawing
                /*String time = getDate();
                if (id == null) {
                    presenter.saveNote(key, Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(description.getText()).toString(), time, time, single_choice_selected, password, fixed, listImages);
                    listImages.add(new Images(data.getByteArrayExtra("image"), time, time));
                    presenter.saveImages(listImages, time);
                } else {
                    listImages.add(new Images(data.getByteArrayExtra("image"), time, time));
                    presenter.saveImages(listImages, id);
                }*/
                /*Realm.getDefaultInstance().executeTransaction(realm -> {
                    rst.add(new Item(data.getByteArrayExtra("image")));
                    realm.insertOrUpdate(rst);
                });*/
            } else {
                // add images
                pickedImage = data.getData();
            }
            if (requestCode == 999 && resultCode == RESULT_OK) {
                ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                description.setText(Objects.requireNonNull(description.getText()).toString() + " " + Objects.requireNonNull(suggestedWords).iterator().next());
            }

            // код для TTS. Добавим позже
            //returned from TTS data check
            if (requestCode == 0) {
                //we have the data - create a TTS instance

                //intent will take user to TTS download page in Google Play
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);

            }
        }
    }

    private void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.priority));
        builder.setSingleChoiceItems(PRIORITY, 0, (dialogInterface, i) -> single_choice_selected = PR[i]);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) ->
        {
            if (single_choice_selected.equals(Utils.PRIORITY_RED)) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.red_600));
            }
            if (single_choice_selected.equals(Utils.PRIORITY_YELLOW)) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.yellow));
            }
            if (single_choice_selected.equals(Utils.PRIORITY_GREEN)) {
                view_priority.setBackgroundColor(getResources().getColor(R.color.green));
            }
            if (single_choice_selected.equals(Utils.PRIORITY_DEFAULT)) {
                view_priority.setBackgroundColor(0);
                view_priority.setVisibility(GONE);
            }
            view_priority.setVisibility(android.view.View.VISIBLE);
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
        Toast.makeText(this, "" + single_choice_selected, Toast.LENGTH_SHORT).show();
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
