package com.kotdev.smartnotes.app.view;

import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.kotdev.smartnotes.MvpPresenter;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.note.Note;

import java.util.List;

public class ContractDetailImage {

    public interface ViewContractImage {
    }

    public interface ImageAction extends MvpPresenter<ViewContractImage> {

        void delete(Image image);

        void redactor(Image image);

    }

}
