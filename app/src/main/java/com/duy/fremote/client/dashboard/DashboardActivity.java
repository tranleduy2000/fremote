package com.duy.fremote.client.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.duy.fremote.R;
import com.duy.fremote.views.CustomViewPager;

public class DashboardActivity extends AppCompatActivity {
    private CustomViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initView();
    }

    private void initView() {
        mViewPager = findViewById(R.id.view_pager);
        DashboardPagerAdapter adapter = new DashboardPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(adapter.getCount());
    }
}
