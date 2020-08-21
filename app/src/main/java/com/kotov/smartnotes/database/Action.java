package com.kotov.smartnotes.database;

import android.content.Context;
import android.os.Build;

import com.kotov.smartnotes.adapter.AdapterList;
import com.kotov.smartnotes.model.Inbox;
import com.kotov.smartnotes.model.Item;
import com.kotov.smartnotes.model.MapNote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
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

    private MapNote experiment = new MapNote();

    public void add(String key, Inbox map) {
        RealmResults<MapNote> newExperiments = mRealm.where(MapNote.class).equalTo("key", key).findAll();
        try {
            mRealm.executeTransaction(realm -> {
                        if (newExperiments.size() != 0) {
                            for (MapNote newExperiment : newExperiments) {
                                if (newExperiment.getKey().equals(key)) {
                                    newExperiment.getNotes().add(map);//.iterator().next().setId(generateId());
                                    realm.insertOrUpdate(newExperiment);
                                    break;
                                }
                            }
                        } else {
                            experiment.setKey(key);
                            experiment.setNotes((new RealmList<>(map)));
                            realm.insertOrUpdate(experiment);
                        }
                        realm.insertOrUpdate(map);
                    }
            );
        } catch (RealmException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void remove(String id) {
        mRealm.executeTransaction(realm1 -> {
            realm1.where(Inbox.class).equalTo("create_date", id).findAll().deleteAllFromRealm();
        });

    }
    public void replace(String key, String id, Inbox inbox) {
        RealmResults<MapNote> categories = mRealm.where(MapNote.class).equalTo("key", key).findAll();
        try {
            mRealm.executeTransaction(realm -> {
                for (MapNote m : categories) {
                    if (m.getKey().equals(key)) {
                        for (Inbox newExperiment : m.getNotes()) {
                            if (newExperiment.getCreate_date().equals(id)) {
                                newExperiment.setTitle(inbox.getTitle());
                                newExperiment.setDescription(inbox.getDescription());
                                newExperiment.setUpdate_date(inbox.getUpdate_date());
                                newExperiment.setPriority(inbox.getPriority());
                                newExperiment.setPassword(inbox.getPassword());
                                newExperiment.setFixNote(inbox.isFixNote());
                                realm.insertOrUpdate(newExperiment);
                                break;
                            }
                        }
                    }
                }
            });
        } catch (RealmException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public Inbox getParameters(String id) {
        return mRealm.where(Inbox.class).equalTo("create_date", id).findFirst();
    }

    public List<Inbox> getNotes(String key) {
        return checkFixed(key, false);
    }

    public List<Inbox> getFixedNotes(String key) {
        return checkFixed(key, true);
    }

    public List<MapNote> getCategory() {
            return mRealm.where(MapNote.class).findAll();
    }
    private List<Inbox> checkFixed(String key, boolean b) {
        if (mRealm.where(MapNote.class).equalTo("key", key).findAll().iterator().hasNext()) {
            if (mRealm.where(MapNote.class).equalTo("key", key).findAll().iterator().next().getNotes().iterator().hasNext()) {
                return mRealm.where(MapNote.class).equalTo("key", key).findAll().iterator().next().getNotes()
                        .where().equalTo("fixNote", b).findAll().sort("update_date", Sort.DESCENDING).sort("priority");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        if (mRealm != null) {
            mRealm.close();
        }
    }
}
