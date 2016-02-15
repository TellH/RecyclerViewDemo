package com.example.tellh.recyclerviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tlh on 2016/2/14.
 */
public class RecycAdapter extends RecyclerView.Adapter<RecycAdapter.MyViewHolder>  {

    protected final List<String> mData;
    protected final Context mContext;
    protected LayoutInflater mInflater;
    private ItemClickListener mListener;

    private int times;
    public RecycAdapter(Context ctx,List<String> list) {
        mData=list;
        mContext=ctx;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TAG", "onCreateViewHolder() called with: " + "parent = [" + parent + "], viewType = [" + viewType + "]"+",times="+"["+(++times)+"]");
        final MyViewHolder holder=new MyViewHolder(mInflater.inflate(R.layout.item,parent,false));

        if (mListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v,holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onItemLongClick(v,holder.getLayoutPosition());
                    return false;
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder() called with: " + "holder = [" + holder + "], position = [" + position + "]");
        holder.tv.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(int pos,String item){
        mData.add(pos, item);
        notifyItemInserted(pos);
    }
    public void delete(int pos){
        mData.remove(pos);
        notifyItemRemoved(pos);
    }
    public void setOnItemClickListener(ItemClickListener listener){
        mListener=listener;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv= (TextView) itemView.findViewById(R.id.tv_num);
        }
    }

    public interface ItemClickListener{
        public void onItemClick(View itemView,int pos);
        public void onItemLongClick(View itemView,int pos);
    }
}
