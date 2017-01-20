package com.example.tellh.recyclerviewdemo.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tellh.recyclerviewdemo.AndroidApplication;
import com.example.tellh.recyclerviewdemo.R;
import com.example.tellh.recyclerviewdemo.Utils;
import com.example.tellh.recyclerviewdemo.adapter.DiffRecyclerAdapter;
import com.example.tellh.recyclerviewdemo.adapter.DiffRecyclerAdapter.Differentiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
        adapter = new DiffRecyclerAdapter<AppInfo>(null) {
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
        getApps().subscribeOn(Schedulers.io())
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
                            new AppInfo("New item" + System.currentTimeMillis(), displayList.get(0).icon, System.currentTimeMillis()));
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
                        if (old.name != null && !old.name.equals(news.name))
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

    private class AppInfo implements Differentiable<AppInfo> {
        private long id;
        private String name;
        private String icon;

        public AppInfo(String name, String icon, long id) {
            this.id = id;
            this.name = name;
            this.icon = icon;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        @Override
        public AppInfo clone() {
            return new AppInfo(name, icon, id);
        }

        @Override
        public boolean sameItems(AppInfo o) {
            //这里的lastUpdateTime相当于每个item的id
            return equals(o) || id == o.id;
        }

        @Override
        public boolean sameContents(AppInfo o) {
            return name.equals(o.name) && icon.equals(o.icon);
        }
    }

    private Observable<List<AppInfo>> getApps() {
        return Observable.create(new Observable.OnSubscribe<List<AppInfo>>() {
            @Override
            public void call(Subscriber<? super List<AppInfo>> subscriber) {
                List<AppInfoRich> apps = new ArrayList<>();
                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> infos = DiffUtilActivity.this.getPackageManager().queryIntentActivities(mainIntent, 0);
                for (ResolveInfo info : infos) {
                    apps.add(new AppInfoRich(DiffUtilActivity.this, info));
                }
                List<AppInfo> result = new ArrayList<>(apps.size());
                for (AppInfoRich appInfo : apps) {
                    Bitmap icon = Utils.drawableToBitmap(appInfo.getIcon());
                    String name = appInfo.getName();
                    String iconPath = AndroidApplication.getInstance().getApplicationContext().getFilesDir() + "/" + name;
                    Utils.storeBitmap(AndroidApplication.getInstance(), icon, name);

                    if (subscriber.isUnsubscribed()) {
                        return;
                    }
                    result.add(new AppInfo(name, iconPath, appInfo.getLastUpdateTime()));
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public class AppInfoRich implements Comparable<Object> {

        String mName = null;

        private Context mContext;

        private ResolveInfo mResolveInfo;

        private ComponentName mComponentName = null;

        private PackageInfo pi = null;

        private Drawable icon = null;

        public AppInfoRich(Context ctx, ResolveInfo ri) {
            mContext = ctx;
            mResolveInfo = ri;

            mComponentName = new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);

            try {
                pi = ctx.getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }

        public String getName() {
            if (mName != null) {
                return mName;
            } else {
                try {
                    return getNameFromResolveInfo(mResolveInfo);
                } catch (PackageManager.NameNotFoundException e) {
                    return getPackageName();
                }
            }
        }

        public String getActivityName() {
            return mResolveInfo.activityInfo.name;
        }

        public String getPackageName() {
            return mResolveInfo.activityInfo.packageName;
        }

        public ComponentName getComponentName() {
            return mComponentName;
        }

        public String getComponentInfo() {
            if (getComponentName() != null) {
                return getComponentName().toString();
            } else {
                return "";
            }
        }

        public ResolveInfo getResolveInfo() {
            return mResolveInfo;
        }

        public PackageInfo getPackageInfo() {
            return pi;
        }

        public String getVersionName() {
            PackageInfo pi = getPackageInfo();
            if (pi != null) {
                return pi.versionName;
            } else {
                return "";
            }
        }

        public int getVersionCode() {
            PackageInfo pi = getPackageInfo();
            if (pi != null) {
                return pi.versionCode;
            } else {
                return 0;
            }
        }

        public Drawable getIcon() {
            if (icon == null) {
                icon = getResolveInfo().loadIcon(mContext.getPackageManager());
            /*
            Drawable dr = getResolveInfo().loadIcon(mContext.getPackageManager());
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            icon = new BitmapDrawable(mContext.getResources(), AppHelper.getResizedBitmap(bitmap, 144, 144));
            */
            }
            return icon;
        }

        @SuppressLint("NewApi")
        public long getFirstInstallTime() {
            PackageInfo pi = getPackageInfo();
            if (pi != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                return pi.firstInstallTime;
            } else {
                return 0;
            }
        }

        @SuppressLint("NewApi")
        public long getLastUpdateTime() {
            PackageInfo pi = getPackageInfo();
            if (pi != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                return pi.lastUpdateTime;
            } else {
                return 0;
            }
        }

        @Override
        public int compareTo(Object o) {
            AppInfoRich f = (AppInfoRich) o;
            return getName().compareTo(f.getName());
        }

        @Override
        public String toString() {
            return getName();
        }

        /**
         * Helper method to get an applications name!
         */

        public String getNameFromResolveInfo(ResolveInfo ri) throws PackageManager.NameNotFoundException {
            String name = ri.resolvePackageName;
            if (ri.activityInfo != null) {
                Resources res = mContext.getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
                Resources engRes = getEnglishRessources(res);

                if (ri.activityInfo.labelRes != 0) {
                    name = engRes.getString(ri.activityInfo.labelRes);

                    if (name == null || name.equals("")) {
                        name = res.getString(ri.activityInfo.labelRes);
                    }

                } else {
                    name = ri.activityInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                }
            }
            return name;
        }

        public Resources getEnglishRessources(Resources standardResources) {
            AssetManager assets = standardResources.getAssets();
            DisplayMetrics metrics = standardResources.getDisplayMetrics();
            Configuration config = new Configuration(standardResources.getConfiguration());
            config.locale = Locale.US;
            return new Resources(assets, metrics, config);
        }
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
