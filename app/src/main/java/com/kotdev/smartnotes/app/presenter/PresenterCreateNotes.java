package com.kotdev.smartnotes.app.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.kotdev.smartnotes.PresenterBase;
import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.app.model.ModelCheckbox;
import com.kotdev.smartnotes.app.model.ModelCreateNotes;
import com.kotdev.smartnotes.app.model.ModelImage;
import com.kotdev.smartnotes.app.view.ContractNotes;
import com.kotdev.smartnotes.di.main.MainScope;
import com.kotdev.smartnotes.room.category.Category;
import com.kotdev.smartnotes.room.checkbox.Checkbox;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.ui.adapters.ImagesAdapter;
import com.kotdev.smartnotes.ui.adapters.draggable.AdapterCheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.view.View.VISIBLE;

@MainScope
public class PresenterCreateNotes extends PresenterBase<ContractNotes.ViewContractCreateNotes> implements ContractNotes.CreateNotes {

    private final ModelCreateNotes model;
    private final ModelImage modelImage;
    private final ModelCheckbox modelCheckbox;
    private ImagesAdapter adapter;
    private AdapterCheck adapterCheck;

    @Inject
    public PresenterCreateNotes(ModelCreateNotes model, ModelImage modelImage, ModelCheckbox modelCheckbox) {
        this.model = model;
        this.modelImage = modelImage;
        this.modelCheckbox = modelCheckbox;
    }

    private void addImage(Note note) {
        getAllImages().subscribe(images -> {
            for (Image image : images) {
                if (image.notes_images_id == null) {
                    addNotesImagesId(note.id, image.create_date);
                }
            }
        });
    }

    private void addCheckbox(Note note) {
        getAllCheckbox().subscribe(checkboxes -> {
            for (Checkbox checkbox : checkboxes) {
                if (checkbox.notes_checkboxes_id == null) {
                    addNotesCheckboxId(note.id, checkbox.create_date);
                }
            }
        });
    }


