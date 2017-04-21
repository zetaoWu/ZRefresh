package com.nick.wzt.likerefresh.scroll;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.nick.wzt.likerefresh.R;

/**
 * Created by apple on 2017/4/20.
 */

public class ScrollViewActivity extends AppCompatActivity {

    private View mSvContent;
    private RefScrollView mRefSv;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_scrollview_ref);
        mRefSv = (RefScrollView) findViewById(R.id.ref_sv);

        //1. 初始化SCrollview contentView
        initContentView();

        //2. 接口
        mRefSv.setOnRefreshListener(new RefScrollView.onRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            //模拟耗时操作
                            Thread.sleep(3000);
                            if (mHandler != null) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRefSv.setOnRefreshComplete();
                                        Toast.makeText(ScrollViewActivity.this, "刷新完毕", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private void initContentView() {
        //3. layout_scroll_content  需要添加的内容
        mSvContent = LayoutInflater.from(this).inflate(R.layout.layout_scroll_content, null);
        if (mSvContent != null) {
            mRefSv.setContentView(mSvContent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
