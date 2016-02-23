package com.example.tellh.recyclerviewdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tlh on 2016/2/15.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = -1;
    private static final int TYPE_FOOTER = 1;
    protected final List<T> mItems;
    protected final Context mContext;
    protected LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public BaseRecyclerAdapter(Context ctx, List<T> list) {
        mItems = (list != null) ? list : new ArrayList<T>();
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);

        if (isHeaderExist()) mItems.add(0, null);
        if (isFooterExist()) mItems.add(mItems.size(), null);
    }


    /**
     * 如果你要用在GridLayout中使用header或footer，记得在setLayoutManager之后调用该方法
     *
     * @param manager
     */
    public void getGridLayoutManager(@NonNull final GridLayoutManager manager) {
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == getFooterPosition() || position == getHeaderPosition()) ?
                        manager.getSpanCount() : 1;
            }
        });
    }

    /**
     * 如果需要在子类重写该方法，建议参照此形式
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (!isHeaderExist() && !isFooterExist())
            return super.getItemViewType(position);
        else {
            if (position == getHeaderPosition())
                return TYPE_HEADER;
            if (position == getFooterPosition())
                return TYPE_FOOTER;
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = -1;
        switch (viewType) {
            case TYPE_HEADER:
                layoutId = getHeaderLayoutId();
                break;
            case TYPE_FOOTER:
                layoutId = getFooterLayoutId();
                break;
            case TYPE_ITEM:
                layoutId = getItemLayoutId(viewType);
                break;
            default:
                throw new RuntimeException("illegal viewType!");
        }
        if (layoutId == -1)
            throw new RuntimeException("The method getHeaderLayoutId() return the wrong id, you should override it and return the correct id");

        final RecyclerViewHolder holder = new RecyclerViewHolder(mContext,
                mInflater.inflate(layoutId, parent, false));
        if (mClickListener != null && viewType != TYPE_HEADER && viewType != TYPE_FOOTER) {
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
        if (mLongClickListener != null && viewType != TYPE_HEADER && viewType != TYPE_FOOTER) {
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

    protected int getFooterLayoutId() {
        return -1;
    }

    final public boolean isFooterExist() {
        return getFooterLayoutId() != -1;
    }

    protected int getHeaderLayoutId() {
        return -1;
    }

    final public boolean isHeaderExist() {
        return getHeaderLayoutId() != -1;
    }

    final public int getHeaderPosition() {
        if (isHeaderExist()) return 0;
        return -1;
    }

    final public int getFooterPosition() {
        if (isFooterExist()) return getItemCount() - 1;
        return -2;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (position == getFooterPosition()) bindFooter(holder, position);
        else if (position == getHeaderPosition()) bindHeader(holder, position);
        else bindData(holder, position, mItems.get(position));
    }


    @Override
    final public int getItemCount() {
        return mItems.size();
    }

    //添加数据到特定的位置，该位置已考虑header和footer
    public void add(int pos, T item) {
        if (pos != getHeaderPosition() && pos != getFooterPosition() + 1) {
            mItems.add(pos, item);
            notifyItemInserted(pos);
        } else {
            try {
                throw new Exception("your position to add or delete should consider the header and footer.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //在特定位置删除数据，该位置已考虑header和footer
    public void delete(int pos) {
        if (pos != getHeaderPosition() && pos != getFooterPosition()) {
            mItems.remove(pos);
            notifyItemRemoved(pos);
        } else {
            try {
                throw new Exception("your position to add or delete should consider the header and footer.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //在特定的位置之间交换位置，该位置已考虑header和footer
    public void swap(int fromPosition, int toPosition) {
        int head = getHeaderPosition();
        int foot = getFooterPosition();
        if (fromPosition != head && toPosition != head && fromPosition != foot && toPosition != foot) {
            Collections.swap(mItems, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        } else {
            try {
                throw new Exception("your position to swap should consider the header and footer.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    final public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    final public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    //重写该方法进行header视图的数据绑定
    protected void bindHeader(RecyclerViewHolder holder, int position) {
    }

    //重写该方法进行footer视图的数据绑定
    protected void bindFooter(RecyclerViewHolder holder, int position) {
    }

    /**
     * 重写该方法，根据viewType设置item的layout
     *
     * @param viewType 通过重写getItemViewType（）设置，默认item是0
     * @return
     */
    abstract protected int getItemLayoutId(int viewType);

    /**
     * 重写该方法进行item数据项视图的数据绑定
     *
     * @param holder   通过holder获得item中的子View，进行数据绑定
     * @param position 该item的position
     * @param item     映射到该item的数据
     */
    abstract protected void bindData(RecyclerViewHolder holder, int position, T item);

    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int pos);
    }

}
