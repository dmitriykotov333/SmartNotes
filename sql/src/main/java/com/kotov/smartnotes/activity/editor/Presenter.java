package com.kotov.smartnotes.activity.editor;

import android.content.Context;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.database.AudioAction;
import com.kotov.smartnotes.database.CheckAction;
import com.kotov.smartnotes.database.ImageAction;
import com.kotov.smartnotes.model.Audio;
import com.kotov.smartnotes.model.Category;
import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.model.Images;
import com.kotov.smartnotes.model.Note;

import java.util.Date;
import java.util.List;


/**
 * @author dmitriykotov333@gmail.com
 * @since 10.08.2018
 * Presenter Add Notes
 */
public class Presenter {

    private View view;
    private Action action;
    private ImageAction imageAction;
    private CheckAction checkAction;
    private AudioAction audioAction;
    private Context context;

    public Presenter(View view, Context context) {
        this.view = view;
        this.context = context;
        action = new Action(context);
        imageAction = new ImageAction(context);
        checkAction = new CheckAction(context);
        audioAction = new AudioAction(context);
    }

    void saveNote(String category, String title, String description, String create_date, String update_date, Integer priority, String password, Integer fixed,
                  List<Images> images, List<Check> checks, List<Audio> audios) {
        view.showProgress();
        if (title != null || description != null) {
            view.hideProgress();
            action.add(category, new Note(title, description, create_date, update_date, priority, password, fixed), images, imageAction, checks, checkAction, audios, audioAction);
            view.onAddSuccess(context.getString(R.string.successful));
        }
        if (title == null && description == null) {
            view.hideProgress();
            view.onAddError(context.getString(R.string.empty));
        }
    }

    void saveImages(List<Images> images) {

        view.showProgress();
        if (images != null) {
            view.hideProgress();
            imageAction.addImage(images);
            view.onAddSuccess(context.getString(R.string.successful));
        }
    }

    void saveOneImage(Images image, String date) {
        view.showProgress();
        if (image != null) {
            view.hideProgress();
            imageAction.addOneImage(image, date, action);
            view.onAddSuccess(context.getString(R.string.successful));
        }
    }

    void saveCheckbox(List<Check> checks) {
        view.showProgress();
        if (checks != null) {
            view.hideProgress();
            //checkAction.addCheck(checks);
            view.onAddSuccess(context.getString(R.string.successful));
        }
    }

    void saveOneCheckbox(Check check, String date) {
        view.showProgress();
        if (check != null) {
            view.hideProgress();
            //checkAction.addOneCheck(check, date, action);
            view.onAddSuccess(context.getString(R.string.successful));
        }
    }

    void deleteNote(String id) {
        view.showProgress();
        action.remove(id);
        view.hideProgress();
        view.onAddSuccess(context.getString(R.string.successful));
    }

    void imagesIsNullNotesId() {
        imageAction.deleteImageNullNotesId();
        view.onAddError(context.getString(R.string.empty));
    }

    void checksIsNullNotesId() {
        checkAction.deleteCheckNullNotesId();
        view.onAddError(context.getString(R.string.empty));
    }
    void audiosIsNullNotesId() {
        audioAction.deleteAudioNullNotesId();
        view.onAddError(context.getString(R.string.empty));
    }

    void replaceNote(String key, String date, String title, String description, String update_date, Integer priority, String password, Integer fixed,
                     List<Images> list, List<Check> checks, List<Audio> audios) {
        view.showProgress();
        if (title != null || description != null) {
            view.hideProgress();
            action.replaceNote(key, date, new Note(title, description, update_date, priority, password, fixed), list, checks, audios, imageAction, checkAction, audioAction);
            view.onAddSuccess(context.getString(R.string.successful));
        }
    }

    public void deleteImage(String date) {
        imageAction.deleteImage(date);
    }

    public void deleteCheck(String date) {
        checkAction.deleteCheck(date);
    }

    public Images getImg(String date) {
        return imageAction.getImg(date);
    }

    public Note get(String id) {
        return action.getNote(id);
    }

    public List<Images> getAllImages(String date) {
        return imageAction.getAllImagesNotesId(date, action);
    }

    public List<Check> getAllChecks(String date) {
        return checkAction.getAllCheckNotesId(date, action);
    }
    public List<Audio> getAllAudios(String date) {
        return audioAction.getAllAudioNotesId(date, action);
    }

    public List<Images> getAllImagesISNull() {
        return imageAction.getAllImages();
    }

    public List<Check> getAllChecksISNull() {
        return checkAction.getAllChecks();
    }
    public List<Audio> getAllAudiosISNull() {
        return audioAction.getAllAudios();
    }

    public Category getCategory(String date) {
        return action.getCategory(date);
    }
}
