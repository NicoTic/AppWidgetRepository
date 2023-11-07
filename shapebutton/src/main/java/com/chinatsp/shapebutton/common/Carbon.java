package com.chinatsp.shapebutton.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.TintAwareDrawable;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.checkbox.tint.TintedView;
import com.chinatsp.shapebutton.chip.ColorStateListFactory;
import com.chinatsp.shapebutton.shapeButton.reveal.RevealView;
import com.chinatsp.shapebutton.shapeButton.ripple.RippleDrawable;
import com.chinatsp.shapebutton.shapeButton.ripple.RippleView;
import com.chinatsp.shapebutton.shapeButton.shadow.ShadowView;
import com.chinatsp.shapebutton.shapeButton.shape.ShapeModelView;
import com.chinatsp.shapebutton.shapeButton.stateAnimator.StateAnimatorView;
import com.chinatsp.shapebutton.shapeButton.stroke.StrokeView;
import com.chinatsp.shapebutton.shapeButton.utils.AnimUtils;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.security.InvalidParameterException;

public class Carbon {
    private static final long DEFAULT_REVEAL_DURATION = 200;
    private static long defaultRevealDuration = DEFAULT_REVEAL_DURATION;
    public static final boolean IS_LOLLIPOP_OR_HIGHER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    public static final boolean IS_PIE_OR_HIGHER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;

    public static PorterDuffXfermode CLEAR_MODE = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    public static long getDefaultRevealDuration() {
        return defaultRevealDuration;
    }

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

    public static Drawable getDrawable(View view, TypedArray a, int attr, int defaultValue) {
        if (!view.isInEditMode()) {
            int resId = a.getResourceId(attr, defaultValue);
            if (resId != 0) {
                return ContextCompat.getDrawable(view.getContext(), resId);
            }
        } else {
            try {
                return a.getDrawable(attr);
            } catch (Exception e) {
                return ContextCompat.getDrawable(view.getContext(),defaultValue);
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

    public static ColorStateList getDefaultColorStateList(View view, TypedArray a, int id) {
        if (!a.hasValue(id))
            return null;

        Context context = view.getContext();
        int resourceId = a.getResourceId(id, 0);

        if (resourceId == R.color.carbon_defaultIconColor) {
            return ColorStateListFactory.getInstance().makeIconPrimary1(context);
        }else if(resourceId == R.color.carbon_defaultIconCheckColor){
            return ColorStateListFactory.getInstance().makeIconPrimary(context);
        }else if(resourceId == R.color.carbon_defaultIconPressColor){
            return ColorStateListFactory.getInstance().makeIconPrimary2(context);
        }
        return null;
    }

    public static float getRevealRadius(View view, int x, int y, float radius) {
        if (radius >= 0)
            return radius;
        if (radius != RevealView.MAX_RADIUS)
            throw new InvalidParameterException("radius should be RevealView.MAX_RADIUS, 0.0f or a positive float");
        int w = Math.max(view.getWidth() - x, x);
        int h = Math.max(view.getHeight() - y, y);
        return (float) Math.sqrt(w * w + h * h);
    }

    /**
     *
     * @param drawable
     * @param tint
     * @param mode
     */
    public static void setTintListMode(Drawable drawable, ColorStateList tint, PorterDuff.Mode mode) {
        if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            drawable.setTintList(tint);
            drawable.setTintMode(mode);
        } else if (drawable instanceof TintAwareDrawable) {
            ((TintAwareDrawable) drawable).setTintList(tint);
            ((TintAwareDrawable) drawable).setTintMode(mode);
        } else {
            drawable.setColorFilter(tint == null ? null : new PorterDuffColorFilter(tint.getColorForState(drawable.getState(), tint.getDefaultColor()), mode));
        }
    }

    public static void clearTint(Drawable drawable) {
        if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            drawable.setTintList(null);
        } else if (drawable instanceof TintAwareDrawable) {
            ((TintAwareDrawable) drawable).setTintList(null);
        } else {
            drawable.setColorFilter(null);
        }
    }

    /**
     * 初始化控件选择时的图标颜色
     */
    public static void initCheckedTint(TintedView view, TypedArray a, int carbon_tint_checked,int carbon_tintMode){
        View view1 = (View) view;
        Context context = view1.getContext();
        int defaultColor = getThemeColor(context, android.R.attr.colorPrimary);
        int check_color = ContextCompat.getColor(context,R.color.carbon_orange_700);
        int checked_color = a.getColor(carbon_tint_checked, 0);
        Log.d("aaaasss","defaultColor = " + defaultColor + ",check_color = " + check_color + " ,checked_color="+checked_color);
        ColorStateList colorStateList = ColorStateListFactory.getInstance().makeIconPrimaryChecked(context,checked_color);
        if(colorStateList!=null){
            view.setTintList(colorStateList);
        }

        view.setTintMode(TintedView.modes[a.getInt(carbon_tintMode, 1)]);
    }

    /**
     * 初始化控件的图标颜色/背景颜色
     * @param view
     * @param a
     * @param ids
     */
    public static void initTint(TintedView view, TypedArray a, int[] ids) {
        int carbon_tint = ids[0];
        int carbon_tintMode = ids[1];
        int carbon_backgroundTint = ids[2];
        int carbon_backgroundTintMode = ids[3];
        int carbon_animateColorChanges = ids[4];

        if (a.hasValue(carbon_tint)) {
            ColorStateList color = getDefaultColorStateList((View) view, a, carbon_tint);

            if (color == null)
                color = a.getColorStateList(carbon_tint);
            if (color != null)
                view.setTintList(color);
        }
        view.setTintMode(TintedView.modes[a.getInt(carbon_tintMode, 1)]);

        if (a.hasValue(carbon_backgroundTint)) {
            ColorStateList color = getDefaultColorStateList((View) view, a, carbon_backgroundTint);
            if (color == null)
                color = a.getColorStateList(carbon_backgroundTint);
            if (color != null)
                view.setBackgroundTintList(color);
        }
        view.setBackgroundTintMode(TintedView.modes[a.getInt(carbon_backgroundTintMode, 1)]);

        if (a.hasValue(carbon_animateColorChanges))
            view.setAnimateColorChangesEnabled(a.getBoolean(carbon_animateColorChanges, false));
    }

    public static int getThemeColor(Context context, int attr) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId != 0 ? context.getResources().getColor(typedValue.resourceId) : typedValue.data;
    }

    public static Menu getMenu(Context context, int resId) {
        Context contextWrapper = context;
        Menu menu = new MenuBuilder(contextWrapper);
        MenuInflater inflater = new SupportMenuInflater(contextWrapper);
        inflater.inflate(resId, menu);
        return menu;
    }

    public static int getThemeResId(Context context, int attr) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValueAttr = new TypedValue();
        theme.resolveAttribute(attr, typedValueAttr, true);
        return typedValueAttr.resourceId;
    }
}
