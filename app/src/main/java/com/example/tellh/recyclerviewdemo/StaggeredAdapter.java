package com.example.tellh.recyclerviewdemo;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by tlh on 2016/2/14.
 */
public class StaggeredAdapter extends RecycAdapter {
    private int[] heighs;
    public StaggeredAdapter(Context ctx, List<String> list) {
        super(ctx,list);
        heighs=new int[list.size()];
        for (int i=0;i<heighs.length;i++){
            heighs[i]=(int) (Math.random()*300+100);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height= heighs[position];
        holder.itemView.setLayoutParams(layoutParams);
        holder.tv.setText((String)mData.get(position));
    }

}
