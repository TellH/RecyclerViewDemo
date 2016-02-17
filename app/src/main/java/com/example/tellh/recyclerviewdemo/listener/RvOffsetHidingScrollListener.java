package com.example.tellh.recyclerviewdemo.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.tellh.recyclerviewdemo.Utils;

public class RvOffsetHidingScrollListener extends RecyclerView.OnScrollListener {

    private final Toolbar mToolbar;
    private int mToolbarOffset = 0;
    private int mToolbarHeight;
    private boolean mControlsVisible = true;
    private static final float HIDE_THRESHOLD = 10;
    private static final float SHOW_THRESHOLD = 70;

    public RvOffsetHidingScrollListener(Context context, Toolbar toolbar) {
        mToolbarHeight = Utils.getToolbarHeight(context);
        mToolbar = toolbar;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset);

        if ((mToolbarOffset < mToolbarHeight && dy > 0) || (mToolbarOffset > 0 && dy < 0)) {
            mToolbarOffset += dy;
        }
    }

    private boolean isReachTop(RecyclerView recyclerView) {
        //因为存在header所以是1
        int firstItemtop = recyclerView.getChildAt(1).getTop();
        int parentTop = recyclerView.getTop();
        return !(firstItemtop < parentTop);
    }

    private boolean isReachBottom(RecyclerView recyclerView) {
        int lastItemBottom = recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom();
        int parentBottom = recyclerView.getBottom();
        if (lastItemBottom > parentBottom)
            return false;
        else return true;
    }

    private void clipToolbarOffset() {
        if (mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if (mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    private void setVisible() {
        if (mToolbarOffset > 0) {
            onShow();
            mToolbarOffset = 0;
        }
        mControlsVisible = true;
    }

    private void onShow() {
        if (mToolbar != null) {
            mToolbar.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (mControlsVisible) {
                if (mToolbarOffset > HIDE_THRESHOLD) {
                    setInvisible();
                } else {
                    setVisible();
                }
            } else {
                if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                    setVisible();
                } else {
                    if (isReachTop(recyclerView) || isReachBottom(recyclerView)) {
                        setVisible();
                        Log.d("t", "reach");
                    } else setInvisible();
                }
            }
        }
    }

    private void setInvisible() {
        if (mToolbarOffset < mToolbarHeight) {
            onHide();
            mToolbarOffset = mToolbarHeight;
        }
        mControlsVisible = false;
    }

    private void onHide() {
        if (mToolbar != null) {
            mToolbar.animate()
                    .translationY(-mToolbar.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2))
                    .start();
        }
    }

    public void onMoved(int distance) {
        mToolbar.setTranslationY(-distance);
    }
}