package com.nick.wzt.likerefresh.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nick.wzt.likerefresh.R;


/**
 * Created by apple on 2017/4/18.
 */

public abstract class BaseRefreshAdapter extends RecyclerView.Adapter {
    private final int TYPE_HEAD = 0;
    private final int TYPE_ITEM = 1;
    private static int headViewMeasuredHeight;
    private View headerView;
    private final int STOP_ANIM = 0;
    private final int START_ANIM = 1;

    private Animation backInto;
    private Animation wheelRotote;
    private Animation riderRun;
    private Animation sunRotate;
    private Animation backOut;

    private ImageView rider;
    private ImageView sun;
    private ImageView left_wheel;
    private ImageView right_wheel;
    private ImageView ivBack1;
    private ImageView ivBack2;
    private RelativeLayout car;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEAD:
                headerView = View
                        .inflate(parent.getContext(), R.layout.layout_pull_head, null);
                viewHolder = new RefreshHeaderViewHolder(headerView);
                break;
            case TYPE_ITEM:
                viewHolder = onCreateItemViewHolder(parent);
                break;
        }
        return viewHolder;
    }

    public abstract RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_HEAD:
                prepareHeaderView(holder);
                break;
            case TYPE_ITEM:
                onBindItemViewHolder(holder, position - 1);
                break;
        }
    }

    private void prepareHeaderView(RecyclerView.ViewHolder holder) {

    }

    protected abstract void onBindItemViewHolder(RecyclerView.ViewHolder holder, int i);

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public int getHeaderRefreshHeight() {
        return headViewMeasuredHeight;
    }

    class RefreshHeaderViewHolder extends RecyclerView.ViewHolder {
        public RefreshHeaderViewHolder(final View headView) {
            super(headView);

            rider = (ImageView) headView.findViewById(R.id.rider);
            sun = (ImageView) headView.findViewById(R.id.sun);
            left_wheel = (ImageView) headView.findViewById(R.id.left_wheel);
            right_wheel = (ImageView) headView.findViewById(R.id.right_wheel);
            ivBack1 = (ImageView) headView.findViewById(R.id.iv_back1);
            ivBack2 = (ImageView) headView.findViewById(R.id.iv_back2);
            car = (RelativeLayout) headView.findViewById(R.id.main);


            headView.post(new Runnable() {
                @Override
                public void run() {
                    headViewMeasuredHeight = headView.getMeasuredHeight();
                    setHeaderPadding(-headViewMeasuredHeight);
                }
            });
        }
    }

    public void setHeaderPadding(int toLocation) {
        if (headerView != null) {
            headerView.setPadding(0, toLocation, 0, 0);
        }
    }

    public void setHeadState(Context context, int state) {

        if (backInto == null) {
            backInto = AnimationUtils.loadAnimation(context, R.anim.back_into);
            backOut = AnimationUtils.loadAnimation(context, R.anim.back_out);

            wheelRotote = AnimationUtils.loadAnimation(context, R.anim.wheel_rotate);
            sunRotate = AnimationUtils.loadAnimation(context, R.anim.sun_rotate);
            riderRun = AnimationUtils.loadAnimation(context, R.anim.rider_run);

            LinearInterpolator lir = new LinearInterpolator();
            backInto.setInterpolator(lir);
            backOut.setInterpolator(lir);
            wheelRotote.setInterpolator(lir);
            sunRotate.setInterpolator(lir);
            riderRun.setInterpolator(lir);
        }

        switch (state) {
            case START_ANIM:
                startAnim();
                break;
            case STOP_ANIM:
                stopAnim();
                break;
        }
    }

    /**
     * 开启动画
     */
    public void startAnim() {
        ivBack1.startAnimation(backInto);
        ivBack2.startAnimation(backOut);
        sun.startAnimation(sunRotate);
        left_wheel.startAnimation(wheelRotote);
        right_wheel.startAnimation(wheelRotote);
        car.startAnimation(riderRun);
    }

    /**
     * 关闭动画
     */
    public void stopAnim() {
        ivBack1.clearAnimation();
        ivBack2.clearAnimation();
        sun.clearAnimation();
        left_wheel.clearAnimation();
        right_wheel.clearAnimation();
        car.clearAnimation();
    }
}
