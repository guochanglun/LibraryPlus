package com.gcl.library.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by gcl on 2017/3/8.
 */

public class PhoneWindowUtil {

    private static Rect rect = null;

    public static Rect getWindowRect(Activity context) {
        if (rect == null) {
            rect = new Rect();
            WindowManager manager = context.getWindowManager();
            Display display = manager.getDefaultDisplay();
            display.getRectSize(rect);
        }
        return rect;

    }
}
