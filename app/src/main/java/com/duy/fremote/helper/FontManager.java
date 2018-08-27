package com.duy.fremote.helper;

import android.content.Context;
import android.graphics.Typeface;

import java.util.WeakHashMap;

public class FontManager {

    private static final WeakHashMap<String, Typeface> sCached = new WeakHashMap<>();

    public static Typeface getFontFromAssets(Context context, String assetsPath) {
        if (sCached.containsKey(assetsPath)) {
            return sCached.get(assetsPath);
        }
        try {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), assetsPath);
            sCached.put(assetsPath, typeface);
            return typeface;
        } catch (Exception e) {
            return Typeface.MONOSPACE;
        }
    }
}
