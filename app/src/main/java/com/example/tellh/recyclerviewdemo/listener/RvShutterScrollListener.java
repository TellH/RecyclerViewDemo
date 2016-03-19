package com.example.tellh.recyclerviewdemo.listener;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Transformation;

import com.example.tellh.recyclerviewdemo.adapter.RecyclerViewHolder;

public class RvShutterScrollListener extends RecyclerView.OnScrollListener {

    private boolean hasSetTouchListener;
    private boolean isAnimStart;
    private MyAnimation animation=new MyAnimation();
    private static final int TOP = 705;
    private static final int BOTTOM = 661;
    private int newState;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        setRvOnTouchListener(recyclerView);
    }

    private void setRvOnTouchListener(@NonNull final RecyclerView recyclerView) {
        if (!hasSetTouchListener){
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                float startY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            stopShutterAnim(recyclerView);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (isReachTop(recyclerView)&&event.getRawY()-startY>=10&&newState==RecyclerView.SCROLL_STATE_DRAGGING){
                                startShutterAnim(recyclerView,TOP);
                            }else if (isReachBottom(recyclerView)&&startY-event.getRawY()>=10&&newState==RecyclerView.SCROLL_STATE_DRAGGING){
                                startShutterAnim(recyclerView,BOTTOM);
                            }
                            break;
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getRawY();
                            break;
                    }
                    return false;
                }
            });
            hasSetTouchListener=true;
        }
    }

    private void stopShutterAnim(RecyclerView recyclerView) {
        if (!isAnimStart)
            return;
        MyAnimation animation=new MyAnimation();
        animation.setRotateX(0);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        for (int i=firstVisibleItemPosition;i<=lastVisibleItemPosition;i++){
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
            View itemView = viewHolder.getItemView();
            itemView.startAnimation(animation);
        }
        isAnimStart=false;
    }

    private void startShutterAnim(RecyclerView recyclerView,int type) {
        isAnimStart=true;
        if (type==TOP)
            animation.setRotateX(-8);
        else animation.setRotateX(8);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        for (int i=firstVisibleItemPosition;i<=lastVisibleItemPosition;i++){
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
            View itemView = viewHolder.getItemView();
            itemView.startAnimation(animation);
        }
    }

    public boolean isReachTop(RecyclerView recyclerView) {
        return ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition()
                    == 0;
    }

    public boolean isReachBottom(RecyclerView recyclerView) {
        return ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()
                    == recyclerView.getAdapter().getItemCount() - 1;
    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        this.newState=newState;
    }

    class MyAnimation extends Animation {
        private int mCenterWidth;
        private int mCenterHeight;
        private Camera mCamera = new Camera();
        private float mRotateX = 0.0f;

        @Override
        public void initialize(int width,
                               int height,
                               int parentWidth,
                               int parentHeight) {

            super.initialize(width, height, parentWidth, parentHeight);
            // 设置默认时长
            setDuration(500);
            // 动画结束后保留状态
            setFillAfter(true);
            // 设置默认插值器
            setInterpolator(new BounceInterpolator());
            mCenterWidth = width / 2;
            mCenterHeight = height / 2;
        }

        // 暴露接口-设置旋转角度
        public void setRotateX(float rotateX) {
            mRotateX = rotateX;
        }

        @Override
        protected void applyTransformation(
                float interpolatedTime,
                Transformation t) {
            if (interpolatedTime==0) {
                final Matrix matrix = t.getMatrix();
                mCamera.save();
                // 使用Camera设置旋转的角度
                mCamera.rotateX(mRotateX);
                // 将旋转变换作用到matrix上
                mCamera.getMatrix(matrix);
                mCamera.restore();
                // 通过pre方法设置矩阵作用前的偏移量来改变旋转中心。简而言之就是边转边改变旋转中心
                matrix.preTranslate(-mCenterWidth, -mCenterHeight);
                matrix.postTranslate(mCenterWidth, mCenterHeight);
            }
        }
    }
}