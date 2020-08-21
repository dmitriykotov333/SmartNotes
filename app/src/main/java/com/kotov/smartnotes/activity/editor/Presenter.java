package com.kotov.smartnotes.activity.editor;

import android.content.Context;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.model.Inbox;
import com.kotov.smartnotes.model.Item;
import com.kotov.smartnotes.model.MapNote;

import java.util.Date;

import io.realm.RealmList;

/**
 * @author dmitriykotov333@gmail.com
 * @since 10.08.2020
 * Presenter Add Notes
 */
class Presenter {

    private View view;
    private Action action;
    private Context context;

    Presenter(View view, Context context) {
        this.view = view;
        this.context = context;
        action = new Action(context);
    }
    void saveNote(String category, String title, String description, String create_date, String update_date, int priority, String password, boolean fixed, RealmList<Item> image) {
        view.showProgress();
        if (title != null || description != null) {
            view.hideProgress();
            action.add(category, new Inbox(title, description, create_date, update_date, priority, password, fixed, image));
            view.onAddSuccess(context.getString(R.string.successful));
        }
        if (title == null && description == null) {
            view.hideProgress();
            view.onAddError(context.getString(R.string.empty));
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

    void deleteNote(String key, String id) {
        view.showProgress();
        action.remove(id);
        view.hideProgress();
        view.onAddSuccess(context.getString(R.string.successful));
    }

    void replaceNote(String key, String id, String title, String description, String create_date, String update_date, int priority, String password, boolean fixed, RealmList<Item> image) {
        view.showProgress();
        if (title != null || description != null) {
            view.hideProgress();
            action.replace(key, id, new Inbox(title, description, create_date, update_date, priority, password, fixed, image));
            view.onAddSuccess(context.getString(R.string.successful));
        }
        if (title == null && description == null) {
            view.hideProgress();
            action.remove(create_date);
            view.onAddError(context.getString(R.string.empty));
        }
    }

    Inbox get(String id) {
        return action.getParameters(id);
    }
}
