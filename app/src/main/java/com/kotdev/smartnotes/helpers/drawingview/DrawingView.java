package com.kotdev.smartnotes.helpers.drawingview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


import com.kotdev.smartnotes.R;

import java.io.ByteArrayOutputStream;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DrawingView extends View {

    /**
     * Когда мы касаемся экрана и начинаем двигать пальцем, то используем класс Path, который позволяет рисовать след движения пальца.
     */
    private Path drawPath;

    /**
     * Само рисование происходит с помощью объектов Paint
     */
    private Paint drawPaint;
    private Paint canvasPaint;
    /**
     *  Сразу же установим начальный цвет для рисования.
     */
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private boolean erase = false;

    /**
     * Первая переменная будет отвечать за размер кисти, а вторая будет запоминать текущий размер кисти,
     * чтобы вернуться к нему, если пользователь временно переключился на инструмент Ластик, чтобы стереть рисунок.
     */
    private float brushSize, lastBrushSize;
    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    public DrawingView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    /**
     * Модифицируем метод init() для настройки к рисованию:
     */
    private void init() {

        // Первоначальные настройки для рисования
        // prepare for drawing and setup paint stroke properties
        float brushSize = 10;
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    /**
     * Нам нужно переопределить метод onSizeChanged() суперкласса для отслеживания изменения размеров создаваемого компонента:
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
    }

    /**
     * Также следует реализовать метод onDraw():
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    /**
     * Для отслеживания нажатия и движения пальца нам понадобится метод onTouchEvent():
     *
     * Когда пользователь касается экрана внутри DrawingView (ACTION_DOWN),
     * мы фиксируем координаты пальца для начала рисования.
     * При движении (ACTION_MOVE) мы рисуем линию к следующей точке по всему пути движения пальца.
     * Когда палец отрывается от экрана (ACTION_UP),
     * мы завершаем рисование линии и фиксируем получившийся рисунок,
     * ожидая рисования новой линии из другой точки.
     * Метод invalidate() вызывает метод onDraw(), чтобы изменения стали видимы на экране.
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        // respond to down, move and up events
        switch (event.getAction()) {



            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                return true;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                drawPath.moveTo(touchX, touchY);
                break;
            default:
                return false;
        }
        // redraw
        invalidate();
        return true;
    }

    /**
     * В классе DrawingView добавим новый метод, который устанавливает цвет:
     * @param newColor
     */
    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    /**
     * Добавим новый метод в класс, который будет отвечать за установку размера кисти программно:
     * @param newSize
     */
    public void setBrushSize(float newSize) {
        // update size
        float pixelAmount = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, newSize, getResources()
                        .getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    /**
     * Теперь добавим в приложение возможность стирания рисунка. В классе DrawingView добавим булеву переменную, которая будет служить флагом для определения - рисовать или стирать.
     * @param isErase
     */
    private final Paint eraserPaint = new Paint();
    public void setErase(boolean isErase) {
        // set erase true or false
       erase = isErase;

        if (erase) {
            drawPaint
                   .setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else
            drawPaint.setXfermode(null);
    }

    /**
     * В классе DrawingView добавим новый метод startNew(), который стирает рисунок с поверхности:
     */
    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    public Bitmap getBitmaps() {
        return canvasBitmap;
    }

    @SuppressLint("WrongThread")
    public byte[] getImageUri(Context inContext) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        canvasBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

     public void loadImage(Bitmap bitmap) {
         Log.d(TAG, "loadImage: ");
         canvasBitmap = bitmap;
         canvasBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
         drawCanvas = new Canvas(canvasBitmap);
         invalidate();
          }


    public static void main(String[] args){

    }


}
