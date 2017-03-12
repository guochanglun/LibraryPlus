package com.gcl.library.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gcl.library.activity.R;

/**
 * Created by gcl on 2017/3/5.
 */

public class LoadingDialog extends Dialog {

    private Context mContext;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.FullHeightDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LinearLayout.inflate(mContext, R.layout.loading_dialog, null);
        setContentView(view);

        Window window = getWindow();
        WindowManager windowManager = window.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = lp.width = (int) (Math.min(rect.width(), rect.height()) * 0.5);
        window.setAttributes(lp);
        window.getDecorView().getBackground().setAlpha(0);
    }
}
