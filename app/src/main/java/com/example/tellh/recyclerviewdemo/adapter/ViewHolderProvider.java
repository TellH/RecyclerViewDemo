package com.example.tellh.recyclerviewdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by tlh on 2016/8/5.
 */
public interface ViewHolderProvider {
    int layoutId();
    RecyclerView.ViewHolder provide(View view);
}
