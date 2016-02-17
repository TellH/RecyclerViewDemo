package com.example.tellh.recyclerviewdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tlh on 2016/2/15.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private static final int TYPE_ITEM = 0;
    private final int TYPE_HEADER = -1;
    protected final List<T> mData;
    protected final Context mContext;
    protected LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public BaseRecyclerAdapter(Context ctx, List<T> list) {
        mData = (list != null) ? list : new ArrayList<T>();
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
    }


    /**
     * 如果需要在子类重写该方法，建议参照此形式
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (!isHeaderExist())
            return super.getItemViewType(position);
        else {
            if (position == 0)
                return TYPE_HEADER;
            else return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == TYPE_HEADER) {
            layoutId = getHeaderLayoutId();
            if (layoutId == -1)
                throw new RuntimeException("The method getHeaderLayoutId() return the wrong id, you should override it and return the correct id");
        } else layoutId = getItemLayoutId(viewType);
        final RecyclerViewHolder holder = new RecyclerViewHolder(mContext,
                mInflater.inflate(layoutId, parent, false));
        if (mClickListener != null && viewType != TYPE_HEADER) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isHeaderExist())
                        mClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                    else
                        mClickListener.onItemClick(holder.itemView, holder.getLayoutPosition() - 1);
                }
            });
        }
        if (mLongClickListener != null && viewType != TYPE_HEADER) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isHeaderExist()) {
                        mLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    } else {
                        mLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition() - 1);
                    }
                    return true;
                }
            });
        }
        return holder;
    }

    protected int getHeaderLayoutId() {
        return -1;
    }

    private boolean isHeaderExist() {
        return getHeaderLayoutId() != -1;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (!isHeaderExist())
            bindData(holder, position, mData.get(position));
        else {
            if (position != 0) {
                bindData(holder, position - 1, mData.get(position - 1));
            }
        }
    }


    @Override
    public int getItemCount() {
        if (!isHeaderExist()) {
            return mData.size();
        } else {
            return mData.size() + 1;
        }
    }

    public void add(int pos, T item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void delete(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    abstract protected int getItemLayoutId(int viewType);

    abstract protected void bindData(RecyclerViewHolder holder, int position, T item);

    public interface OnItemClickListener {
        public void onItemClick(View itemView, int pos);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View itemView, int pos);
    }

}
