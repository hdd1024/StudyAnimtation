package com.example.studyanimtation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DrawPaintView extends View {
    private final String TAG = "DrawPaintView";
    //画笔
    private Paint mDrawPaint;
    private int mPaintColor = Color.RED;
    private PointF mDrawPoint;
    private PointF mMoveDrawPoint;
    private Path mDrawPath;
    private boolean isSaveData;
    private RectF mDrawRect;
    private Canvas mBufferCanvas;
    private Bitmap buffertBimap;
    private boolean isClearDraw;

    public DrawPaintView(Context context) {
        this(context, null);
    }

    public DrawPaintView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawPaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDrawPaint = new Paint();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setStyle(Paint.Style.STROKE);

        mDrawPath = new Path();
        mDrawPoint = new PointF();
        mMoveDrawPoint = new PointF();

        mDrawRect = new RectF();

        setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buffertBimap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(buffertBimap);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDrawRect.set(left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(buffertBimap, 0f, 0f, mDrawPaint);
    }

    public void drawRestore() {
        isClearDraw = true;
        mBufferCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mDrawPath.reset();
        invalidate();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawLine(Canvas canvas) {
        Log.d(TAG, "画笔开画喽！");
        mDrawPath.quadTo(mDrawPoint.x, mDrawPoint.y, mMoveDrawPoint.x, mMoveDrawPoint.y);
        mBufferCanvas.drawPath(mDrawPath, mDrawPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSaveData = false;
                mDrawPoint.x = event.getX();
                mDrawPoint.y = event.getY();
                mDrawPath.moveTo(mDrawPoint.x, mDrawPoint.y);
                mMoveDrawPoint.x = mDrawPoint.x;
                mMoveDrawPoint.y = mDrawPoint.y;
                drawLine(mBufferCanvas);
                break;
            case MotionEvent.ACTION_MOVE:
                isSaveData = false;
                mMoveDrawPoint.x = event.getX();
                mMoveDrawPoint.y = event.getY();
                drawLine(mBufferCanvas);
                mDrawPoint.x = event.getX();
                mDrawPoint.y = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isSaveData = true;
                invalidate();
                break;
        }
        return true;
    }
}
