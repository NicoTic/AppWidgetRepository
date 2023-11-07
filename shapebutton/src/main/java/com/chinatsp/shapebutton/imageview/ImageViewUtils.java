package com.chinatsp.shapebutton.imageview;

import static com.chinatsp.shapebutton.common.Carbon.getDefaultColorStateList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.TintAwareDrawable;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.animation.AnimatedColorStateList;
import com.chinatsp.shapebutton.checkbox.tint.TintedView;
import com.chinatsp.shapebutton.chip.ColorStateListFactory;
import com.chinatsp.shapebutton.common.Carbon;

public class ImageViewUtils {
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

    public static void initTint(TintedImageView view, TypedArray a, int[] ids) {
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
        view.setTintMode(TintedImageView.modes[a.getInt(carbon_tintMode, 1)]);

        if (a.hasValue(carbon_backgroundTint)) {
            ColorStateList color = getDefaultColorStateList((View) view, a, carbon_backgroundTint);
            if (color == null)
                color = a.getColorStateList(carbon_backgroundTint);
            if (color != null)
                view.setBackgroundTintList(color);
        }
        view.setBackgroundTintMode(TintedImageView.modes[a.getInt(carbon_backgroundTintMode, 1)]);

        if (a.hasValue(carbon_animateColorChanges))
            view.setAnimateColorChangesEnabled(a.getBoolean(carbon_animateColorChanges, false));
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
}
