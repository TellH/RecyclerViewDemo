package com.example.tellh.recyclerviewdemo.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.example.tellh.recyclerviewdemo.R;
import com.example.tellh.recyclerviewdemo.adapter.RecyclerViewHolder;

import java.lang.reflect.Field;

/**
 * Created by tlh on 2016/3/20.
 */
public class OnShowPopupMenuClickListener implements View.OnClickListener {
    private static final String TAG = "OnShowPopupMenuClickListener";
    private final RecyclerView mRecyclerView;
    private final int mPosition;
    private final RecyclerViewHolder mHolder;
    //    private Album mAlbum;
    private Context mContext;
    private final AlphaAnimation mFadeOut = new AlphaAnimation(1.0f, 0.3f);
    private final AlphaAnimation mFadeIn = new AlphaAnimation(0.3f, 1.0f);
    public OnShowPopupMenuClickListener(Context context, RecyclerView recyclerView, RecyclerViewHolder holder) {
        mContext = context;
        mRecyclerView=recyclerView;
        mPosition=holder.getLayoutPosition();
        mHolder=holder;
        mFadeIn.setFillAfter(true);
        mFadeOut.setFillAfter(true);
//        mAlbum = album;
    }
    @SuppressLint("LongLogTag")
    @Override
    public void onClick(View v) {
        // This is an android.support.v7.widget.PopupMenu;
        PopupMenu popupMenu = new PopupMenu(mContext, v) {
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_delete:
//                        deleteAlbum(mAlbum);
                        Toast.makeText(mContext, "you click delete", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.popup_rename:
//                        renameAlbum(mAlbum);
                        Toast.makeText(mContext, "you click rename", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.popup_lock:
//                        lockAlbum(mAlbum);
                        Toast.makeText(mContext, "you click lock", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.popup_unlock:
//                        unlockAlbum(mAlbum);
                        Toast.makeText(mContext, "you click unlock", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.popup_set_cover:
//                        setAlbumCover(mAlbum);
                        Toast.makeText(mContext, "you click set cover", Toast.LENGTH_SHORT).show();
                        return true;

                    default:
//                        return super.onMenuItemSelected(menu, item);
                }
                return false;
            }
        };

        popupMenu.inflate(R.menu.popup_menu);

//        if (mAlbum.isLocked()) {
//            popupMenu.getMenu().removeItem(R.id.album_overflow_lock);
//            popupMenu.getMenu().removeItem(R.id.album_overflow_rename);
//            popupMenu.getMenu().removeItem(R.id.album_overflow_delete);
//        } else {
//            popupMenu.getMenu().removeItem(R.id.album_overflow_unlock);
//        }

        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            //PopupMenu有一个私有变量mPopup，类型是MenuPopupHelper，有一个方法setForceShowIcon，用于显示icon
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[] { boolean.class };
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w(TAG, "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();

        //调整popupMenu的位置偏移量
        try {
            Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
            fListPopup.setAccessible(true);
            Object listPopup = fListPopup.get(menuHelper);
            argTypes = new Class[] { int.class };
            Class listPopupClass = listPopup.getClass();

            // Get the width of the popup window
            int width = (Integer) listPopupClass.getDeclaredMethod("getWidth").invoke(listPopup);

            // Invoke setHorizontalOffset() with the negative width to move left by that distance
            listPopupClass.getDeclaredMethod("setHorizontalOffset", argTypes).invoke(listPopup, -width+100);

            // Invoke show() to update the window's position
            listPopupClass.getDeclaredMethod("show").invoke(listPopup);
        } catch (Exception e) {
            // Again, an exception here indicates a programming error rather than an exceptional condition
            // at runtime
            Log.w(TAG, "Unable to force offset", e);
        }
        FadingOutOtherItems(popupMenu);
    }

    private void FadingOutOtherItems(PopupMenu popupMenu) {
        // Dim out all the other list items if they exist
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        for (int i=firstVisibleItemPosition;i<=lastVisibleItemPosition;i++){
            if (i==mHolder.getLayoutPosition()) continue;
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) mRecyclerView.findViewHolderForLayoutPosition(i);
            View itemView = viewHolder.getItemView();
            itemView.clearAnimation();
            itemView.startAnimation(mFadeOut);
        }

        // Make sure to bring them back to normal after the menu is gone
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                for (int i=firstVisibleItemPosition;i<=lastVisibleItemPosition;i++){
                    if (i== mHolder.getLayoutPosition()) continue;
                    RecyclerViewHolder viewHolder = (RecyclerViewHolder) mRecyclerView.findViewHolderForLayoutPosition(i);
                    View itemView = viewHolder.getItemView();
                    viewHolder.getLayoutPosition();
                    itemView.clearAnimation();
                    itemView.startAnimation(mFadeIn);
                }
            }
        });
    }
}
