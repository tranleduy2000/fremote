package com.duy.fremote.views;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.duy.fremote.helper.FontManager;

public class BaseButton extends AppCompatButton {

    public BaseButton(Context context) {
        super(context);
        setup(context);

    }

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public BaseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);

    }

    private void setup(Context context) {
        setTypeface(FontManager.getFontFromAssets(context, "fonts/Montserrat-Regular.ttf"));

    }
}
