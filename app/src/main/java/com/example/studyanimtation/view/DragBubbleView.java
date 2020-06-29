package com.example.studyanimtation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.studyanimtation.R;

public class DragBubbleView extends View {
    private final String TAG = "DragBubbleView";
    //气泡默认状态---静止状态
    private static final int BUBBLE_STATE_DEFAUL = 0;
    //气泡连接状态
    private static final int BUBBLE_STATE_CONNECT = 1;
    //气泡分离状态
    private static final int BUBBLE_STATE_APART = 2;
    //气泡消失状态
    private static final int BUBBLE_STATE_DISMISS = 3;
    //气泡状态标志
    private int mBubbleState = BUBBLE_STATE_DEFAUL;
    //气泡半径
    private float mBubbleRadius = 40;
    //气泡颜色
    private int mBubbleColor = Color.RED;
    //气泡文字颜色
    private int mTextColor = Color.WHITE;
    //不动气泡半径
    private float mStillBubRadius;
    //移动气泡半径
    private float mMoveableBubRadius;
    //不动气泡圆心
    private PointF mStillBubCenter;
    //可动气泡圆心
    private PointF mMoveableBubCenter;
    //气泡画笔
    private Paint mBubblePaint;
    //文字画笔
    private Paint mTextPaint = new Paint();
    //文字内容
    private String mTextStr = "25";
    //气泡文字大小
    private int mTextSize = 28;
    //文字绘制区域
    private Rect mTextRect = new Rect();
    //贝塞尔曲线路径
    private Path mBezierPath;
    //爆炸气泡画笔
    private Paint mBurstPaint = new Paint();
    //气泡爆炸区域
    private Rect mBurstRect = new Rect();
    private Bitmap[] mBurstBitmaps;
    private int[] mBurstBitmapIds = {R.drawable.burst_1, R.drawable.burst_2,
            R.drawable.burst_3, R.drawable.burst_4, R.drawable.burst_5};
    //当前爆炸图片下标
    private int mCurrentBurstBitmapIndex;
    //两气泡圆心距离
    private float mDist;
    //两气泡连接状态最大圆心距离
    private float mMaxDist;
    //手指触摸偏移量
    private final float MOVE_OFFSET = mMaxDist / 4;
    private boolean mIsBurstAnimator;

    public DragBubbleView(Context context) {
        this(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStillBubRadius = mBubbleRadius;
        mMoveableBubRadius = mStillBubRadius;

        mMaxDist = mBubbleRadius * 8;

        //设置气泡画笔
        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setColor(mBubbleColor);
        mBubblePaint.setStyle(Paint.Style.FILL);
        //贝塞尔曲线初始化
        mBezierPath = new Path();

        //设置文字画笔
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        //初始化爆炸图片
        mBurstBitmaps = new Bitmap[mBurstBitmapIds.length];
        for (int i = 0; i < mBurstBitmapIds.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mBurstBitmapIds[i]);
            mBurstBitmaps[i] = bitmap;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mStillBubCenter == null) {
            mStillBubCenter = new PointF(w / 2, h / 2);
        } else {
            mStillBubCenter.set(w / 2, h / 2);
        }

        if (mMoveableBubCenter == null) {
            mMoveableBubCenter = new PointF(w / 2, h / 2);
        } else {
            mMoveableBubCenter.set(w / 2, h / 2);
        }
        mStillBubRadius = BUBBLE_STATE_DEFAUL;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "点击事件");
                if (mBubbleState != BUBBLE_STATE_DISMISS) {
                    mDist = (float) Math.hypot((event.getX() - mStillBubCenter.x),
                            (event.getY() - mStillBubCenter.y));
                    if (mDist < mBubbleRadius + MOVE_OFFSET) {
                        //通过MOVE_OFFSET优化进入链接状态
                        mBubbleState = BUBBLE_STATE_CONNECT;
                        Log.d(TAG, "点击事件2");
                    } else {
                        mBubbleState = BUBBLE_STATE_DEFAUL;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBubbleState != BUBBLE_STATE_DEFAUL) {
                    mMoveableBubCenter.x = event.getX();
                    mMoveableBubCenter.y = event.getY();
                    mDist = (float) Math.hypot((event.getX() - mStillBubCenter.x),
                            (event.getY() - mStillBubCenter.y));
                    if (mBubbleState == BUBBLE_STATE_CONNECT) {
                        if (mDist < mMaxDist - MOVE_OFFSET) {
                            // 减去MOVE_OFFSET是为了让不动气泡半径到一个较小值时就直接消失
                            // 或者说是进入分离状态
                            //设置半径，让小圆逐渐减小
                            mStillBubRadius = mBubbleRadius - mDist / 8;
                        } else {
                            mBubbleState = BUBBLE_STATE_APART;
                        }
                    }
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:

                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    resoverBubbleAnimator();
                } else {
                    if (mBubbleState == BUBBLE_STATE_APART) {
                        if (mDist < mBubbleState * 2) {
                            resoverBubbleAnimator();
                        } else {
                            burstAnimator();
                        }
                    }
                }
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBubble(canvas);
    }

