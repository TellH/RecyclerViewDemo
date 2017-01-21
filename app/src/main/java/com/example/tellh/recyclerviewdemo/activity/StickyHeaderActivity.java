package com.example.tellh.recyclerviewdemo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tellh.recyclerviewdemo.AppInfo;
import com.example.tellh.recyclerviewdemo.R;
import com.example.tellh.recyclerviewdemo.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.example.tellh.recyclerviewdemo.Utils.getApps;

public class StickyHeaderActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TextView header;
    private int headerHeight = -1;
    private LinearLayoutManager linearLayoutManager;
    private int firstVisibleItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_header);
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        header = (TextView) findViewById(R.id.tv_header);
        linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        final StickyHeaderAdapter adapter = new StickyHeaderAdapter();
        rv.setAdapter(adapter);
        getApps(this).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<AppInfo>>() {
                    @Override
                    public void call(List<AppInfo> appInfos) {
                        adapter.getDisplayList().addAll(appInfos);
                        adapter.notifyItemInserted(0);
                        updateHeader(adapter);
                    }
                });
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (headerHeight == -1)
                    headerHeight = header.getHeight();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View view = linearLayoutManager.findViewByPosition(firstVisibleItemPosition + 1);
                if (view != null) {
                    if (view.getTop() <= headerHeight) {
                        header.setY(-(headerHeight - view.getTop()));
                    } else {
                        header.setY(0);
                    }
                }

                if (firstVisibleItemPosition != linearLayoutManager.findFirstVisibleItemPosition()) {
                    firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    header.setY(0);
                    updateHeader(adapter);
                }
            }
        });
    }

    private void updateHeader(StickyHeaderAdapter adapter) {
        AppInfo item = adapter.getDisplayList().get(firstVisibleItemPosition);
        header.setText(new SimpleDateFormat("MM-dd-yyyy").format(new Date(item.getId())));
    }

    public static class StickyHeaderAdapter extends RecyclerView.Adapter<StickyHeaderAdapter.ViewHolder> {
        private List<AppInfo> items;

        public StickyHeaderAdapter() {
            this.items = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sticky, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            AppInfo item = items.get(position);
            holder.name.setText(item.getName());
            Utils.getBitmap(item.getIcon())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            holder.image.setImageBitmap(bitmap);
                        }
                    });
            holder.header.setText(new SimpleDateFormat("MM-dd-yyyy").format(new Date(item.getId())));
        }

        @Override
        public int getItemCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }

        public List<AppInfo> getDisplayList() {
            return items;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView header;
            public TextView name;
            public ImageView image;

            public ViewHolder(View rootView) {
                super(rootView);
                this.header = (TextView) rootView.findViewById(R.id.tv_header);
                this.name = (TextView) rootView.findViewById(R.id.name);
                this.image = (ImageView) rootView.findViewById(R.id.image);
            }

        }
    }
}
