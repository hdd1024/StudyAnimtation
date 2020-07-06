package com.example.studyanimtation.view.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.studyanimtation.R;

public class MenuContentLayout extends LinearLayout {
    //最大偏移量
    private float mMaxTranslationX;

    public MenuContentLayout(Context context) {
        this(context, null);
    }

    public MenuContentLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuContentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuContentLayout);
        mMaxTranslationX = typedArray.getDimension(R.styleable.MenuContentLayout_maxTranslationX, 30);
        typedArray.recycle();
    }

    boolean opend;

    public void setTouchY(float fingerY, float slideOffset) {
        //判断是否打开
        opend = slideOffset > 0.8;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            //禁止了按钮的按压效果
            childAt.setPressed(false);
            //判断手指的Y坐标是落在了那个view上，区间值为 view的高度直接
            boolean isHover = opend && fingerY > childAt.getTop() && fingerY < childAt.getBottom();
            if (isHover) {
                //当pressed为true
                childAt.setPressed(true);
            }
            apply(childAt, fingerY, slideOffset);
        }
    }

    private void apply(View childView, float fingerY, float slideOffset) {

        float translationX = 0;

        int conterY = childView.getTop() + childView.getHeight() / 2;

        int conterX = childView.getLeft() + childView.getWidth() / 2;
        //控制中心与手指的距离
        float disance = Math.abs(conterY - fingerY);
        float sclae = disance / getHeight() * 3;

        translationX = translationX - translationX * disance;
        childView.setTranslationX(translationX);
    }

    /**
     * 手指抬起来，调用该方法，触发按钮点击事件
     */
    public void onMotionUp() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.isPressed()) {
                childAt.performClick();
            }
        }

    }
}
