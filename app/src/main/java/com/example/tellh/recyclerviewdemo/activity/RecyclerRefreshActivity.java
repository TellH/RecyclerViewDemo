package com.example.tellh.recyclerviewdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tellh.recyclerviewdemo.R;
import com.example.tellh.recyclerviewdemo.adapter.BaseRecyclerAdapter;
import com.example.tellh.recyclerviewdemo.adapter.FooterLoadMoreAdapter;
import com.example.tellh.recyclerviewdemo.listener.RvFabOffsetHidingScrollListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerRefreshActivity extends AppCompatActivity {

    List<String> mDataList;
    private RecyclerView recyclerView;
    private BaseRecyclerAdapter mAdapter;
    private Toolbar mToolbar;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        init();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.id_action_gridview:
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                break;
            case R.id.id_action_listview:
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                break;
            case R.id.id_action_horizontalGridView:
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL));
                break;
            case R.id.id_action_staggeredgridview:
                startActivity(new Intent(this, StaggeredActivity.class));
                break;
            case R.id.id_action_add:
                ((BaseRecyclerAdapter) mAdapter).add(2, "new item");
                break;
            case R.id.id_action_delete:
                mAdapter.delete(2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(mToolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mDataList = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            mDataList.add(String.valueOf(i));
        }
        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FooterLoadMoreAdapter(this, mDataList);

        recyclerView.setAdapter(mAdapter);
        //添加item点击事件监听
        ((BaseRecyclerAdapter) mAdapter).setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                Toast.makeText(RecyclerRefreshActivity.this, "click " + pos, Toast.LENGTH_SHORT).show();
            }
        });
        ((BaseRecyclerAdapter) mAdapter).setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int pos) {
                Toast.makeText(RecyclerRefreshActivity.this, "long click " + pos, Toast.LENGTH_SHORT).show();
            }
        });
        //设置布局样式LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerRefreshActivity.this, LinearLayoutManager.VERTICAL, false));

        recyclerView.addOnScrollListener(new RvFabOffsetHidingScrollListener(this, fab) {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isReachBottom(recyclerView, newState)) {
                    ((FooterLoadMoreAdapter)mAdapter).setFooterStatus(FooterLoadMoreAdapter.LOADING);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.add(mAdapter.getItemCount()-1,"new");
                            ((FooterLoadMoreAdapter)mAdapter).setFooterStatus(FooterLoadMoreAdapter.PULL_TO_LOAD_MORE);
//                            ((FooterLoadMoreAdapter)mAdapter).setFooterStatus(FooterLoadMoreAdapter.NO_MORE);
                        }
                    }, 5000);
                }
            }
        });

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseRecyclerAdapter) mAdapter).add(0, "new");
                        if (mRefresh.isRefreshing())
                            mRefresh.setRefreshing(false);
                        recyclerView.smoothScrollToPosition(0);
                    }
                }, 3000);
            }
        });

    }

}