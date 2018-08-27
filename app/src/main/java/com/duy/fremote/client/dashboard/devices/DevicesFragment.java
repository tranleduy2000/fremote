package com.duy.fremote.client.dashboard.devices;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class DevicesFragment extends Fragment {
    public static DevicesFragment newInstance() {

        Bundle args = new Bundle();

        DevicesFragment fragment = new DevicesFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
