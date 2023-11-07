package com.chinatsp.shapebutton.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.StateSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;

public class AnimatedColorStateList extends ColorStateList {
    private int[][] states;
    private ValueAnimator.AnimatorUpdateListener listener;
    private int[] currentState;
    private ValueAnimator colorAnimation;
    private int animatedColor = 0;

    private static Field mStateSpecsField;
    private static Field mColorsField;
    private static Field mDefaultColorField;

    /**
     * Creates a ColorStateList that returns the specified mapping from
     * states to colors.
     *
     * @param states
     * @param colors
     */
    public AnimatedColorStateList(int[][] states, int[] colors, ValueAnimator.AnimatorUpdateListener updateListener) {
        super(states, colors);
        initColorAnimator(listener);
    }

    private void initColorAnimator(ValueAnimator.AnimatorUpdateListener listener) {
        colorAnimation = ValueAnimator.ofInt(0, 0);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.setDuration(200);
        colorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                animatedColor = (int) animation.getAnimatedValue();
                if(listener!=null){
                    listener.onAnimationUpdate(animation);
                }
            }
        });
        colorAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatedColor = (int) colorAnimation.getAnimatedValue();
                if(listener!=null){
                    listener.onAnimationUpdate(colorAnimation);
                }
            }
        });
    }

    public static AnimatedColorStateList fromList(ColorStateList list, ValueAnimator.AnimatorUpdateListener listener) {
        int[][] mStateSpecs; // must be parallel to mColors
        int[] mColors; // must be parallel to mStateSpecs
        int mDefaultColor;
        try {
            mStateSpecs = (int[][]) mStateSpecsField.get(list);
            mColors = (int[]) mColorsField.get(list);
            mDefaultColor = (int) mDefaultColorField.get(list);
            AnimatedColorStateList animatedColorStateList = new AnimatedColorStateList(mStateSpecs, mColors, listener);
            mDefaultColorField.set(animatedColorStateList, mDefaultColor);
            return animatedColorStateList;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Parcelable.Creator<AnimatedColorStateList> CREATOR = new Parcelable.Creator<AnimatedColorStateList>() {
        @Override
        public AnimatedColorStateList[] newArray(int size) {
            return new AnimatedColorStateList[size];
        }

        @Override
        public AnimatedColorStateList createFromParcel(Parcel source) {
            int N = source.readInt();
            int[][] stateSpecs = new int[N][];
            for (int i = 0; i < N; i++) {
                stateSpecs[i] = source.createIntArray();
            }
            int[] colors = source.createIntArray();
            return fromList(new ColorStateList(stateSpecs, colors), null);
        }
    };

    static {
        try {
            mStateSpecsField = ColorStateList.class.getDeclaredField("mStateSpecs");
            mStateSpecsField.setAccessible(true);
            mColorsField = ColorStateList.class.getDeclaredField("mColors");
            mColorsField.setAccessible(true);
            mDefaultColorField = ColorStateList.class.getDeclaredField("mDefaultColor");
            mDefaultColorField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getColorForState(@Nullable int[] stateSet, int defaultColor) {
        synchronized (this){
            if (Arrays.equals(stateSet, currentState) && colorAnimation.isRunning()) {
                return animatedColor;
            }
        }
        return super.getColorForState(stateSet, defaultColor);
    }

    public void setStates(int[] newState) {
        synchronized (this){
            if(Arrays.equals(newState,currentState)) return;
            colorAnimation.end();
            if(currentState.length!=0){
                for (int[] state: states) {
                    if (StateSet.stateSetMatches(state, newState)) {
                        int firstColor = getColorForState(currentState, getDefaultColor());
                        int secondColor = super.getColorForState(newState, getDefaultColor());
                        colorAnimation.setIntValues(firstColor, secondColor);
                        currentState = newState;
                        animatedColor = firstColor;
                        colorAnimation.start();
                        return;
                    }
                }
            }
        }
        this.currentState = newState;
    }

    public void jumpToCurrentState() {
        colorAnimation.end();
    }
}
