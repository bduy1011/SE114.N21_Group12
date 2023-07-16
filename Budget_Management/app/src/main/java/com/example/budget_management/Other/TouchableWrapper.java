package com.example.budget_management.Other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {
    private OnTouchListener touchListener;

    public TouchableWrapper(Context context) {
        super(context);
    }

    public TouchableWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTouchListener(OnTouchListener listener) {
        touchListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchListener != null) {
            touchListener.onTouch(this, ev);
        }
        return false;
    }
}
