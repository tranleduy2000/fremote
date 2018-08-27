package com.duy.fremote.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.angads25.toggle.LabeledSwitch;

public class BaseSwitch extends LabeledSwitch {
    public BaseSwitch(Context context) {
        super(context);
    }

    public BaseSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
