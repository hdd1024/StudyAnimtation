package com.example.studyanimtation.view.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MenuBgView extends View {

    private Paint mPaint;
    private Path mPath;
    private Bitmap mBgBitmap;
    private float width;
    private float endX;
    private float endY;

    public MenuBgView(Context context) {
        this(context, null);
    }

    public MenuBgView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
    }

    /**
     * 触摸传入坐标
     *
     * @param fingerY 手指Y的文职
     * @param percent 菜单滑动得比例
     */
    public void setTouchY(float fingerY, float percent) {
        mPath.reset();
        width = getWidth() * percent;
        float height = getHeight();
        //超出部
        float offserY = height / 8;
        float beginX = 0;
        float beginY = -offserY;
        //控制点
        endX = 0;
        endY = height + offserY;
        float controlX = width * percent;
        float controlY = fingerY;
        mPath.lineTo(beginX, beginY);
        mPath.quadTo(controlX, controlY, endX, endY);
        mPath.close();
        invalidate();
    }

    public void setColor(int bgColor) {
        mPaint.setColor(bgColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);

        if (mBgBitmap != null) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawBitmap(mBgBitmap, 0, 0, mPaint);
        }
    }

    public void setColor(Drawable background) {
        if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            mPaint.setColor(colorDrawable.getColor());
        } else if (background instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) background;
            mBgBitmap = bitmapDrawable.getBitmap();
            mPaint.setColor(Color.WHITE);
        }
    }
}
