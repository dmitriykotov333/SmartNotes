package com.kotdev.smartnotes.app.presenter;

import com.kotdev.smartnotes.PresenterBase;
import com.kotdev.smartnotes.app.model.ModelDrawing;
import com.kotdev.smartnotes.app.model.ModelImage;
import com.kotdev.smartnotes.app.view.ContractDrawing;
import com.kotdev.smartnotes.app.view.ContractNotes;
import com.kotdev.smartnotes.di.main.MainScope;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.ui.adapters.SliderAdapter;

import javax.inject.Inject;

@MainScope
public class PresenterDrawing extends PresenterBase<ContractDrawing.ViewContractDrawing> implements ContractDrawing.Drawing {

    private ModelDrawing modelDrawing;

    @Inject
    public PresenterDrawing(ModelDrawing modelDrawing) {
        this.modelDrawing = modelDrawing;
    }

    @Override
    public void save(Image image) {
        modelDrawing.insertWithoutId(image);
    }
}
