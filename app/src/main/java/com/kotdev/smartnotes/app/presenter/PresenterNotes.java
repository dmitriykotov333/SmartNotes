package com.kotdev.smartnotes.app.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kotdev.smartnotes.PresenterBase;
import com.kotdev.smartnotes.app.model.ModelNotes;
import com.kotdev.smartnotes.app.view.ContractNotes;
import com.kotdev.smartnotes.di.main.MainScope;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.ui.adapters.NotesAdapter;
import com.kotdev.smartnotes.ui.adapters.NotesAdapterFix;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

@MainScope
public class PresenterNotes extends PresenterBase<ContractNotes.ViewContractNotes> implements ContractNotes.Notes {

    private final ModelNotes model;
    private NotesAdapterFix adapterFix;
    private NotesAdapter adapter;

    @Inject
    public PresenterNotes(ModelNotes model) {
        this.model = model;
    }

    public void setAdapter(NotesAdapter adapter) {
        this.adapter = adapter;
    }

    public NotesAdapter getAdapter() {
        return adapter;
    }

    public void setAdapterFix(NotesAdapterFix adapterFix) {
        this.adapterFix = adapterFix;
    }

    public NotesAdapterFix getAdapterFix() {
        return adapterFix;
    }

    @Override
    public LiveData<List<Note>> getNotes(long id) {
        return model.getAllNotes(id);
    }

    public LiveData<List<Note>> getNotesFix(long id) {
        return model.getAllNotesFix(id);
    }

    @Override
    public ItemTouchHelper deleteNotes() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                model.delete(adapter.getNotes().get(viewHolder.getAdapterPosition()));
                adapter.remove(viewHolder.getAdapterPosition());
            }

        });
    }

}
