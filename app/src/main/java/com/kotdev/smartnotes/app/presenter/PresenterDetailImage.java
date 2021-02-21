package com.kotdev.smartnotes.app.presenter;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kotdev.smartnotes.PresenterBase;
import com.kotdev.smartnotes.app.model.ModelImage;
import com.kotdev.smartnotes.app.model.ModelNotes;
import com.kotdev.smartnotes.app.view.ContractDetailImage;
import com.kotdev.smartnotes.app.view.ContractNotes;
import com.kotdev.smartnotes.di.main.MainScope;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.ui.adapters.ImagesAdapter;
import com.kotdev.smartnotes.ui.adapters.NotesAdapter;
import com.kotdev.smartnotes.ui.adapters.SliderAdapter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@MainScope
public class PresenterDetailImage extends PresenterBase<ContractDetailImage.ViewContractImage> implements ContractDetailImage.ImageAction {

    private SliderAdapter adapter;
    private ModelImage modelImage;
    @Inject
    public PresenterDetailImage(ModelImage modelImage) {
        this.modelImage = modelImage;
    }

    public void setAdapter(SliderAdapter adapter) {
        this.adapter = adapter;
    }

    public SliderAdapter getAdapter() {
        return adapter;
    }

    public Single<List<Image>> getAllImages() {
        return modelImage.getAllImages();
    }

    public Flowable<List<Image>> getAllImagesNotesId(long id) {
        return modelImage.getAllImagesNotesId(id);
    }

    @Override
    public void delete(Image image) {
        modelImage.delete(image);
    }

    @Override
    public void redactor(Image image) {

    }
}
