package com.example.tellh.recyclerviewdemo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tellh.recyclerviewdemo.R;

import java.util.List;

/**
 * Created by tlh on 2016/2/18.
 */
public class FooterLoadMoreAdapter extends BaseRecyclerAdapter<String> {
    public static final int PULL_TO_LOAD_MORE = 0;
    public static final int LOADING = 1;
    public static final int NO_MORE = 2;
    private int mFooterStatus=PULL_TO_LOAD_MORE;

    public FooterLoadMoreAdapter(Context ctx, List<String> list) {
        super(ctx, list);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.item_cardview;
    }

    @Override
    public void bindData(RecyclerViewHolder holder, int position, String item) {
        //调用holder.getView(),getXXX()方法根据id得到控件实例，进行数据绑定即可
        holder.setText(R.id.tv_num, item);
    }

    @Override
    protected int getFooterLayoutId() {
        return R.layout.footer_load_more;
    }

    @Override
    protected void bindFooter(RecyclerViewHolder holder, int position) {
        ProgressBar progressBar = (ProgressBar) holder.getView(R.id.progressBar);
        switch (mFooterStatus){
            case PULL_TO_LOAD_MORE:
                progressBar.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_footer, "上拉加载更多...");
                break;
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_footer,"正在拼命加载...");
                break;
            case NO_MORE:
                holder.setText(R.id.tv_footer, "没有更多了！");
                progressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void setFooterStatus(int status){
        mFooterStatus=status;
        notifyDataSetChanged();
    }
}