    public void insertNote(boolean checkTextUtils, String categoryTitle, Note mNoteFinal, String date, Note selected_note, long ACTION_DELETE, View view) {
        if (checkTextUtils) {
            if (selected_note == null) {
                mNoteFinal.create_date = date;
                mNoteFinal.update_date = date;
                if (getCategoryId(categoryTitle) == null) {
                    Category category = new Category();
                    category.title = categoryTitle;
                    insert(category);
                    mNoteFinal.categoryId = getCategoryId(categoryTitle);
                    insert(mNoteFinal);
                    addImage(mNoteFinal);
                    addCheckbox(mNoteFinal);
                } else {
                    mNoteFinal.categoryId = getCategoryId(categoryTitle);
                    insert(mNoteFinal);
                    addImage(mNoteFinal);
                    addCheckbox(mNoteFinal);
                }
            } else {
                mNoteFinal.update_date = date;
                if (categoryTitle.equals(getCategoryById(mNoteFinal.categoryId))) {
                    mNoteFinal.id = ACTION_DELETE;
                    updateNote(mNoteFinal);
                    addImage(mNoteFinal);
                    addCheckbox(mNoteFinal);
                } else {
                    if (getCategoryId(categoryTitle) == null) {
                        mNoteFinal.id = ACTION_DELETE;
                        Category category = new Category();
                        category.title = categoryTitle;
                        insert(category);
                        mNoteFinal.categoryId = getCategoryId(categoryTitle);
                        update(mNoteFinal.title, mNoteFinal.content, mNoteFinal.update_date, mNoteFinal.priority, mNoteFinal.password, mNoteFinal.categoryId, mNoteFinal.create_date);
                        addImage(mNoteFinal);
                        addCheckbox(mNoteFinal);
                    } else {
                        mNoteFinal.id = ACTION_DELETE;
                        mNoteFinal.categoryId = getCategoryId(categoryTitle);
                        update(mNoteFinal.title, mNoteFinal.content, mNoteFinal.update_date, mNoteFinal.priority, mNoteFinal.password, mNoteFinal.categoryId, mNoteFinal.create_date);
                        addImage(mNoteFinal);
                        addCheckbox(mNoteFinal);
                    }
                }
            }
            Bundle bundle = new Bundle();
            bundle.putLong("id_fragmentTwo_to_FragmentOne", getCategoryId(categoryTitle));
            bundle.putString("id_fragmentTwo_to_FragmentOneString", getCategoryById(mNoteFinal.categoryId));
            Navigation.findNavController(view)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment, bundle);
        } else {
            Snackbar.make(view, "Entered data incorrect", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void setAdapter(ImagesAdapter adapter) {
        this.adapter = adapter;
    }

    public ImagesAdapter getAdapter() {
        return adapter;
    }

    public void setAdapterCheck(AdapterCheck adapterCheck) {
        this.adapterCheck = adapterCheck;
    }

    public AdapterCheck getAdapterCheck() {
        return adapterCheck;
    }

    public void viewIsReady() {
        getView().imagesListNotEmpty(VISIBLE);
    }

    public void insertWithoutId(Checkbox checkbox) {
        modelCheckbox.insertWithoutId(checkbox);
    }

    public void delete(Checkbox checkbox) {
        modelCheckbox.delete(checkbox);
    }

    public void deleteCheckboxNullNotesId() {
        modelCheckbox.deleteCheckboxNullNotesId();
    }

    public Flowable<List<Checkbox>> getAllCheckboxNotesId(long id) {
        return modelCheckbox.getAllCheckboxNotesId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Checkbox>> getAllCheckbox() {
        return modelCheckbox.getAllCheckbox();
    }

    public void updateCheck(int checking, String create_date) {
        modelCheckbox.updateCheck(checking, create_date);
    }

    public void updateCheckTitle(String title, long create_date) {
        modelCheckbox.updateCheckTitle(title, create_date);
    }

    public Checkbox getCheckbox(String date) {
        return modelCheckbox.getCheckbox(date);
    }

    public void addNotesCheckboxId(long id, String update_date) {
        modelCheckbox.addNotesCheckboxId(id, update_date);
    }


    public void insert(Category category) {
        model.insert(category);
    }

    public void insert(Note note) {
        model.insert(note);
    }

    public Integer getCategoryId(String title) {
        return model.getCategoryId(title);
    }

    public String getCategoryById(long id) {
        return model.getCategoryById(id);
    }

    public Flowable<List<Category>> getCategories() {
        return model.getCategories();
    }

    public void share(Activity activity, String title, String category, String content) {
        File path = activity.getExternalFilesDir(title);
        File file = new File(path, title + ".txt");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(String.format("Title: %s\nCategory: %s\nDescription:\n%s", title, category, content).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        activity.startActivity(Intent.createChooser(sharingIntent, "share file with"));
    }

    @Override
    public void updateNote(Note notes) {
        model.update(notes);
    }


    public void update(String title, String content, String update_date, int priority, String password, long id, String date) {
        model.update(title, content, update_date, priority, password, id, date);
    }

    @Override
    public void deleteNotes(Note note) {
        model.delete(note);
    }


    public void insert(Image image) {
        modelImage.insert(image);
    }

    public void insertWithoutId(Image image) {
        modelImage.insertWithoutId(image);
    }

    public void delete(Image image) {
        modelImage.delete(image);
    }

    public void deleteImageNullNotesId() {
        modelImage.deleteImageNullNotesId();
    }


    public Single<List<Image>> getAllImages() {
        return modelImage.getAllImages();
    }

    public Image getImg(String date) {
        return modelImage.getImg(date);
    }

    public void addNotesImagesId(long id, String update_date) {
        modelImage.addNotesImagesId(id, update_date);
    }


    @Override
    public void onDestroy() {
        model.dispose();
    }

    public void voiceInput(Activity activity, ActivityResultLauncher<Intent> action_read_aloud, ActivityResultLauncher<Intent> listenToSpeech) {
        PackageManager packageManager = activity.getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> intActivities =
                packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0) {
            listenToSpeech(listenToSpeech);
            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            action_read_aloud.launch(checkTTSIntent);
        }
    }

    private void listenToSpeech(ActivityResultLauncher<Intent> listenToSpeech) {
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, Objects.requireNonNull(getClass().getPackage()).getName());
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажи что-нибудь!");
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        listenToSpeech.launch(listenIntent);
    }

    public void voiceRead(Activity activity, String text) {
        new Thread(() -> {
            String Url = "https://translate.google.com/translate_tts?ie=UTF-8";
            String pronouce = "&q=" + text.replaceAll(" ", "%20");
            String language = "&tl=" + "en";
            String web = "&client=tw-ob";
            String fullUrl = Url + pronouce + language + web;
            Uri uri = Uri.parse(fullUrl);
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(activity, uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void check(Activity activity, String a, String b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(a)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{b}
                        , 1);
            }
        }
    }
}
