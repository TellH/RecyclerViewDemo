package com.example.tellh.recyclerviewdemo.adapter;

import com.example.tellh.recyclerviewdemo.ItemTouchHelperCallback;

import java.util.Collections;

/**
 * Created by tlh on 2016/2/19.
 */
public class ItemTouchAdapterWrapper extends HeaderAndFooterAdapterWrapper implements ItemTouchHelperCallback.ItemTouchListener {

    public ItemTouchAdapterWrapper(ItemTouchAdapter adapter) {
        super(adapter);
    }

    @Override
    public void OnItemMove(int fromPosition, int toPosition) {
        swap(fromPosition, toPosition);
    }

    @Override
    public void OnItemDismiss(int position) {
        delete(position);
    }

    @Override
    public void add(int pos, Object item) {
        mItems.add(pos, item);
        notifyItemInserted(pos + getHeadersCount());
    }

    @Override
    public void delete(int pos) {
        mItems.remove(pos);
        notifyItemRemoved(pos + getHeadersCount());
    }

    @Override
    public void swap(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition - getHeadersCount(), toPosition - getHeadersCount());
        notifyItemMoved(fromPosition, toPosition);
    }
}
