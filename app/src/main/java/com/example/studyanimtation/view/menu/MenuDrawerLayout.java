package com.example.studyanimtation.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * 侧滑的抽屉
 */
public class MenuDrawerLayout extends DrawerLayout implements DrawerLayout.DrawerListener {
    private String TAG = MenuContentLayout.class.getSimpleName();
    MenuContentLayout mMenuContentLayout;
    View mContentView;
    private MenuPutLayout mPutLayout;

    public MenuDrawerLayout(@NonNull Context context) {
        this(context, null);
    }

    public MenuDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView instanceof MenuContentLayout) {
                mMenuContentLayout = (MenuContentLayout) childView;
            } else {
                mContentView = childView;
            }
        }
        //异触MenuContentView 在添加MenuPutView
        removeView(mMenuContentLayout);
        mPutLayout = new MenuPutLayout(mMenuContentLayout);
        addView(mPutLayout);
        addDrawerListener(this);
    }

    private float fingerY;
    private float slideOffset;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        fingerY = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            closeDrawers();
            mMenuContentLayout.onMotionUp();
            Log.d(TAG,"抬起事件");
            return super.dispatchTouchEvent(ev);
        }
        //没有打开前不拦截，
        if (slideOffset < 0.8) {
            return super.dispatchTouchEvent(ev);
        } else {
            //等于1
            mPutLayout.setTouchY(fingerY, slideOffset);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mPutLayout.setTouchY(ev.getY(), ev.getX() / ev.getRawX());
        return super.onTouchEvent(ev);
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        this.slideOffset = slideOffset;
        mMenuContentLayout.setTouchY(fingerY, slideOffset);
        //对内容进行偏移
        float contentViewOffsetX = drawerView.getWidth() * slideOffset / 2;
        mContentView.setTranslationX(contentViewOffsetX);
        Log.d(TAG, "偏移量" + slideOffset);
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
