package com.kotov.smartnotes.utils.drawingview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.activity.editor.Presenter;

import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DrawingViewActivity extends AppCompatActivity implements View.OnClickListener, com.kotov.smartnotes.activity.editor.View {


    private DrawingView drawView;
    /**
     * Добавим три переменные для трёх типов размеров:
     */
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton eraseBtn;
    private ImageButton saveBtn;
    /**
     * Теперь займёмся кнопкой для создания нового рисунка. Добавим переменную и инициализируем её.
     */
    private ImageButton newBtn;

    /**
     * Поработаем с кодом выбора цвета. Объявим переменную для текущей кнопки по выбору цвета:
     */
    private ImageButton currPaint;

    /**
     * Поработаем с кнопкой для кисти.
     */
    private ImageButton drawBtn;
    private Presenter presenter;
    String id;
    int position;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_view);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        position = intent.getIntExtra("position", -1);
        presenter = new Presenter(this, getApplicationContext());
        drawView = (DrawingView) findViewById(R.id.drawing);
        drawView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      //  Bitmap bitmap = BitmapFactory.decodeByteArray(presenter.get(id).getImage().get(position).getImage(), 0, presenter.get(id).getImage().get(position).getImage().length);
        //drawView.loadImage(bitmap);
        smallBrush = 11;
        mediumBrush = 17;
        largeBrush = 24;
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
    }

    /**
     * У всех кнопок для выбора цвета мы задействовали атрибут onClick, прописав для него метод paintClicked(). Давайте его напишем:
     * @param view
     */
    // user clicked paint
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
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new View.OnClickListener(){
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
        final Dialog brushDialog = new Dialog(this);
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
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog
                    .setMessage("Рисуем нового кота (текущий рисунок будет стёрт)?");
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
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Сохранение рисунка");
            saveDialog.setMessage("Сохранить рисунок в Галерее?");
            saveDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // save drawing
                            drawView.setDrawingCacheEnabled(true);
                            Intent intent = new Intent();
                            intent.putExtra("image", drawView.getImageUri(getApplicationContext()));
                            setResult(RESULT_OK, intent);
                            finish();
                            /*String imgSaved = MediaStore.Images.Media
                                    .insertImage(getContentResolver(), drawView
                                                    .getDrawingCache(), UUID
                                                    .randomUUID().toString() + ".png",
                                            "drawing");
                            if (imgSaved != null) {
                                Toast savedToast = Toast.makeText(
                                        getApplicationContext(),
                                        "Рисунок сохранён в Галерее!",
                                        Toast.LENGTH_SHORT);
                                savedToast.show();
                            } else {
                                Toast unsavedToast = Toast.makeText(
                                        getApplicationContext(),
                                        "Мяу! Рисунок не получилось сохранить.",
                                        Toast.LENGTH_SHORT);
                                unsavedToast.show();
                            }*/
                            drawView.destroyDrawingCache();
                        }
                    });
            saveDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            saveDialog.show();
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onAddSuccess(String message) {

    }

    @Override
    public void onAddError(String message) {

    }
}
