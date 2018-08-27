package com.duy.fremote.client.dashboard.scenes;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ScenesFragment extends Fragment {
    public static final String PACKAGE_NAME = "com.duy.fremote.client.dashboard.scenes";
    public static final String ACTION_UPDATE_SCENES = PACKAGE_NAME + ".ACTION_UPDATE_SCENES";

    public static ScenesFragment newInstance() {

        Bundle args = new Bundle();

        ScenesFragment fragment = new ScenesFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
