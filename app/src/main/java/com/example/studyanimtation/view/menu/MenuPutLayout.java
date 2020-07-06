package com.example.studyanimtation.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 该类用于吧背景view和抽屉组合在一起
 */
public class MenuPutLayout extends RelativeLayout {

    private MenuBgView mBgView;
    private MenuContentLayout mContentLayout;

    public MenuPutLayout(Context context, MenuContentLayout contentLayout) {
        this(context, null, contentLayout);
    }

    public MenuPutLayout(Context context, AttributeSet attrs, MenuContentLayout contentLayout) {
        this(context, attrs, 0, contentLayout);
    }

    public MenuPutLayout(Context context, AttributeSet attrs, int defStyleAttr, MenuContentLayout contentLayout) {
        super(context, attrs, defStyleAttr);
    }

    public MenuPutLayout(MenuContentLayout contentLayout) {
        this(contentLayout.getContext(), contentLayout);
        mBgView = new MenuBgView(getContext());
        //添加背景view
        addView(mBgView, new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //设置背景颜色
        mBgView.setColor(contentLayout.getBackground());
        addView(contentLayout,new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setTouchY(float fingerY, float percent) {
        mBgView.setTouchY(fingerY, percent);
    }


}
