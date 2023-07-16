package com.example.budget_management.Other;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
            // Kiểm tra nếu sự kiện chạm là từ EditText, không xử lý và trả về false
            if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
                View view = findViewAtPosition(ev.getRawX(), ev.getRawY());
                if (view instanceof EditText) {
                    return false;
                }
            }
            touchListener.onTouch(this, ev);
        }
        return false;
    }

    private View findViewAtPosition(float x, float y) {
        Rect rect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.getGlobalVisibleRect(rect);
            if (rect.contains((int) x, (int) y)) {
                return child;
            }
        }
        return null;
    }
}
