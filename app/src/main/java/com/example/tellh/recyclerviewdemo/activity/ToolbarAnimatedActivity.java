package com.example.tellh.recyclerviewdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.example.tellh.recyclerviewdemo.adapter.HeaderAndFooterAdapterWrapper;
import com.example.tellh.recyclerviewdemo.adapter.RecyclerViewHolder;
import com.example.tellh.recyclerviewdemo.listener.RvFabOffsetHidingScrollListener;
import com.example.tellh.recyclerviewdemo.listener.RvToolbarOffsetHidingScrollListener;

import java.util.ArrayList;
import java.util.List;

public class ToolbarAnimatedActivity extends AppCompatActivity {

    List<String> mDataList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private Toolbar mToolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
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
                recyclerView.setLayoutManager(new GridLayoutManager(this,3));
                break;
            case R.id.id_action_listview:
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                break;
            case R.id.id_action_horizontalGridView:
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
                break;
            case R.id.id_action_staggeredgridview:
                startActivity(new Intent(this,StaggeredActivity.class));
                break;
            case R.id.id_action_add:
                ((BaseRecyclerAdapter)mAdapter).add(2, "new item");
                break;
            case R.id.id_action_delete:
                ((BaseRecyclerAdapter)mAdapter).delete(2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
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
        mAdapter = new BaseRecyclerAdapter<String>(this,mDataList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_cardview;
            }
            @Override
            public void bindData(RecyclerViewHolder holder, int position,String item) {
                holder.setText(R.id.tv_num,item);
            }
        };
        HeaderAndFooterAdapterWrapper wrapper=new HeaderAndFooterAdapterWrapper((BaseRecyclerAdapter) mAdapter);
        wrapper.addHeader(R.layout.header);
        recyclerView.setAdapter(wrapper);
        //添加item点击事件监听
        ((BaseRecyclerAdapter)mAdapter).setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                Toast.makeText(ToolbarAnimatedActivity.this, "click " + pos, Toast.LENGTH_SHORT).show();
            }
        });
        ((BaseRecyclerAdapter)mAdapter).setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int pos) {
                Toast.makeText(ToolbarAnimatedActivity.this, "long click " + pos, Toast.LENGTH_SHORT).show();
            }
        });
        //设置布局样式LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(ToolbarAnimatedActivity.this, LinearLayoutManager.VERTICAL, false));

//        recyclerView.addOnScrollListener(new RvHidingScrollListener(this, mToolbar, fab) {
//            @Override
//            public void onHide() {
//                hideFab();
//                hideToolbar();
//            }
//
//            @Override
//            public void onShow() {
//                showFab();
//                showToolbar();
//            }
//        });

        recyclerView.addOnScrollListener(new RvToolbarOffsetHidingScrollListener(this,mToolbar));
        recyclerView.addOnScrollListener(new RvFabOffsetHidingScrollListener(this,fab));
    }

}