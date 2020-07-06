package com.example.studyanimtation.view.recyclerview;

import android.view.View;
import android.view.ViewGroup;

public interface MyAdapter {
    //多少个itme
    int getCount();

    //itme的类型
    int getItmeViewType();

    //itme的类型数量
    int getViewTypeCount();

    //获取view
    View getView(int postion, View convertView, ViewGroup parent);
    //获取高度
    int getHeight(int index);

}
