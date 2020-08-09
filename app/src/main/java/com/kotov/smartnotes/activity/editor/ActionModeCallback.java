package com.kotov.smartnotes.activity.editor;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.utils.Utils;


import androidx.appcompat.view.ActionMode;

public abstract class ActionModeCallback implements ActionMode.Callback {
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    private Activity activity;
    protected ActionModeCallback(Activity activity) {
        this.activity = activity;
    }

    public boolean onCreateActionMode(ActionMode action, Menu menu) {
        Utils.setSystemBarColor(activity, R.color.blue_grey_700);
        action.getMenuInflater().inflate(R.menu.menu_delete, menu);

        return true;
    }

    public boolean onActionItemClicked(ActionMode action, MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.action_delete) {
            return false;
        }
        deleteInbox();
        action.finish();

        return false;
    }

    public void onDestroyActionMode(ActionMode action) {
        Utils.setSystemBarColor(activity, R.color.colorPrimaryDark);
    }


    public abstract void deleteInbox();


}