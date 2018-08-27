package com.duy.fremote.client.dashboard.scenes;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ScenesFragment extends Fragment {
    public static ScenesFragment newInstance() {

        Bundle args = new Bundle();

        ScenesFragment fragment = new ScenesFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
