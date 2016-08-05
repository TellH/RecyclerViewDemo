
package com.example.tellh.recyclerviewdemo.adapter;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 实现多种item样式的过程：
 * getItemViewType()根据position获得样式常量->用这个常量在onCreateViewHolder中创建特定的ViewHolder
 * ->借助ViewHolder进行数据绑定。
 * 思路：
 * 将数据绑定和数据封装到一个实体类ItemViewBinder，不同的binder在position中返回各自的layoutId作为每种样式的唯一常量，
 * 将Adapter中实例化ViewHolder的操作交给ViewHolderProvider，将数据绑定交给ItemViewBinder。
 */
public class MultiTypeItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemViewBinder> typeItemBinders;
    private static SparseArrayCompat<ViewHolderProvider> viewHolderProviderPool = new SparseArrayCompat<>();

    //add different item type. Because different types of items may use different different kinds of ViewHolders.
    public static void register(ViewHolderProvider provider) {
        viewHolderProviderPool.put(provider.layoutId(), provider);
    }

    public MultiTypeItemsAdapter(@NonNull List<ItemViewBinder> binders) {
        this.typeItemBinders = binders;
    }

    @Override
    public int getItemViewType(int position) {
        ItemViewBinder binder = typeItemBinders.get(position);
        return binder.getItemLayoutId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int indexViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(indexViewType, parent, false);
        return viewHolderProviderPool.get(indexViewType).provide(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewBinder binder = typeItemBinders.get(position);
        binder.onBindView(holder, position);
    }

    @Override
    public int getItemCount() {
        return typeItemBinders.size();
    }

}