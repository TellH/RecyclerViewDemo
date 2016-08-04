package com.example.tellh.recyclerviewdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.example.tellh.recyclerviewdemo.R;

import java.util.List;

/**
 * Created by tlh on 2016/2/19.
 */
public class ItemTouchAdapter extends BaseRecyclerAdapter<String>{
    public ItemTouchAdapter(Context ctx, List<String> list) {
        super(ctx, list);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_cardview;
    }

    @Override
    protected void bindData(final RecyclerViewHolder holder, int position, String item) {
        holder.setText(R.id.tv_num, item);
        holder.getView(R.id.iv_reorder).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //让Activity实现OnStartDragListener接口，以便调用ItemTouchHelper的startDrag方法
                if (event.getAction()==MotionEvent.ACTION_DOWN)
//                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN)
                    ((OnStartActionListener)mContext).onStartDrag(holder);
                return false;
            }
        });
    }

    public interface OnStartActionListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
        void onStartSwipe(RecyclerView.ViewHolder viewHolder);
    }
}
