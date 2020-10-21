package com.kotov.smartnotes.utils.drawingview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

import androidx.annotation.ColorInt;

public class DrawingViewImage extends View {
    private static final String TAG = "DrawingView";
    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap mBitmap;
    private Bitmap mOriginBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private boolean mDrawMode;
    private float mX, mY;
    private float mProportion = 0;
    private LinkedList<DrawPath> savePath;
    private DrawPath mLastDrawPath;
    private Matrix matrix;
    private float mPaintBarPenSize;
    private int mPaintBarPenColor;

    public DrawingViewImage(Context c) {
        this(c, null);
    }

    public DrawingViewImage(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public DrawingViewImage(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        init();
    }

    private void init() {
        Log.d(TAG, "init: ");
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mDrawMode = false;
        savePath = new LinkedList<>();
        matrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mBitmap != null) {
            if ((mBitmap.getHeight() > heightSize) && (mBitmap.getHeight() > mBitmap.getWidth())) {
                widthSize = heightSize * mBitmap.getWidth() / mBitmap.getHeight();
            } else if ((mBitmap.getWidth() > widthSize) && (mBitmap.getWidth() > mBitmap.getHeight())) {
                heightSize = widthSize * mBitmap.getHeight() / mBitmap.getWidth();
            } else {
                heightSize = mBitmap.getHeight();
                widthSize = mBitmap.getWidth();
            }
        }
        Log.d(TAG, "onMeasure: heightSize: " + heightSize + " widthSize: " + widthSize);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }
    @SuppressLint("WrongThread")
    public byte[] getImageUri(Context inContext) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        mBitmap.recycle();
        return byteArray;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据图片尺寸缩放图片，同样只考虑了高大于宽的情况
        float proportion = (float) canvas.getHeight() / mBitmap.getHeight();
        if (proportion < 1) {
            mProportion = proportion;
            matrix.reset();
            matrix.postScale(proportion, proportion);
            matrix.postTranslate((canvas.getWidth() - mBitmap.getWidth() * proportion) / 2, 0);
            canvas.drawBitmap(mBitmap, matrix, mBitmapPaint);
        } else {
            mProportion = 0;
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果你的界面有多个模式，你需要有个变量来判断当前是否可draw
        if (!mDrawMode) {
            return false;
        }
        float x;
        float y;
        if (mProportion != 0) {
            x = (event.getX()) / mProportion;
            y = event.getY() / mProportion;
        } else {
            x = event.getX();
            y = event.getY();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // This happens when we undo a path
                if (mLastDrawPath != null) {
                    mPaint.setColor(mPaintBarPenColor);
                    mPaint.setStrokeWidth(mPaintBarPenSize);
                }
                mPath = new Path();
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                mCanvas.drawPath(mPath, mPaint);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                }
                mCanvas.drawPath(mPath, mPaint);
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);
                mLastDrawPath = new DrawPath(mPath, mPaint.getColor(), mPaint.getStrokeWidth());
                savePath.add(mLastDrawPath);
                mPath = null;
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    public void initializePen() {
        mDrawMode = true;
        mPaint = null;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    @Override
    public void setBackgroundColor(int color) {
        mCanvas.drawColor(color);
        super.setBackgroundColor(color);
    }

    /**
     * This method should ONLY be called by clicking paint toolbar(outer class)
     */
    public void setPenSize(float size) {
        mPaintBarPenSize = size;
        mPaint.setStrokeWidth(size);
    }

    public float getPenSize() {
        return mPaint.getStrokeWidth();
    }

    /**
     * This method should ONLY be called by clicking paint toolbar(outer class)
     */
    public void setPenColor(@ColorInt int color) {
        mPaintBarPenColor = color;
        mPaint.setColor(color);
    }

    public
    @ColorInt
    int getPenColor() {
        return mPaint.getColor();
    }

    /**
     * @return 当前画布上的内容
     */
    public Bitmap getImageBitmap() {
        return mBitmap;
    }

    public void loadImage(Bitmap bitmap) {
        Log.d(TAG, "loadImage: ");
        mOriginBitmap = bitmap;
        mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    public void undo() {
        Log.d(TAG, "undo: recall last path");
        if (savePath != null && savePath.size() > 0) {
            // 清空画布
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            loadImage(mOriginBitmap);

            savePath.removeLast();

            // 将路径保存列表中的路径重绘在画布上 遍历绘制
            for (DrawPath dp : savePath) {
                mPaint.setColor(dp.getPaintColor());
                mPaint.setStrokeWidth(dp.getPaintWidth());
                mCanvas.drawPath(dp.path, mPaint);
            }
            invalidate();
        }
    }

    /**
     * 路径对象
     */
    private class DrawPath {
        Path path;
        int paintColor;
        float paintWidth;

        DrawPath(Path path, int paintColor, float paintWidth) {
            this.path = path;
            this.paintColor = paintColor;
            this.paintWidth = paintWidth;
        }

        int getPaintColor() {
            return paintColor;
        }

        float getPaintWidth() {
            return paintWidth;
        }
    }
}
