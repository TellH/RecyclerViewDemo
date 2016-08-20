package com.example.tellh.recyclerviewdemo.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.tellh.recyclerviewdemo.R;

/**
 * Created by tlh on 2016/2/18.
 */
public class FooterLoadMoreAdapterWrapper extends HeaderAndFooterAdapterWrapper {
    public static final int PULL_TO_LOAD_MORE = 0;
    public static final int LOADING = 1;
    public static final int NO_MORE = 2;
    private int mFooterStatus = PULL_TO_LOAD_MORE;
    private String toLoadText = "上拉加载更多...";
    private String noMoreText = "没有更多了！";
    private String loadingText = "正在拼命加载...";

    public FooterLoadMoreAdapterWrapper(BaseRecyclerAdapter adapter) {
        super(adapter);
    }

    @Override
    protected void onBindFooter(RecyclerViewHolder holder, int position) {
        ProgressBar progressBar = (ProgressBar) holder.getView(R.id.progressBar);
        switch (mFooterStatus) {
            case PULL_TO_LOAD_MORE:
                progressBar.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_footer, toLoadText);
                break;
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_footer, loadingText);
                break;
            case NO_MORE:
                holder.setText(R.id.tv_footer, noMoreText);
                progressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void setOnReachFooterListener(RecyclerView recyclerView, final OnReachFooterListener listener) {
        if (recyclerView == null || listener == null)
            return;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isReachBottom(recyclerView, newState) && mFooterStatus != LOADING) {
                    setFooterStatus(LOADING);
                    listener.onReach();
                }
            }
        });
    }

    public void setFooterStatus(int status) {
        mFooterStatus = status;
        notifyDataSetChanged();
    }

    public boolean isReachBottom(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            return ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()
                    == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    public interface OnReachFooterListener {
        void onReach();
    }


    public void setToLoadText(String toLoadText) {
        this.toLoadText = toLoadText;
    }

    public void setNoMoreText(String noMoreText) {
        this.noMoreText = noMoreText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }
}
