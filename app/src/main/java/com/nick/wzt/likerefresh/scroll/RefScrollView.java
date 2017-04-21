package com.nick.wzt.likerefresh.scroll;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.nick.wzt.likerefresh.R;

/**
 * Created by apple on 2017/4/20.
 */


public class RefScrollView extends ScrollView {
    private static final int DONE = 0;      //刷新完毕状态
    private static final int PULL_TO_REFRESH = 1;   //下拉刷新状态
    private static final int RELEASE_TO_REFRESH = 2;    //释放状态
    private static final int REFRESHING = 3;   //正在刷新中
    private static final int RATIO = 3;  // 添加一个阻尼效果
    private boolean isEnd;  //是否结束

    private View headView;
    private View contentView;
    private int headViewHeight;
    private LinearLayout mIncludedLayout;
    private LinearLayout mInHeadLayout;
    private LinearLayout mInContentLayout;
    private float startY;
    private float tempY;
    private float offsetY;
    private boolean isRecord = false;   //是否记录
    private int state;  //状态值
    private ValueAnimator animator_hide_header;


    private ImageView rider;
    private ImageView sun;
    private ImageView left_wheel;
    private ImageView right_wheel;
    private ImageView ivBack1;
    private ImageView ivBack2;
    private RelativeLayout car;

    private Animation backInto;
    private Animation wheelRotote;
    private Animation riderRun;
    private Animation sunRotate;
    private Animation backOut;

    public RefScrollView(Context context) {
        this(context, null);
    }

    public RefScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //关闭view的OverScroll
        setOverScrollMode(OVER_SCROLL_NEVER);
        mIncludedLayout = (LinearLayout) View.inflate(context, R.layout.layout_scroll_be_inclued, null);
        mInHeadLayout = (LinearLayout) mIncludedLayout.findViewById(R.id.header_layout);
        mInContentLayout = (LinearLayout) mIncludedLayout.findViewById(R.id.content_layout);


        //1.初始化刷新头 添加头部
        initHeadUI(context);

        initAnimation(context);


        //2.初始化界面  添加具体界面  改由外部传入
//        contentView = LayoutInflater.from(context).inflate(R.layout.layout_scroll_content, this, false);
//        mBeInContentLayout.addView(contentView);

        //3.测量布局 设置顶部位置
        measureView(mInHeadLayout);
        headViewHeight = mInHeadLayout.getMeasuredHeight();
        mInHeadLayout.setPadding(0, -headViewHeight, 0, 0);

