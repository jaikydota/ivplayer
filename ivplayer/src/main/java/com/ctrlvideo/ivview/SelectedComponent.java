package com.ctrlvideo.ivview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 选项选择类组件
 */
public class SelectedComponent extends RelativeLayout {


    public SelectedComponent(Context context) {
        this(context, null);
    }

    public SelectedComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

    }
}
