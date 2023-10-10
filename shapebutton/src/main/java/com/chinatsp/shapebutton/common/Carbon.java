package com.chinatsp.shapebutton.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.chinatsp.shapebutton.ripple.RippleDrawable;
import com.chinatsp.shapebutton.ripple.RippleView;
import com.chinatsp.shapebutton.shadow.ShadowView;
import com.chinatsp.shapebutton.shape.ShapeModelView;
import com.chinatsp.shapebutton.stroke.StrokeView;
import com.chinatsp.shapebutton.stateAnimator.StateAnimatorView;
import com.chinatsp.shapebutton.utils.AnimUtils;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

public class Carbon {

    public static final boolean IS_LOLLIPOP_OR_HIGHER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    public static final boolean IS_PIE_OR_HIGHER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;

    public static PorterDuffXfermode CLEAR_MODE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private Carbon() {
    }

    public static void initElevation(ShadowView view, TypedArray a, int[] ids) {
        int carbon_elevation = ids[0];
        int carbon_shadowColor = ids[1];
        int carbon_ambientShadowColor = ids[2];
        int carbon_spotShadowColor = ids[3];

        float elevation = a.getDimension(carbon_elevation, 0);
        view.setElevation(elevation);
        if (elevation > 0)
            AnimUtils.setupElevationAnimator(((StateAnimatorView) view).getStateAnimator(), view);
        ColorStateList shadowColor = a.getColorStateList(carbon_shadowColor);
        view.setElevationShadowColor(shadowColor != null ? shadowColor.withAlpha(255) : null);
        if (a.hasValue(carbon_ambientShadowColor)) {
            ColorStateList ambientShadowColor = a.getColorStateList(carbon_ambientShadowColor);
            view.setOutlineAmbientShadowColor(ambientShadowColor != null ? ambientShadowColor.withAlpha(255) : null);
        }
        if (a.hasValue(carbon_spotShadowColor)) {
            ColorStateList spotShadowColor = a.getColorStateList(carbon_spotShadowColor);
            view.setOutlineSpotShadowColor(spotShadowColor != null ? spotShadowColor.withAlpha(255) : null);
        }
    }

    public static void initCornerCutRadius(ShapeModelView shapeModelView, TypedArray a, int[] ids) {
        int carbon_cornerRadiusTopStart = ids[0];
        int carbon_cornerRadiusTopEnd = ids[1];
        int carbon_cornerRadiusBottomStart = ids[2];
        int carbon_cornerRadiusBottomEnd = ids[3];
        int carbon_cornerRadius = ids[4];
        int carbon_cornerCutTopStart = ids[5];
        int carbon_cornerCutTopEnd = ids[6];
        int carbon_cornerCutBottomStart = ids[7];
        int carbon_cornerCutBottomEnd = ids[8];
        int carbon_cornerCut = ids[9];

        float cornerRadius = Math.max(a.getDimension(carbon_cornerRadius, 0), 0.1f);
        float cornerRadiusTopStart = a.getDimension(carbon_cornerRadiusTopStart, cornerRadius);
        float cornerRadiusTopEnd = a.getDimension(carbon_cornerRadiusTopEnd, cornerRadius);
        float cornerRadiusBottomStart = a.getDimension(carbon_cornerRadiusBottomStart, cornerRadius);
        float cornerRadiusBottomEnd = a.getDimension(carbon_cornerRadiusBottomEnd, cornerRadius);
        float cornerCut = a.getDimension(carbon_cornerCut, 0);
        float cornerCutTopStart = a.getDimension(carbon_cornerCutTopStart, cornerCut);
        float cornerCutTopEnd = a.getDimension(carbon_cornerCutTopEnd, cornerCut);
        float cornerCutBottomStart = a.getDimension(carbon_cornerCutBottomStart, cornerCut);
        float cornerCutBottomEnd = a.getDimension(carbon_cornerCutBottomEnd, cornerCut);
        ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                .setTopLeftCorner(cornerCutTopStart >= cornerRadiusTopStart ? new CutCornerTreatment(cornerCutTopStart) : new RoundedCornerTreatment(cornerRadiusTopStart))
                .setTopRightCorner(cornerCutTopEnd >= cornerRadiusTopEnd ? new CutCornerTreatment(cornerCutTopEnd) : new RoundedCornerTreatment(cornerRadiusTopEnd))
                .setBottomLeftCorner(cornerCutBottomStart >= cornerRadiusBottomStart ? new CutCornerTreatment(cornerCutBottomStart) : new RoundedCornerTreatment(cornerRadiusBottomStart))
                .setBottomRightCorner(cornerCutBottomEnd >= cornerRadiusBottomEnd ? new CutCornerTreatment(cornerCutBottomEnd) : new RoundedCornerTreatment(cornerRadiusBottomEnd))
                .build();
        shapeModelView.setShapeModel(model);
    }

    public static boolean isShapeRect(ShapeAppearanceModel model, RectF bounds) {
        return model.getTopLeftCornerSize().getCornerSize(bounds) <= 0.2f &&
                model.getTopRightCornerSize().getCornerSize(bounds) <= 0.2f &&
                model.getBottomLeftCornerSize().getCornerSize(bounds) <= 0.2f &&
                model.getBottomRightCornerSize().getCornerSize(bounds) <= 0.2f;
    }

    public static void initRippleDrawable(RippleView rippleView, TypedArray a, int[] ids) {
        int carbon_rippleColor = ids[0];
        int carbon_rippleStyle = ids[1];
        int carbon_rippleHotspot = ids[2];
        int carbon_rippleRadius = ids[3];

        View view = (View) rippleView;
        if (view.isInEditMode())
            return;

        ColorStateList color = a.getColorStateList(carbon_rippleColor);

        if (color != null) {
            RippleDrawable.Style style = RippleDrawable.Style.values()[a.getInt(carbon_rippleStyle, RippleDrawable.Style.Background.ordinal())];
            boolean useHotspot = a.getBoolean(carbon_rippleHotspot, true);
            int radius = (int) a.getDimension(carbon_rippleRadius, -1);

            rippleView.setRippleDrawable(RippleDrawable.create(color, style, view, useHotspot, radius));
        }
    }

    public static int getThemeColor(Context context, int attr) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId != 0 ? context.getResources().getColor(typedValue.resourceId) : typedValue.data;
    }

    public static Drawable getDrawable(View view, TypedArray a, int attr, int defaultValue) {
        if (!view.isInEditMode()) {
            int resId = a.getResourceId(attr, 0);
            if (resId != 0) {
                return ContextCompat.getDrawable(view.getContext(), resId);
            }
        } else {
            try {
                return a.getDrawable(attr);
            } catch (Exception e) {
                return view.getResources().getDrawable(defaultValue);
            }
        }

        return null;
    }



    public static void initStroke(StrokeView strokeView, TypedArray a, int[] ids) {
        int carbon_stroke = ids[0];
        int carbon_strokeWidth = ids[1];

        View view = (View) strokeView;
        ColorStateList color =  a.getColorStateList(carbon_stroke);

        if (color != null)
            strokeView.setStroke(color);
        strokeView.setStrokeWidth(a.getDimension(carbon_strokeWidth, 0));
    }

}
