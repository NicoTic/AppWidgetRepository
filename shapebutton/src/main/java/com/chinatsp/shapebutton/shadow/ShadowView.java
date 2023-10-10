package com.chinatsp.shapebutton.shadow;

import android.content.res.ColorStateList;
import android.graphics.Canvas;

/**
 * 阴影相关
 */
public interface ShadowView {
    /**
     * The elevation value. There are useful values of elevation defined in xml as
     * carbon_elevationFlat, carbon_elevationLow, carbon_elevationMedium, carbon_elevationHigh,
     * carbon_elevationMax.
     */
    float getElevation();

    void setElevation(float elevation);

    float getTranslationZ();

    void setTranslationZ(float translationZ);

    ColorStateList getElevationShadowColor();

    void setElevationShadowColor(ColorStateList color);

    void setElevationShadowColor(int color);

    int getOutlineAmbientShadowColor();

    void setOutlineAmbientShadowColor(int color);

    void setOutlineAmbientShadowColor(ColorStateList color);

    int getOutlineSpotShadowColor();

    void setOutlineSpotShadowColor(int color);

    void setOutlineSpotShadowColor(ColorStateList color);

    boolean hasShadow();

    void drawShadow(Canvas canvas);

}
