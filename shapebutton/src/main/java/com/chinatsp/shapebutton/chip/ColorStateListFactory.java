package com.chinatsp.shapebutton.chip;

import android.content.Context;
import android.content.res.ColorStateList;

public class ColorStateListFactory {
    private static ColorStateListFactory INSTANCE = new ColorStateListFactory();
    //将构造器设置为private禁止通过new进行实例化
    private ColorStateListFactory() {

    }
    public static ColorStateListFactory getInstance() {
        return INSTANCE;
    }

    public ColorStateList make(Context context,int defaultColor,int pressed,int activated,int disabled,int invalid){
        return new ColorStateList(
                new int[][]{
                    new int[]{-android.R.attr.state_enabled},
                    new int[]{android.R.attr.state_pressed},
                    new int[]{android.R.attr.state_checked},
                    new int[]{android.R.attr.state_activated},
                    new int[]{android.R.attr.state_selected},
                    new int[]{android.R.attr.state_focused},
                    new int[]{}
                },
                new int[]{
                    disabled,
                    pressed,
                    activated,
                    activated,
                    activated,
                    activated,
                    defaultColor
                });
    }
}