    private void drawBubble(Canvas canvas) {
        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            //画静止状态下的小球
            canvas.drawCircle(mMoveableBubCenter.x, mMoveableBubCenter.y,
                    mMoveableBubRadius, mBubblePaint);
//            mTextPaint.getTextBounds(mTextStr, 0, mTextStr.length(), mTextRect);
//            canvas.drawText(mTextStr, mMoveableBubCenter.x - mTextRect.width() / 2,
//                    mMoveableBubCenter.y + mTextRect.height() / 2, mTextPaint);
        }

        //相连状态下的气泡
        if (mBubbleState == BUBBLE_STATE_CONNECT) {
            //画拖动状态下的小球
            canvas.drawCircle(mStillBubCenter.x, mStillBubCenter.y,
                    mStillBubRadius, mBubblePaint);

            //两个圆的圆心的距离
//            mDist = (float) Math.hypot((mMoveableBubCenter.x - mStillBubCenter.x), (mMoveableBubCenter.y - mStillBubCenter.y));
            //sinTheta=对边:斜边 cosTheta=领边:斜边
            float sinTheta = (mMoveableBubCenter.y - mStillBubCenter.y) / mDist;
            float cosTheta = (mMoveableBubCenter.x - mStillBubCenter.x) / mDist;
            //pointControlX,pointControlY 计算控制点的坐标
            int pointControlX = (int) ((mStillBubCenter.x + mMoveableBubCenter.x) / 2);
            int pointControlY = (int) ((mStillBubCenter.y + mMoveableBubCenter.y) / 2);
            //pointStartX,pointStartY，计算上半弧起点坐标
            float pointStartUpX = mStillBubCenter.x - sinTheta * mStillBubRadius;
            float pointStartUpY = mStillBubCenter.y + cosTheta * mStillBubRadius;
            //计算上半弧的结束点的坐标
            float pointEndUpX = mMoveableBubCenter.x - (sinTheta * mMoveableBubRadius);
            float pointEndUpY = mMoveableBubCenter.y + (cosTheta * mMoveableBubRadius);
            //计算下半弧的起点点的坐标
            float pointStartDownX = mMoveableBubCenter.x + (sinTheta * mMoveableBubRadius);
            float pointStartDownY = mMoveableBubCenter.y - (cosTheta * mMoveableBubRadius);
            //计算下半弧的结束点左边
            float pointEndDownX = mStillBubCenter.x + sinTheta * mStillBubRadius;
            float pointEndDownY = mStillBubCenter.y - cosTheta * mStillBubRadius;
            mBezierPath.reset();
            //画上半弧形
            mBezierPath.moveTo(pointStartUpX, pointStartUpY);
            mBezierPath.quadTo(pointControlX, pointControlY, pointEndUpX, pointEndUpY);
            //画下半弧
            mBezierPath.lineTo(pointStartDownX, pointStartDownY);
            mBezierPath.quadTo(pointControlX, pointControlY, pointEndDownX, pointEndDownY);
            //闭合上下半弧形
            mBezierPath.close();
            canvas.drawPath(mBezierPath, mBubblePaint);
        }
        //画文字，获取文字的宽高，用于计算文字坐标
        mTextPaint.getTextBounds(mTextStr, 0, mTextStr.length(), mTextRect);
        //气泡圆心为基准点，x=圆心x-文字宽度/2， y=圆心+文字高度/2
        canvas.drawText(mTextStr, mMoveableBubCenter.x - mTextRect.width() / 2,
                mMoveableBubCenter.y + mTextRect.height() / 2, mTextPaint);
        //是否开启爆炸动画
        if (mIsBurstAnimator) {
            //根据圆的中心点和半径计算出要显示的爆炸图片的位置
            mBurstRect.set((int) (mMoveableBubCenter.x - mBubbleRadius),
                    (int) (mMoveableBubCenter.y - mBubbleRadius),
                    (int) (mMoveableBubCenter.x + mBubbleRadius),
                    (int) (mMoveableBubCenter.y + mBubbleRadius));
            canvas.drawBitmap(mBurstBitmaps[mCurrentBurstBitmapIndex],
                    null, mBurstRect, mBubblePaint);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void resoverBubbleAnimator() {
        ValueAnimator animator = ValueAnimator.ofObject(new PointFEvaluator(), mMoveableBubCenter, mStillBubCenter);
        animator.setDuration(200);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mMoveableBubCenter = (PointF) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //动画结束后不需要绘制最后一帧了
                mBubbleState = BUBBLE_STATE_DEFAUL;
            }
        });
        animator.start();
    }

    private void burstAnimator() {
        mBubbleState = BUBBLE_STATE_DISMISS;
        mIsBurstAnimator = true;
        ValueAnimator animator = ValueAnimator.ofInt(0, mBurstBitmapIds.length);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //设置当前绘制的爆炸图片index
                mCurrentBurstBitmapIndex = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //修改动画执行标志
                mIsBurstAnimator = false;
            }
        });
        animator.start();
    }
}
