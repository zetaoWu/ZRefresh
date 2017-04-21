package com.nick.wzt.likerefresh.list;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.nick.wzt.likerefresh.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by apple on 2017/4/18.
 */

public class ListViewActivity extends AppCompatActivity implements RefListhview.onRefreshListener {

    private ArrayList mDatas;
    private ArrayAdapter mAdapter;
    private Handler mHandler = new Handler();
    private RefListhview mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_listview_ref);

        mListView = (RefListhview) findViewById(R.id.lv);

        String[] data = new String[]{"ListView-Refresh", "a", "b", "c", "d",
                "e", "f", "g", "h", "i",
                "j", "k", "l", "m", "n", "o", "p", "q", "r", "s"};
        mDatas = new ArrayList<String>(Arrays.asList(data));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDatas);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    mDatas.add(0, "new data");
                    if (mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mListView.setOnRefreshComplete();
                                mAdapter.notifyDataSetChanged();
                                mListView.setSelection(0);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
