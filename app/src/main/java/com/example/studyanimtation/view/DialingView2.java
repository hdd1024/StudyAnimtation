package com.example.studyanimtation.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class DialingView2 extends ImageView {
    private String TAG = "DialingView2";
    private Paint mLinePaint;
    private Path mLinPath;
    private int width;
    private float lineWidth = 10f;
    private float lineHeight = 10f;
    private float lineStartX = 10f;
    private float lineStartY = 15f;
    private float lineEndX = 10f;
    private float lineEndY = 10f;


    public DialingView2(Context context) {
        this(context, null);
    }

    public DialingView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialingView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(lineWidth);
        mLinePaint.setColor(Color.WHITE);
        mLinPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = oldw;

        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        Log.d(TAG, "屏幕宽度：" + width);
        lineStartX = 0f;
        lineStartY = h / 2;
        lineEndX = 10;
        lineEndY = h / 2;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mLinPath.moveTo(lineStartX, lineStartY);
        mLinPath.lineTo(lineEndX, lineEndY);
        canvas.drawPath(mLinPath, mLinePaint);
//        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, mLinePaint);
    }

    public void startDialing() {
        Log.d(TAG, "屏幕宽度：" + width);
        final ValueAnimator animator = ValueAnimator.ofFloat(lineWidth + 20);
        animator.setDuration(10000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lineStartX = lineEndX + 10;
                lineEndX = lineEndX + (float) valueAnimator.getAnimatedValue() + 10;
                if (lineEndX > 1080) {
                    lineEndX = 0;
                    mLinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    mLinPath.reset();
                }
                Log.d(TAG, "拨号------" + lineStartX + ">>>>" + lineEndX);
                invalidate();
            }
        });
        animator.start();
    }
}
