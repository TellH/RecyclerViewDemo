package com.example.tellh.recyclerviewdemo;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by tlh on 2016/2/19.
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    //adapter实现该接口，用于监听item的移动和删除动作，来进行数据的处理和同步
    private final ItemTouchListener mListener;

    public ItemTouchHelperCallback(@NonNull ItemTouchListener listener) {
        mListener = listener;
    }

    //True if ItemTouchHelper should start dragging an item when it is long pressed, false otherwise.
    //是否长按触发拖放操作
    //but you may want to disable this
    // if you want to start dragging on a custom view touch using startDrag(RecyclerView.ViewHolder).
    // Default value is true.默认返回true
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    //Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped over the View.
    //Default value returns true but you may want to disable this
    // if you want to start swiping on a custom view touch using startSwipe(RecyclerView.ViewHolder).
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    //设置手势的方向
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
/*        int swipeFlags = makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT) |
                makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT);*/
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int dragFlags, swipeFlags;
        if (layoutManager instanceof GridLayoutManager) {
            swipeFlags = 0;
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mListener.OnItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mListener.OnItemDismiss(viewHolder.getAdapterPosition());
    }

    //设置drag或swipe的动画效果
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

    public interface ItemTouchListener {
        void OnItemMove(int fromPosition, int toPosition);

        void OnItemDismiss(int position);
    }
}