        //4.设置界面
        this.addView(mIncludedLayout);
    }

    private void initAnimation(Context context) {
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

    private void initHeadUI(Context context) {
        headView = LayoutInflater.from(context).inflate(R.layout.layout_pull_head, this, false);
        mInHeadLayout.addView(headView);
        rider = (ImageView) headView.findViewById(R.id.rider);
        sun = (ImageView) headView.findViewById(R.id.sun);
        left_wheel = (ImageView) headView.findViewById(R.id.left_wheel);
        right_wheel = (ImageView) headView.findViewById(R.id.right_wheel);
        ivBack1 = (ImageView) headView.findViewById(R.id.iv_back1);
        ivBack2 = (ImageView) headView.findViewById(R.id.iv_back2);
        car = (RelativeLayout) headView.findViewById(R.id.main);
    }

    public void setContentView(View contentView) {
        if (contentView == null) {
            return;
        }
        mInContentLayout.addView(contentView);
    }


    private void measureView(final View headView) {
        ViewGroup.LayoutParams params = headView.getLayoutParams();

        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        //根据父亲节点获取子节点的MeasureSpec  因为是match_patent 所以直接取值
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int childHeightSpec;
        int lpHeight = params.height;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        headView.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //正在刷新状态  不让他滑动
                if (!isEnd) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ///如果当前是在listview顶部并且没有记录y坐标
                if (isTop() && !isRecord) {
                    //将isRecord置为true，说明现在已记录y坐标
                    isRecord = true;
                    //将当前y坐标赋值给startY起始y坐标
                    startY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:

                //再起判断一下是否为listview顶部并且没有记录y坐标
                if (isTop() && !isRecord) {
                    isRecord = true;
                    startY = ev.getY();
                }

                tempY = ev.getY();

                if (state != REFRESHING && isRecord) {
                    offsetY = tempY - startY;

                    // 放开刷新状态，
                    if (state == RELEASE_TO_REFRESH && isRecord) {
                        //如果当前滑动的距离小于headerView的总高度
                        if (offsetY / RATIO - headViewHeight < 0) {
                            //将状态置为下拉刷新状态
                            state = PULL_TO_REFRESH;
                            //根据状态改变headerView，主要是更新动画和文字等信息
//                                    如果当前y的位移值小于0，即为headerView隐藏了
                        } else if (offsetY <= 0) {
//                                    将状态变为done
                            state = DONE;
                            // TODO stopAnim();

                            //根据状态改变headerView，主要是更新动画和文字等信息
                            changeHeaderByState(state);
                            mInHeadLayout.setPadding(0, -headViewHeight, 0, 0);
                        }
                    }


                    //如果当前状态为下拉刷新并且已经记录y坐标
                    if (state == PULL_TO_REFRESH && isRecord) {
                        //如果下拉距离大于等于headerView的总高度  状态变为 松手刷新
                        if (offsetY / RATIO - headViewHeight >= 0) {
                            //将状态变为放开刷新
                            state = RELEASE_TO_REFRESH;
                            //根据状态改变headerView，主要是更新动画和文字等信息
                            changeHeaderByState(state);
                        } else if (offsetY <= 0) {
                            //如果当前y的位移值小于0，即为headerView隐藏了
                            //将状态变为done
                            state = DONE;
                            changeHeaderByState(state);
                            mInHeadLayout.setPadding(0, -headViewHeight, 0, 0);
                        }
                    }

                    //如果当前状态为done并且已经记录y坐标
                    if (state == DONE && isRecord) {
                        //如果位移值大于0
                        if (offsetY >= 0) {
//                                    将状态改为下拉刷新状态
                            state = PULL_TO_REFRESH;
                            changeHeaderByState(state);
                        }
                    }


                    //如果为下拉刷新状态
                    if (state == PULL_TO_REFRESH) {
                        //则改变headerView的padding来实现下拉的效果
                        //(int) (-headViewHeight + offsetY / RATIO)==0  刚好滑动出来 全部显示
                        mInHeadLayout.setPadding(0, (int) (-headViewHeight + (int) (offsetY / RATIO)), 0, 0);
                    }

                    //如果为放开刷新状态
                    if (state == RELEASE_TO_REFRESH) {
                        //改变headerView的padding值
                        mInHeadLayout.setPadding(0, (int) (offsetY / RATIO) - headViewHeight, 0, 0);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (state == PULL_TO_REFRESH && (int) (offsetY / RATIO) > 10) {
                    //平滑的隐藏headerView
                    scrollAnimaionRefresh((int) (offsetY / RATIO - headViewHeight), -headViewHeight, 500);
                }

                //如果当前状态为放开刷新
                if (state == RELEASE_TO_REFRESH) {
                    //(int) (offsetY / RATIO -headViewHeight)  滑动的距离 刚好到刷新头部完全显示
                    //平滑的滑到正好完全显示headerView
                    scrollAnimaionRefresh((int) (offsetY / RATIO) - headViewHeight, 0, 400);
                    //将当前状态设置为正在刷新
                    state = REFRESHING;
                    //回调接口的onRefresh方法
                    listener.onRefresh();
                    //禁止滑动
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


    private onRefreshListener listener;

    public void setOnRefreshListener(onRefreshListener listener) {
        this.listener = listener;
    }

    public interface onRefreshListener {
        void onRefresh();
    }


    private void scrollAnimaionRefresh(int curLocation, int toLocation, int duration) {
        ValueAnimator animator_relase_torefresh = ValueAnimator.ofInt(curLocation, toLocation);
        animator_relase_torefresh.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mInHeadLayout.setPadding(0, (Integer) valueAnimator.getAnimatedValue(), 0, 0);
            }
        });
        animator_relase_torefresh.setDuration(duration);
        animator_relase_torefresh.start();
    }


    private boolean isTop() {
        return getScrollY() <= 0 || mInHeadLayout.getLayoutParams().height > headViewHeight || mInContentLayout.getTop() > 0;
    }

    /**
     * 修改状态
     *
     * @param state
     */
    private void changeHeaderByState(int state) {
        switch (state) {
            case DONE://如果的隐藏的状态
                break;
            case RELEASE_TO_REFRESH://当前状态为放开刷新
                break;
            case PULL_TO_REFRESH://当前状态为下拉刷新
                startAnim();
                break;
            case REFRESHING://当前状态为正在刷新
                break;
            default:
                break;
        }
    }

    public void setOnRefreshComplete() {
        //一定要将isEnd设置为true，以便于下次的下拉刷新
        isEnd = true;
        state = DONE;

        scrollAnimaionRefresh(0, -headViewHeight, 300);

        changeHeaderByState(state);
    }

    /**
     * 开启动画
     */
    public void startAnim() {
        System.out.println("开启");
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
        System.out.println("关闭");
        ivBack1.clearAnimation();
        ivBack2.clearAnimation();
        sun.clearAnimation();
        left_wheel.clearAnimation();
        right_wheel.clearAnimation();
        car.clearAnimation();
    }
}
