package com.chinatsp.shapebutton.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.chinatsp.shapebutton.shadow.ShadowView;
import com.chinatsp.shapebutton.stateAnimator.StateAnimator;

public class AnimUtils {
    public static final long TOOLTIP_DURATION = 3000;
    public static final int SHORT_ANIMATION_DURATION = 150;
    private static final int LONG_ANIMATION_DURATION = 500;
    private AnimUtils() {
    }

    public static void setupElevationAnimator(StateAnimator stateAnimator, final ShadowView view) {
        // 按下时的状态和动画
        {
            final ValueAnimator animator = ValueAnimator.ofFloat(0, 0);
            animator.setDuration(SHORT_ANIMATION_DURATION);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            Animator.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    float elevationOrTranslationZ = getElevationOrTranslationZ(view);
                    animator.setFloatValues(0, elevationOrTranslationZ);
                }
            };
            animator.addUpdateListener(animation -> view.setTranslationZ((Float) animation.getAnimatedValue()));
            stateAnimator.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, animator, animatorListener);
        }
        // 松开时的状态和动画
        {
            final ValueAnimator animator = ValueAnimator.ofFloat(0, 0);
            animator.setDuration(SHORT_ANIMATION_DURATION);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            Animator.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    float elevationOrTranslationZ = getElevationOrTranslationZ(view);
                    animator.setFloatValues(elevationOrTranslationZ, 0);
                }
            };
            animator.addUpdateListener(animation -> view.setTranslationZ((Float) animation.getAnimatedValue()));
            stateAnimator.addState(new int[]{-android.R.attr.state_pressed, android.R.attr.state_enabled}, animator, animatorListener);
        }
        // 松开时的状态和动画
        {
            final ValueAnimator animator = ValueAnimator.ofFloat(0, 0);
            animator.setDuration(SHORT_ANIMATION_DURATION);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            Animator.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animator.setFloatValues(view.getElevation(), 0);
                }
            };
            animator.addUpdateListener(animation -> view.setTranslationZ((Float) animation.getAnimatedValue()));
            stateAnimator.addState(new int[]{android.R.attr.state_enabled}, animator, animatorListener);
        }

    }

    private static float getElevationOrTranslationZ(ShadowView view) {
        float elevationOrTranslationZ = view.getElevation();
        if(view.getElevation() == 0){
            elevationOrTranslationZ = view.getTranslationZ();
        }
        return elevationOrTranslationZ;
    }

}
