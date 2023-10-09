package com.chinatsp.demo1;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chinatsp.shapebutton.ripple.RippleView;
import com.chinatsp.shapebutton.ripple.RippleDrawable;
import androidx.appcompat.widget.LinearLayoutCompat;

public class LinearLayout extends LinearLayoutCompat {
    public LinearLayout(@NonNull Context context) {
        super(context);
    }

    public LinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child instanceof RippleView) {
            RippleView rippleView = (RippleView) child;
            RippleDrawable rippleDrawable = rippleView.getRippleDrawable();
            if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Borderless) {
                int saveCount = canvas.save();
                canvas.translate(child.getLeft(),child.getTop());
                canvas.concat(child.getMatrix());
                rippleDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }

        return super.drawChild(canvas, child, drawingTime);
    }
}
