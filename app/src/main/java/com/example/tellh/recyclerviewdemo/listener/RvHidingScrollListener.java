package com.example.tellh.recyclerviewdemo.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public abstract class RvHidingScrollListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 120;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

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

    protected void hideView(Toolbar toolbar) {
        toolbar.animate()
                .translationY(-toolbar.getHeight())
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    protected void hideView(ImageButton button) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) button.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        button.animate()
                .translationY(button.getHeight() + fabBottomMargin)
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    protected void showView(Toolbar toolbar) {
        toolbar.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
    }

    protected void showView(ImageButton button) {
        button.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
    }


    public abstract void onHide();

    public abstract void onShow();

}