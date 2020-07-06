package com.example.studyanimtation.view.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerView extends ViewGroup {
    private MyAdapter adapter;
    // y 内容的偏移量
    private int scrollY;
    //当前view的集合
    private List<View> currentViews;
    //当前手指滑动的Y距离
    private int fingerY;
    //行数
    private int rowCount;
    //当前RecyclerView的宽度
    private int currentWidth;
    //当前view的高度
    private int currentHeigth;
    //该类中存放的是所有view的高度，因为样式不一样，可能子view的高度不同
    private int[] heigths;
    //是否初始化 用于处理锁屏等情况下的view 重绘，恢复数据的问题
    private boolean isInit;
    //view的第一行，是内容的第几行
    private int firstRow;
    //最小滑动距离
    private int touchSlop;

    public MyRecyclerView(Context context) {
        this(context, null);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setAdapter(MyAdapter adapter) {
        this.adapter = adapter;
        scrollY = 0;
        firstRow = 0;
        isInit = true;
        //请求重新布局
        requestLayout();
    }

    private void init() {
        currentViews = new ArrayList<>();
        isInit = true;
        //该类中定义的一些view的规范信息，比如滑动的最小距离
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int h;
        //初始化heiths
        //根据adapter中的数据，为heights填值
        if (adapter != null) {
            heigths = new int[adapter.getCount()];
            for (int i = 0; i < adapter.getCount(); i++) {
                heigths[i] = adapter.getHeight(i);
            }
        }
        //内容总高度 根据所有view的高度计算得出该值
        int tmpH = sumHeight(heigths, 0, heigths.length);
        h = Math.min(heightSize, tmpH);
        setMeasuredDimension(widthSize, h);
    }

    private int sumHeight(int[] heigths, int firstIndex, int count) {
        int sum = 0;
        count += firstIndex;
        for (int i = firstIndex; i < count; i++) {
            sum += heigths[i];
        }
        return sum;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {


    }

}
