package com.chinatsp.shapebutton.stroke;

import android.content.res.ColorStateList;

/**
 * 外部轮廓相关
 */
public interface StrokeView {
    ColorStateList getStroke();

    void setStroke(ColorStateList color);
    void setStroke(int color);

    float getStrokeWidth();

    void setStrokeWidth(float strokeWidth);
}
