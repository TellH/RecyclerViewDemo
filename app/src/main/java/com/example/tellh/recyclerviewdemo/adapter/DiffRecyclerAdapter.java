package com.example.tellh.recyclerviewdemo.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tlh on 2017/1/19 :)
 */

public abstract class DiffRecyclerAdapter<T extends DiffRecyclerAdapter.Differentiable<T>> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> displayList;
    protected List<T> backupList;
    protected DiffContentHandler diffContentHandler;
    private DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
        @Override
        public int getOldListSize() {
            return backupList.size();
        }

        @Override
        public int getNewListSize() {
            return displayList.size();
        }

        // judge if the same items
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return DiffRecyclerAdapter.this.areItemsTheSame(backupList.get(oldItemPosition), displayList.get(newItemPosition));
        }

        // if they are the same items, whether the contents has bean changed.
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return DiffRecyclerAdapter.this.areContentsTheSame(backupList.get(oldItemPosition), displayList.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            if (diffContentHandler == null)
                return null;
            return diffContentHandler.getChangePayload(backupList.get(oldItemPosition), displayList.get(newItemPosition));
        }
    };

    public DiffRecyclerAdapter(List<T> displayList) {
        this.displayList = displayList == null ? new ArrayList<T>() : displayList;
        if (displayList != null) {
            backupList.addAll(displayList);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(getLayoutId(), parent, false);
        return newViewHolderInstance(v);
    }

    protected abstract RecyclerView.ViewHolder newViewHolderInstance(View v);

    protected abstract int getLayoutId();

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindData(holder, position, displayList.get(position));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty() && diffContentHandler != null) {
            Bundle b = (Bundle) payloads.get(0);
            diffContentHandler.bindView(holder, position, b);
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    protected abstract void bindData(RecyclerView.ViewHolder holder, int position, T data);

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    private void notifyDiff() {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(this);
    }

    private void backupDisplayList() {
        backupDisplayList(false);
    }

    private void backupDisplayList(boolean checkDiffContent) {
        if (!checkDiffContent) {
            backupList.addAll(displayList);
            return;
        }
        backupList = new ArrayList<>();
        for (T t : displayList) {
            backupList.add(t.clone());
        }
    }

    // For DiffUtil, if they are the same items, whether the contents has bean changed.
    protected boolean areContentsTheSame(T old, T news) {
        if (old == null)
            return news == null;
        return old.sameContents(news);
    }

    // judge if the same item for DiffUtil
    protected boolean areItemsTheSame(T old, T news) {
        if (old == null)
            return news == null;
        return old.sameItems(news);
    }

    public void setDiffContentHandler(DiffContentHandler diffContentHandler) {
        this.diffContentHandler = diffContentHandler;
    }

    public interface Differentiable<T> {
        T clone();

        boolean sameItems(T o);

        boolean sameContents(T o);
    }

    public abstract class DiffContentHandler {
        private Object getChangePayload(T old, T news) {
            Bundle diffBundle = new Bundle();
            handleDiff(diffBundle, old, news);
            if (diffBundle.size() == 0)
                return null;
            return diffBundle;
        }

        protected abstract void handleDiff(Bundle bundle, T old, T news);

        protected abstract void bindView(RecyclerView.ViewHolder holder, int position, Bundle bundle);
    }
}
