package com.chinatsp.shapebutton.chip;

import android.content.Context;
import android.content.res.ColorStateList;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.common.Carbon;

public class ColorStateListFactory {

    public static ColorStateListFactory getInstance() {
        return SingletonInternalClassHolder.INSTANCE;
    }

    private static class SingletonInternalClassHolder {
        private static final ColorStateListFactory INSTANCE = new ColorStateListFactory();
    }

    //将构造器设置为private禁止通过new进行实例化
    private ColorStateListFactory() {

    }

    public ColorStateList make(Context context, int defaultColor, int pressed, int activated, int disabled) {
        int validColor = Carbon.getThemeColor(context, android.R.attr.colorError);
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{R.attr.carbon_state_invalid},
                        new int[]{R.attr.carbon_state_indeterminate},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_activated},
                        new int[]{android.R.attr.state_selected},
                        new int[]{android.R.attr.state_focused},
                        new int[]{}
                },
                new int[]{
                        disabled,
                        validColor,
                        activated,
                        pressed,
                        activated,
                        activated,
                        activated,
                        activated,
                        defaultColor
                });
    }

    public ColorStateList make1(Context context, int defaultColor, int activated, int disabled) {
        int validColor = Carbon.getThemeColor(context, android.R.attr.colorError);
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{R.attr.carbon_state_invalid},
                        new int[]{R.attr.carbon_state_indeterminate},
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_activated},
                        new int[]{android.R.attr.state_selected},
                        new int[]{android.R.attr.state_focused},
                        new int[]{}
                },
                new int[]{
                        disabled,
                        validColor,
                        activated,
                        activated,
                        activated,
                        activated,
                        activated,
                        defaultColor
                });
    }

    public ColorStateList make2(Context context, int defaultColor, int activated, int disabled) {
        int validColor = Carbon.getThemeColor(context, android.R.attr.colorError);
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{R.attr.carbon_state_invalid},
                        new int[]{R.attr.carbon_state_indeterminate},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_activated},
                        new int[]{android.R.attr.state_focused},
                        new int[]{}
                },
                new int[]{
                        disabled,
                        validColor,
                        activated,
                        activated,
                        activated,
                        activated,
                        defaultColor
                });
    }

    public ColorStateList make3(Context context, int defaultColor, int disabled) {
        int validColor = Carbon.getThemeColor(context, android.R.attr.colorError);
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{R.attr.carbon_state_invalid},
                        new int[]{}
                },
                new int[]{
                        disabled,
                        validColor,
                        defaultColor
                });
    }


    public ColorStateList makeIconPrimaryChecked(Context context,int checkedColor){
        return make1(context,
                Carbon.getThemeColor(context, R.attr.carbon_iconColor),
                checkedColor,
                Carbon.getThemeColor(context, R.attr.carbon_iconColorDisabled)
        );
    }

    /**
     * 创建一个默认的ColorStateList，选择的颜色和主题颜色相同
     * @param context
     * @return
     */
    public ColorStateList makeIconPrimary(Context context){
      return make1(context,
              Carbon.getThemeColor(context, R.attr.carbon_iconColor),
              Carbon.getThemeColor(context, android.R.attr.colorPrimary),
              Carbon.getThemeColor(context, R.attr.carbon_iconColorDisabled)
      );
    }

    /**
     * 创建一个默认的ColorStateList，选择、按压的颜色和主题颜色相同
     * @param context
     * @return
     */
    public ColorStateList makeIconPrimary1(Context context){
        return make(context,
                Carbon.getThemeColor(context, R.attr.carbon_iconColor),
                Carbon.getThemeColor(context, android.R.attr.colorSecondary),
                Carbon.getThemeColor(context, android.R.attr.colorPrimary),
                Carbon.getThemeColor(context, R.attr.carbon_iconColorDisabled)
        );
    }

    /**
     * 创建一个默认的ColorStateList，按压的颜色和主题颜色相同
     * @param context
     * @return
     */
    public ColorStateList makeIconPrimary2(Context context){
        return make2(context,
                Carbon.getThemeColor(context, R.attr.carbon_iconColor),
                Carbon.getThemeColor(context, android.R.attr.colorPrimary),
                Carbon.getThemeColor(context, R.attr.carbon_iconColorDisabled)
        );
    }
}
