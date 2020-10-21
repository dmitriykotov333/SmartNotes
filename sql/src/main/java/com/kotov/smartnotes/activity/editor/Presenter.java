package com.kotov.smartnotes.activity.editor;

import android.content.Context;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.model.Category;
import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.model.Images;
import com.kotov.smartnotes.model.Note;

import java.util.Date;
import java.util.List;


/**
 * @author dmitriykotov333@gmail.com
 * @since 10.08.2020
 * Presenter Add Notes
 */
public class Presenter {

    private View view;
    private Action action;
    private Context context;

    public Presenter(View view, Context context) {
        this.view = view;
        this.context = context;
        action = new Action(context);
    }

    void saveNote(String category, String title, String description, String create_date, String update_date, Integer priority, String password, Integer fixed) {
        view.showProgress();
        if (title != null || description != null) {
            view.hideProgress();
            action.add(category, new Note(title, description, create_date, update_date, priority, password, fixed));
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
            for (Images image : images) {
                action.addImage(image);
            }
            view.onAddSuccess(context.getString(R.string.successful));
        }
    }

    void saveCheckbox(List<Check> checks) {
        view.showProgress();
        if (checks != null) {
            view.hideProgress();
            for (Check check : checks) {
                action.addCheckbox(check);
            }
            view.onAddSuccess(context.getString(R.string.successful));
        }
    }
    /*void saveNote(String category, String title, String description, String create_date, int priority, String password, boolean fixed) {
        view.showProgress();
        if (title != null || description != null) {
            view.hideProgress();
            action.add(category, new Inbox(title, description, create_date, create_date, priority, password, fixed));
            view.onAddSuccess(context.getString(R.string.successful));
        }
        if (title == null && description == null) {
            view.hideProgress();
            view.onAddError(context.getString(R.string.empty));
        }
    }*/


    void deleteNote(String id) {
        view.showProgress();
        action.remove(id);
        view.hideProgress();
        view.onAddSuccess(context.getString(R.string.successful));
    }

    void replaceNote(String key, String date, String title, String description, String update_date, Integer priority, String password, Integer fixed) {
        view.showProgress();
        if (title != null || description != null) {
            view.hideProgress();
            action.replaceNote(key, date, new Note(title, description, update_date, priority, password, fixed));
            view.onAddSuccess(context.getString(R.string.successful));
        }
        /*if (title == null && description == null) {
            view.hideProgress();
            action.remove(create_date);
            view.onAddError(context.getString(R.string.empty));
        }*/
    }

    public Note get(String id) {
        return action.getNote(id);
    }
    public Category getCategory(String date) {
        return action.getCategory(date);
    }
}
