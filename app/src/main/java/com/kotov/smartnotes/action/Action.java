package com.kotov.smartnotes.action;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kotov.smartnotes.Inbox;
import com.kotov.smartnotes.action.imageadapter.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class Action implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Action.class);
    private Realm mRealm;

    public Action(Context context) {
        Realm.init(context);
        mRealm = Realm.getDefaultInstance();
    }

   /* public Inbox put(Inbox task) {
        int index = 0;
        for (Inbox element : getNotes()) {
            if (element != null && element.getPriority() > task.getPriority()) {
                break;
            }
            index++;
        }
        tasks.add(index, task);
        return task;
    }*/

    public void add(Inbox inbox) {
        try {
            mRealm.executeTransaction(realm -> realm.insertOrUpdate(inbox));
        } catch (RealmException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void replace(String id, Inbox inbox) {
        RealmResults<Inbox> newExperiments = mRealm.where(Inbox.class).equalTo("create_date", id).findAll();
        mRealm.executeTransaction(realm -> {
            for (Inbox newExperiment : newExperiments) {
                if (newExperiment.getCreate_date().equals(id)) {
                    newExperiment.setTitle(inbox.getTitle());
                    newExperiment.setDescription(inbox.getDescription());
                    newExperiment.setUpdate_date(inbox.getUpdate_date());
                    newExperiment.setPriority(inbox.getPriority());
                    realm.insertOrUpdate(newExperiment);
                    break;
                }
            }
        });
    }

    public Inbox getParameters(String id) {
        return mRealm.where(Inbox.class).equalTo("create_date", id).findFirst();
    }

    public class CustomComparator implements Comparator<Inbox> {
        @Override
        public int compare(Inbox card1, Inbox card2) {
            Integer priority1 = card1.getPriority();
            Integer priority2 = card2.getPriority();
            return priority2.compareTo(priority1);
        }
    }

    public List<Inbox> getNotes() {
        return mRealm.where(Inbox.class).findAll().sort("priority");
    }


    @Override
    public void close() {
        if (mRealm != null) {
            mRealm.close();
        }
    }
}
