package com.duy.fremote.utils;

import android.util.Log;

import com.duy.fremote.BuildConfig;

public class DLog {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static void d(String tag, String m) {
        Log.d(tag, m);
    }

    public static void e(String tag, String s, Exception e) {
        Log.e(tag, s, e);
    }
}
