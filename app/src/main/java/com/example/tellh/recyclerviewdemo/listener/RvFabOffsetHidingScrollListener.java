package com.example.tellh.recyclerviewdemo.listener;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.tellh.recyclerviewdemo.Utils;

public class RvFabOffsetHidingScrollListener extends RecyclerView.OnScrollListener {

    private ImageButton mFab;
    private int mFabOffset = 0;
    private int mFabHeight;
    private boolean mControlsVisible = true;
    private static final float HIDE_THRESHOLD = 10;
    private static final float SHOW_THRESHOLD = 70;
    private int mScrollDistance;

    public RvFabOffsetHidingScrollListener(Context context, @NonNull ImageButton fab) {
        mFabHeight = Utils.getTabsHeight(context);
        mFab = fab;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mFabOffset);

        if ((mFabOffset < mFabHeight && dy > 0) || (mFabOffset > 0 && dy < 0)) {
            mFabOffset += dy;
        }
        Log.d("t", "mFabOffset:" + mFabOffset + "dy:" + dy + "mFabHeight:" + mFabHeight);
        mScrollDistance += dy;
    }

    private boolean isReachTop(RecyclerView recyclerView) {
        //因为存在header所以是1
        int firstItemtop = recyclerView.getChildAt(1).getTop();
        int parentTop = recyclerView.getTop();
        return !(firstItemtop > parentTop);
    }

    private boolean isReachBottom(RecyclerView recyclerView) {
        int lastItemBottom = recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom();
        int parentBottom = recyclerView.getBottom();
        return lastItemBottom <= parentBottom;
    }

    private void clipToolbarOffset() {
        if (mFabOffset > mFabHeight) {
            mFabOffset = mFabHeight;
        } else if (mFabOffset < 0) {
            mFabOffset = 0;
        }
    }

    private void setVisible() {
        onShow();
        mFabOffset = 0;
        mControlsVisible = true;
    }

    private void onShow() {
        if (mFab != null) {
            mFab.animate()
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
                if (mFabOffset > HIDE_THRESHOLD) {
                    setInvisible();
                } else {
                    setVisible();
                }
            } else {
                if ((mFabHeight - mFabOffset) > SHOW_THRESHOLD) {
                    setVisible();
                } else {
                    if (isReachTop(recyclerView) || isReachBottom(recyclerView)) {
                        setVisible();
                    } else setInvisible();
                }
            }
        }
    }

    private void setInvisible() {
        onHide();
        mFabOffset = mFabHeight;
        mControlsVisible = false;
    }

    private void onHide() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFab.animate()
                .translationY(mFab.getHeight() + fabBottomMargin)
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    public void onMoved(int distance) {
        Log.d("TAG", "onMoved() called with: " + "distance = [" + distance + "]");
        mFab.setTranslationY(distance);
    }
}