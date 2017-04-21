package com.nick.wzt.likerefresh.recycle;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nick.wzt.likerefresh.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by apple on 2017/4/18.
 *
 * RecycleView 注意点：
 * 1. 需要继承BaseRefreshAdapter
 *      实现onCreateItemViewHolder onBindItemViewHolder 和实现RecycleView.Adapter基本类似
 *
 * 3. 代码中mRvRef.scrollToPosition(0); 不可少
 *
 *
 */

public class RecycleViewActivity extends AppCompatActivity implements RefRecycleView.onRvRefreshListener {

    private RefRecycleView mRvRef;
    private ArrayList<String> mDatas;
    private Handler mHandler = new Handler();
    private RecycleViewAdapter mRvAdapter;
    private LinearLayoutManager mLinearManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_rlv_ref);
        mRvRef = (RefRecycleView) findViewById(R.id.rv_ref);
        mLinearManager = new LinearLayoutManager(this);
        mLinearManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRef.setLayoutManager(mLinearManager);


        initData();
    }

    private void initData() {

        String[] data = new String[]{"RecycleView-Refresh", "a", "b", "c", "d",
                "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n", "o", "p", "q", "r", "s"};
        mDatas = new ArrayList<String>(Arrays.asList(data));

        mRvAdapter = new RecycleViewAdapter();
        mRvRef.setAdapter(mRvAdapter);
        mRvRef.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    mDatas.add(0, "new data");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRvRef.setOnRefreshComplete();
                            mRvAdapter.notifyDataSetChanged();
                            mRvRef.scrollToPosition(0);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    class RecycleViewAdapter extends BaseRefreshAdapter {

        @Override
        public RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    RecycleViewActivity.this).inflate(R.layout.item_recycleview, parent,
                    false));
            return holder;
        }

        @Override
        protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int i) {
            ((MyViewHolder) holder).tv.setText(mDatas.get(i));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.tv_txt);
            }
        }
    }
}
