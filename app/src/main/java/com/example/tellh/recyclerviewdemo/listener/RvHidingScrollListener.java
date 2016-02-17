package com.example.tellh.recyclerviewdemo.listener;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public abstract class RvHidingScrollListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 120;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private Context mContext;

    public RvHidingScrollListener(Context context, Toolbar mToolbar, FloatingActionButton mFab) {
        this.mToolbar = mToolbar;
        this.mFab = mFab;
        mContext = context;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        //show views if first item is first visible position and views are hidden
        //检查第一个Item是否可见，如果可见，不执行隐藏View的逻辑，view总是显示
        if (firstVisibleItem == 0) {
            if (!controlsVisible) {
                onShow();
                controlsVisible = true;
            }
        } else {
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }
        }
        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }

    protected void hideToolbar() {
        if (mToolbar != null) {
            mToolbar.animate()
                    .translationY(-mToolbar.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2))
                    .start();
        }
    }

    protected void hideFab() {
        if (mFab != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            mFab.animate()
                    .translationY(mFab.getHeight() + fabBottomMargin)
                    .setInterpolator(new AccelerateInterpolator(2))
                    .start();
        }
    }

    protected void showToolbar() {
        if (mToolbar != null) {
            mToolbar.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
        }
    }

    protected void showFab() {
        if (mFab != null) {
            mFab.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
        }
    }


    public abstract void onHide();

    public abstract void onShow();

}