package com.chinatsp.shapebutton.shapeButton.ripple;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.chinatsp.shapebutton.shapeButton.common.Carbon;

public interface RippleDrawable{

    Drawable getBackground();

    enum Style {
        Over,Background, Borderless
    }

    boolean setState(int[] stateSet);

    void draw(Canvas canvas);
    Style getStyle();

    boolean isHotspotEnabled();

    void setHotspotEnabled(boolean useHotspot);

    void setBounds(int left, int top, int right, int bottom);

    void setBounds(Rect bounds);

    void setHotspot(float x, float y);

    boolean isStateful();

    void setCallback(Drawable.Callback cb);

    ColorStateList getColor();

    void setRadius(int radius);

    int getRadius();

    static RippleDrawable create(ColorStateList color, Style style, View view, boolean useHotspot, int radius) {
        RippleDrawable rippleDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rippleDrawable = new RippleDrawableMarshmallow(color, style == Style.Background ? view.getBackground() : null, style);
        } else if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            rippleDrawable = new RippleDrawableLollipop(color, style == Style.Background ? view.getBackground() : null, style);
        }
        rippleDrawable.setCallback(view);
        rippleDrawable.setHotspotEnabled(useHotspot);
        rippleDrawable.setRadius(radius);
        return rippleDrawable;
    }

    static RippleDrawable create(ColorStateList color, Style style, View view, Drawable background, boolean useHotspot, int radius) {
        RippleDrawable rippleDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rippleDrawable = new RippleDrawableMarshmallow(color, background, style);
        } else if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            rippleDrawable = new RippleDrawableLollipop(color, background, style);
        }
        rippleDrawable.setCallback(view);
        rippleDrawable.setHotspotEnabled(useHotspot);
        rippleDrawable.setRadius(radius);
        return rippleDrawable;
    }
}
