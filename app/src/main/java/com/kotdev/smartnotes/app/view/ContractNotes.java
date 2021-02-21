package com.kotdev.smartnotes.app.view;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.kotdev.smartnotes.MvpPresenter;
import com.kotdev.smartnotes.room.category.Category;
import com.kotdev.smartnotes.room.note.Note;

import java.util.List;

public class ContractNotes {

    public interface ViewContractNotes {
    }

    public interface ViewContractCreateNotes {

        void imagesListIsEmpty(int view);

        void imagesListNotEmpty(int view);
    }

    public interface Notes extends MvpPresenter<ViewContractNotes> {

        LiveData<List<Note>> getNotes(long id);

        ItemTouchHelper deleteNotes();

    }

    public interface CreateNotes extends MvpPresenter<ViewContractCreateNotes> {

        void updateNote(Note notes);

        void deleteNotes(Note note);

        void onDestroy();
    }

}
