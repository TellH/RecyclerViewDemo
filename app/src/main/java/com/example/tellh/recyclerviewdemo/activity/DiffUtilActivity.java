package com.example.tellh.recyclerviewdemo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tellh.recyclerviewdemo.AppInfo;
import com.example.tellh.recyclerviewdemo.R;
import com.example.tellh.recyclerviewdemo.Utils;
import com.example.tellh.recyclerviewdemo.adapter.DiffRecyclerAdapter;

import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.example.tellh.recyclerviewdemo.Utils.getApps;

public class DiffUtilActivity extends AppCompatActivity {
    private RecyclerView rv;
    private DiffRecyclerAdapter<AppInfo> adapter;
    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diff_util);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DiffRecyclerAdapter<AppInfo>() {
            @Override
            protected RecyclerView.ViewHolder newViewHolderInstance(View v) {
                return new AppInfoViewHolder(v);
            }

            @Override
            protected int getLayoutId() {
                return R.layout.item_app_list;
            }

            @Override
            protected void bindData(RecyclerView.ViewHolder holder, int position, AppInfo data) {
                final AppInfoViewHolder viewHolder = (AppInfoViewHolder) holder;
                viewHolder.name.setText(data.getName());
                Utils.getBitmap(data.getIcon())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Bitmap>() {
                            @Override
                            public void call(Bitmap bitmap) {
                                viewHolder.image.setImageBitmap(bitmap);
                            }
                        });
            }
        };
        rv.setAdapter(adapter);
        getApps(this).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<AppInfo>>() {
                    @Override
                    public void call(List<AppInfo> appInfos) {
                        adapter.backupDisplayList(true);
                        adapter.getDisplayList().addAll(appInfos);
                        adapter.notifyDiff();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diff_util, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_with_rxjava:
                Observable.create(new Observable.OnSubscribe<DiffUtil.DiffResult>() {
                    @Override
                    public void call(Subscriber<? super DiffUtil.DiffResult> subscriber) {
                        adapter.backupDisplayList();
                        adapter.getDisplayList().remove(1);
                        adapter.getDisplayList().remove(3);
                        adapter.getDisplayList().remove(5);
                        adapter.getDisplayList().remove(7);
                        adapter.getDisplayList().remove(9);
                        subscriber.onNext(adapter.calculateDiff(false));
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<DiffUtil.DiffResult>() {
                            @Override
                            public void call(DiffUtil.DiffResult diffResult) {
                                if (diffResult != null)
                                    diffResult.dispatchUpdatesTo(adapter);
                            }
                        });
                break;
            case R.id.action_diff_normal:
                adapter.backupDisplayList();
                List<AppInfo> displayList = adapter.getDisplayList();
                if (random.nextBoolean()) {
                    Toast.makeText(this, "A New item was Insert!", Toast.LENGTH_SHORT).show();
                    displayList.add(1,
                            new AppInfo("New item" + System.currentTimeMillis(), displayList.get(0).getIcon(), System.currentTimeMillis()));
                } else {
                    displayList.remove(0);
                    displayList.remove(5);
                    Toast.makeText(this, "item0 amd item5 were removed!", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDiff();
                break;
            case R.id.action_update_content:
                adapter.setDiffContentHandler(adapter.new DiffContentHandler() {
                    @Override
                    protected void handleDiff(Bundle bundle, AppInfo old, AppInfo news) {
                        if (old.getName() != null && !old.getName().equals(news.getName()))
                            bundle.putString("APP_NAME", news.getName());
                    }

                    @Override
                    protected void bindView(RecyclerView.ViewHolder holder, int position, Bundle bundle) {
                        if (bundle == null)
                            return;
                        AppInfoViewHolder appInfoViewHolder = (AppInfoViewHolder) holder;
                        appInfoViewHolder.name.setText(bundle.getString("APP_NAME"));
                    }
                });
                adapter.backupDisplayList(true);
                adapter.getDisplayList().get(0).setName("App name was update!");
                adapter.notifyDiff();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AppInfoViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ImageView image;

        public AppInfoViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

}
