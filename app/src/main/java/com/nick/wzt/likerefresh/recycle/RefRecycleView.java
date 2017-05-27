package com.nick.wzt.likerefresh.recycle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by apple on 2017/4/17.
 */

public class RefRecycleView extends RecyclerView {
    private static final int DONE = 0;      //刷新完毕状态
    private static final int PULL_TO_REFRESH = 1;   //下拉刷新状态
    private static final int RELEASE_TO_REFRESH = 2;    //释放状态
    private static final int REFRESHING = 3;   //正在刷新
    private static final int RATIO = 3;
    boolean hasInit = false;
    private int state = DONE;  //状态值
    private boolean isEnd = true;  //是否结束
    private boolean isRefreable;    //是否刷新
    private int currentDist = 0;
    private boolean isRecord;   //是否记录
    private View headView;
    private int headViewHeight;
    private ImageView rider;
    private ImageView sun;
    private ImageView left_wheel;
    private ImageView right_wheel;
    private ImageView ivBack1;
    private ImageView ivBack2;
    private Animation backInto;
    private Animation wheelRotote;
    private Animation riderRun;
    private Animation sunRotate;
    private Animation backOut;
    private int mFirstVisibleItem;
    private float startY;   //开始Y坐标
    private RelativeLayout car;
    private float offsetY;
    private BaseRefreshAdapter mAdapter;
    private ValueAnimator animator_hide_header;

    public RefRecycleView(Context context) {
        this(context, null);
    }

    public RefRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefRecycleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Adapter adapter = getAdapter();
        if (!(adapter instanceof BaseRefreshAdapter)) {
            throw new IllegalArgumentException("the adapter must extents BaseRefreshRecyclerViewAdapter");
        }
        mAdapter = (BaseRefreshAdapter) adapter;

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                headViewHeight = mAdapter.getHeaderRefreshHeight();

                state = DONE;

                ///如果当前是在listview顶部并且没有记录y坐标
                if (mFirstVisibleItem == 0 && !isRecord) {
                    //将isRecord置为true，说明现在已记录y坐标
                    isRecord = true;
                    //将当前y坐标赋值给startY起始y坐标
                    startY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                //再起判断一下是否为顶部并且没有记录y坐标
                if (mFirstVisibleItem == 0 && !isRecord) {
                    isRecord = true;
                    startY = ev.getY();
                }

                float tempY = ev.getY();

                if (state != REFRESHING && isRecord) {
                    //再次得到y坐标，用来和startY相减来计算offsetY位移值
                    offsetY = tempY - startY;

                    //当它松手释放状态的时候
                    if (state == RELEASE_TO_REFRESH && isRecord) {
                        this.scrollToPosition(0);
                        //当前滑动的距离小于 headViewHeight
                        if (offsetY / RATIO - headViewHeight < 0) {
                            //将状态设置为下啦刷新状态
                            state = PULL_TO_REFRESH;
                        } else if (offsetY <= 0) {
                            //将状态变为done
                            state = DONE;

                            mAdapter.setHeaderPadding(-headViewHeight);
                            //根据状态改变headerView，主要是更新动画和文字等信息
                            changeHeaderByState(state);
                        }
                    }

                    //当它正在下滑的时候
                    if (state == PULL_TO_REFRESH && isRecord) {
                        this.scrollToPosition(0);
                        //滑动的距离大于高度
                        if (offsetY / RATIO - headViewHeight >= 0) {
                            state = RELEASE_TO_REFRESH;
                            changeHeaderByState(state);
                        } else if (offsetY <= 0) {
                            state = DONE;
                            changeHeaderByState(state);
                            mAdapter.setHeaderPadding(-headViewHeight);
                        }
                    }

                    //如果当前状态为done 并且已经记录y坐标
                    System.out.println("state====" + state + "----isRecord==" + isRecord);
                    if (state == DONE && isRecord) {
                        if (offsetY >= 0) {
                            //状态为下啦
                            state = PULL_TO_REFRESH;
                            changeHeaderByState(state);
                        }
                    }

                    if (state == PULL_TO_REFRESH) {
                        mAdapter.setHeaderPadding((int) (offsetY / RATIO) - headViewHeight);
                    }

                    //如果为放开状态
                    if (state == RELEASE_TO_REFRESH) {
                        //回弹的距离
                        mAdapter.setHeaderPadding((int) (offsetY / RATIO) - headViewHeight);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                if (state == PULL_TO_REFRESH && (int) (offsetY / RATIO) > 10) {
                    //平滑的隐藏headerView
                    scrollAnimaionRefresh((int) (offsetY / RATIO - headViewHeight), -headViewHeight, 300);
                }

                if (state == RELEASE_TO_REFRESH) {

                    scrollAnimaionRefresh((int) (offsetY / RATIO) - headViewHeight, 0, 300);

                    //将当前状态设置为正在刷新
                    state = REFRESHING;
                    //回调接口的onRefresh方法

                    listener.onRefresh();


                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setLayoutFrozen(true);
                        }
                    }, 300);
                    //在动画完成后禁止滑动  相比较listview  scrollview不同

                    isEnd = false;

                    //根据状态改变headerView
                    changeHeaderByState(state);
                }
                //这一套手势执行完，一定别忘了将记录y坐标的isRecord改为false，以便于下一次手势的执行
                isRecord = false;
                break;
        }

        return super.onTouchEvent(ev);
    }

    private Handler mhandler = new Handler();

    private void scrollAnimaionRefresh(int curLocation, int toLocation, int duration) {
        ValueAnimator animator_relase_torefresh = ValueAnimator.ofInt(curLocation, toLocation);
        animator_relase_torefresh.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAdapter.setHeaderPadding((Integer) valueAnimator.getAnimatedValue());
            }
        });
        animator_relase_torefresh.setDuration(duration);
        animator_relase_torefresh.start();
    }


    /**
     * 根据状态改变headerView的动画和文字显示
     *
     * @param state
     */
    private void changeHeaderByState(int state) {
        switch (state) {
            case DONE://如果的隐藏的状态
                //改变head状态 关闭动画 0
                mAdapter.setHeadState(getContext(), 0);
                break;
            case RELEASE_TO_REFRESH://当前状态为放开刷新
                break;
            case PULL_TO_REFRESH://当前状态为下拉刷新
                //改变head状态 开始动画 1
                mAdapter.setHeadState(getContext(), 1);
                break;
            case REFRESHING://当前状态为正在刷新
                break;
            default:
                break;
        }
    }


    private onRvRefreshListener listener;

    public void setOnRefreshListener(onRvRefreshListener listener) {
        isRefreable = true;
        this.listener = listener;
    }

    public interface onRvRefreshListener {
        void onRefresh();
    }

    /**
     * 刷新完毕，从主线程发送过来，并且改变headerView的状态和文字动画信息
     */

    public void setOnRefreshComplete() {
        //一定要将isEnd设置为true，以便于下次的下拉刷新
        isEnd = true;
        state = DONE;

        scrollAnimaionRefresh(0, -headViewHeight, 300);
        //允许滑动
        setLayoutFrozen(false);

        changeHeaderByState(state);
    }
}
