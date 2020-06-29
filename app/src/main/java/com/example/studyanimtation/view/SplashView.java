package com.example.studyanimtation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.example.studyanimtation.R;

public class SplashView extends View {
    private static final String TAG = "SplashView";
    //旋转圆的半径
    private float mRotationRadius = 90F;
    //小圆半径
    private float mCircleRadius = 18F;
    //小圆的颜色列表
    private int[] mCircleColors;
    //旋转时间
    private int mRotationDuration = 800;//ms
    //第二部分动画时长
    private int mSplashDuration = 1200;
    //整体背景颜色
    private int mSplashBgColor = Color.WHITE;
    /**
     * 保留一些参数，会在运行时改变
     **/
    //空心圆初始半径
    private float mHoleRadius = 0F;
    //当前大圆旋转角度
    private float mCurrentRotationAngle;
    //当前大圆半径
    private float mCurrentRotationRadius = mRotationRadius;

    //绘制圆的画笔
    private Paint mPaint = new Paint();
    //绘制背景的画笔
    private Paint mPaintBackground = new Paint();
    //屏幕中心点的位置
    private float mCenterX;
    private float mCenterY;
    //屏幕对角线的一半
    private float mDiagonalDist;

    private ValueAnimator mAnimator;

    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2f;
        mCenterY = h / 2f;
        mDiagonalDist = (float) (Math.sqrt((w * w + h * h)) / 2);
        init();
    }

    public void init() {
        mCircleColors = getContext().getResources().getIntArray(R.array.splash_circle_colors);
        //画笔初始化
        mPaint.setAntiAlias(true);
        mPaintBackground.setAntiAlias(true);
        //边框样式，描边
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(mSplashBgColor);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                splashDisappear();
            }
        }, 3000);
    }

    private SplashState mState;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mState == null) {
            //第一次启动，创建小球绘制
            mState = new CircleState();
        }
        mState.drawState(canvas);
        Log.d(TAG, ">>>>>>>" + mState.getClass().getSimpleName());

    }

    private void drawCircle(final Canvas canvas) {
        //计算平均每个小球的角度
        float cAngle = (float) (Math.PI * 2 / mCircleColors.length);
        for (int i = 0; i < mCircleColors.length; i++) {
            //计算当前帧小球的角度 当前小球角度=个数*平均每个小球角度+当前帧偏移的角度
            float angle = i * cAngle + mCurrentRotationAngle;
            //根据三角函数与圆的关系求出当前函数的x值 x=cos(角度)*r+X的原点坐标;
            int cX = (int) (mCurrentRotationRadius * Math.cos(angle) + mCenterX);
            //当前Y值 y=sin(角度)*R+Y的原点坐标
            int cY = (int) (mCurrentRotationRadius * Math.sin(angle) + mCenterY);
            //当前帧的小球颜色
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(cX, cY, mCircleRadius, mPaint);
        }

    }


    private void drawBackgroud(Canvas canvas) {
        if (mHoleRadius > 0f) {
            float strokeWidth = mDiagonalDist - mHoleRadius;
            mPaintBackground.setStrokeWidth(strokeWidth);
            //求圆的半径
            float radius = mHoleRadius + strokeWidth / 2;
            canvas.drawCircle(mCenterX, mCenterY, radius, mPaintBackground);
        } else {

            canvas.drawColor(mSplashBgColor);
        }
    }

    private void splashDisappear() {
        if (mState != null && mState instanceof CircleState) {
            CircleState circleState = (CircleState) mState;
            circleState.cancel();
            post(new Runnable() {
                @Override
                public void run() {
                    mState = new MergingState();
                    invalidate();
                }
            });
        }
    }

    public abstract class SplashState {

        abstract void drawState(Canvas canvas);

        public void cancel() {
            mAnimator.cancel();
        }

    }

    private class CircleState extends SplashState {
        public CircleState() {
            //动画变化周期，2π也就是360度
            mAnimator = ValueAnimator.ofFloat(0f, (float) (Math.PI * 2));
            mAnimator.setDuration(mRotationDuration);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    //获取每一帧的角度
                    mCurrentRotationAngle = (float) valueAnimator.getAnimatedValue();
                    postInvalidate();
                }
            });
            mAnimator.start();

        }

        @Override
        void drawState(Canvas canvas) {
            drawBackgroud(canvas);
            drawCircle(canvas);
        }
    }

    private class MergingState extends SplashState {

        public MergingState() {
            mAnimator = ValueAnimator.ofFloat(0f, mRotationRadius);
            mAnimator.setDuration(mSplashDuration);
            mAnimator.setInterpolator(new OvershootInterpolator(10f));
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mCurrentRotationRadius = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    new ExpandState();
                }
            });
            mAnimator.reverse();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackgroud(canvas);
            drawCircle(canvas);

        }
    }

    private class ExpandState extends SplashState {

        public ExpandState() {
            //从圆的半径开始，以对角线为结束点，逐渐扩大
            mAnimator = ValueAnimator.ofFloat(mCircleRadius, mDiagonalDist);
            mAnimator.setDuration(1200);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mHoleRadius = (float) valueAnimator.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.start();

        }

        @Override
        void drawState(Canvas canvas) {
            drawBackgroud(canvas);
        }
    }

}
