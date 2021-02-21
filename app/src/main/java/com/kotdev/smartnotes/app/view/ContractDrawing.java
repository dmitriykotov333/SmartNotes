package com.kotdev.smartnotes.app.view;

import com.kotdev.smartnotes.MvpPresenter;
import com.kotdev.smartnotes.room.image.Image;

public class ContractDrawing {

    public interface ViewContractDrawing {

    }

    public interface Drawing extends MvpPresenter<ViewContractDrawing> {

        void save(Image image);

    }


}
