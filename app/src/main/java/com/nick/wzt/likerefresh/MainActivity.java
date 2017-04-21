package com.nick.wzt.likerefresh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.nick.wzt.likerefresh.list.ListViewActivity;
import com.nick.wzt.likerefresh.list.RefListhview;
import com.nick.wzt.likerefresh.recycle.RecycleViewActivity;
import com.nick.wzt.likerefresh.scroll.ScrollViewActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RefListhview mListView;
    private List<String> mDatas;
    private ArrayAdapter<String> mAdapter;
    private Handler mHandler = new Handler();
    private Button mBtLsRef;
    private Button mBtRcvRef;
    private Button mBtSvRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initListener();
    }

    private void initUI() {
        mBtLsRef = (Button) findViewById(R.id.bt_ls_ref);
        mBtRcvRef = (Button) findViewById(R.id.bt_rcv_ref);
        mBtSvRef = (Button) findViewById(R.id.bt_sv_ref);
    }

    private void initListener() {
        mBtLsRef.setOnClickListener(this);
        mBtRcvRef.setOnClickListener(this);
        mBtSvRef.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ls_ref:
                startActivity(new Intent(MainActivity.this, ListViewActivity.class));
                break;
            case R.id.bt_rcv_ref:
                startActivity(new Intent(MainActivity.this, RecycleViewActivity.class));
                break;
            case R.id.bt_sv_ref:
                startActivity(new Intent(MainActivity.this, ScrollViewActivity.class));
                break;
        }
    }
}
