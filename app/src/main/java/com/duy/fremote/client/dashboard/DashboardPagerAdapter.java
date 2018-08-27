package com.duy.fremote.client.dashboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.duy.fremote.client.dashboard.devices.DevicesFragment;
import com.duy.fremote.client.dashboard.scenes.ScenesFragment;

public class DashboardPagerAdapter extends FragmentPagerAdapter {
    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ScenesFragment.newInstance();
            case 1:
                return DevicesFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
