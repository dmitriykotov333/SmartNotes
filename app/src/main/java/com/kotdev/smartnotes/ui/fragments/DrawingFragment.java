package com.kotdev.smartnotes.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.app.presenter.PresenterDrawing;
import com.kotdev.smartnotes.app.view.ContractDrawing;
import com.kotdev.smartnotes.databinding.FragmentDetailImageBinding;
import com.kotdev.smartnotes.databinding.FragmentDrawingBinding;
import com.kotdev.smartnotes.helpers.drawingview.DrawingView;
import com.kotdev.smartnotes.room.image.Image;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class DrawingFragment extends DaggerFragment implements View.OnClickListener, ContractDrawing.ViewContractDrawing {


    private ImageButton currPaint;
    private DrawingView drawView;
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton eraseBtn;
    private ImageButton saveBtn;

    private ImageButton newBtn;

    private ImageButton drawBtn;
    private View view;

    @Inject
    PresenterDrawing presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drawing, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);

        drawView = (DrawingView) view.findViewById(R.id.drawing);
        drawView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        smallBrush = 11;
        mediumBrush = 17;
        largeBrush = 24;

        eraseBtn = (ImageButton) view.findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        drawBtn = (ImageButton)view.findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        newBtn = (ImageButton) view.findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton) view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        LinearLayout paintLayout = (LinearLayout)view.findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

    }

    public void paintClicked(View view) {
        // use chosen color

        // set erase false
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());

        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            // update ui
            imgView.setImageDrawable(getResources().getDrawable(
                    R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(
                    R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.draw_btn) {

            // draw button clicked
            final Dialog brushDialog = new Dialog(requireContext());
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    // отключаем режим стирания
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton) brushDialog
                    .findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    // отключаем режим стирания
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog
                    .findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    // отключаем режим стирания
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

// выводим диалоговое окно
            brushDialog.show();

        } else if (v.getId() == R.id.erase_btn) {
            // switch to erase - choose size
            final Dialog brushDialog = new Dialog(requireContext());
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton) brushDialog
                    .findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton) brushDialog
                    .findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton) brushDialog
                    .findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        } else if (v.getId() == R.id.new_btn) {
            // new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(requireContext());
            newDialog.setTitle("New drawing");
            newDialog
                    .setMessage("Рисуем нового ?");
            newDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            drawView.startNew();
                            dialog.dismiss();
                        }
                    });
            newDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            newDialog.show();
        } else if (v.getId() == R.id.save_btn) {
            // save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(requireContext());
            saveDialog.setTitle("Сохранение рисунка");
            saveDialog.setMessage("Сохранить рисунок в Галерее?");
            saveDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // save drawing
                            drawView.setDrawingCacheEnabled(true);
                            String time = getDate();
                            Image image = new Image();
                            image.id = Long.parseLong(getDate().replace("-", "").replace(":", "").replace(" ", ""));
                            image.image = drawView.getImageUri(requireContext());
                            image.create_date = time;
                            image.update_date = time;
                            image.notes_images_id = null;
                            presenter.save(image);


                            Bundle bundle = new Bundle();
                            bundle.putParcelable("selected_note", requireArguments().getParcelable("selected_note"));
                            Navigation.findNavController(requireView())
                                    .navigate(R.id.action_drawingFragment_to_create_notes, bundle);
                            drawView.destroyDrawingCache();
                        }
                    });
            saveDialog.setNegativeButton("Cancel",
                    (dialog, which) -> dialog.cancel());
            saveDialog.show();
        }
    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}