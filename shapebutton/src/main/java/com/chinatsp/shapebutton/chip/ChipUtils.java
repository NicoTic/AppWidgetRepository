package com.chinatsp.shapebutton.chip;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.chinatsp.shapebutton.R;

public class ChipUtils {
    public static void initChipDefaultBackground(View view, TypedArray a, int[] ids) {
        Drawable d = getChipDefaultColorDrawable(view, a, ids);
        if (d != null)
            view.setBackgroundDrawable(d);
    }

    public static Drawable getChipDefaultColorDrawable(View view, TypedArray a, int[] ids) {
        ColorStateList color = getChipColorStateList(view, a, ids);
        if (color != null) {
            Drawable d = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                d = new ColorStateListDrawable(color);
            }
            return d;
        }
        return null;
    }

    public static ColorStateList getChipColorStateList(View view, TypedArray a, int ids[]) {
        Context context = view.getContext();
        int chip_bg = ids[0];
        int chip_pressed_bg = ids[1];
        int chip_checked_bg = ids[2];
        int chip_un_enable_bg = ids[3];

        if (!a.hasValue(chip_bg))
            return null;
        int backgroundColor = a.getColor(chip_bg, 0);
        int pressedBgColor = a.getColor(chip_pressed_bg, ContextCompat.getColor(context, R.color.carbon_colorControlPressed));
        int checkedBgColor = a.getColor(chip_checked_bg,ContextCompat.getColor(context,R.color.carbon_colorControlActivated));
        int unEnableBgColor = a.getColor(chip_un_enable_bg,ContextCompat.getColor(context,R.color.carbon_colorControlDisabled));

        return ColorStateListFactory.getInstance().make(context,backgroundColor,
                pressedBgColor,
                checkedBgColor,
                unEnableBgColor);
    }
}
