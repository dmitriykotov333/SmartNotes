package com.kotdev.smartnotes.di.main;

import com.kotdev.smartnotes.app.presenter.PresenterCreateNotes;
import com.kotdev.smartnotes.app.presenter.PresenterDetailImage;
import com.kotdev.smartnotes.app.presenter.PresenterDrawing;
import com.kotdev.smartnotes.app.presenter.PresenterNotes;
import com.kotdev.smartnotes.app.view.ContractDetailImage;
import com.kotdev.smartnotes.app.view.ContractDrawing;
import com.kotdev.smartnotes.app.view.ContractNotes;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class MainBindsModule {

    @MainScope
    @Binds
    public abstract ContractNotes.Notes bindPresenterNotes(PresenterNotes presenterNotes);

    @MainScope
    @Binds
    public abstract ContractNotes.CreateNotes bindPresenterCreateNotes(PresenterCreateNotes presenterCreateNotes);

    @MainScope
    @Binds
    public abstract ContractDetailImage.ImageAction bindPresenterDetailImage(PresenterDetailImage presenterDetailImage);

    @MainScope
    @Binds
    public abstract ContractDrawing.Drawing bindPresenterDrawing(PresenterDrawing presenterDrawing);
}
