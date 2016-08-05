package com.example.tellh.recyclerviewdemo.adapter;

import android.support.v7.widget.RecyclerView;

public abstract class ItemViewBinder<T> {

    protected T data;

    public ItemViewBinder(T data) {
        this.data = data;
    }

    protected abstract void onBindView(RecyclerView.ViewHolder holder, int pos);

    public T getData() {
        return data;
    }

    protected abstract int getItemLayoutId();

}
