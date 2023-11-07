package com.chinatsp.shapebutton.backdrop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.chinatsp.shapebutton.shapeButton.utils.AnimUtils;

public class BackDropLayout extends FrameLayout {
    public enum Side{
        LEFT,RIGHT,BOTTOM,TOP,START,END
    }

    private BackdropLayout_Back backLayout;
    private BackdropLayout_Front frontLayout;

    private boolean opened;
    private Side side = Side.TOP;
    public BackDropLayout(@NonNull Context context) {
        super(context);
    }

    public BackDropLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BackDropLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BackDropLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(child instanceof BackdropLayout_Back && backLayout == null){
            this.backLayout = (BackdropLayout_Back) child;
            super.addView(child,index,params);
        }

        if(child instanceof BackdropLayout_Front && frontLayout == null){
            this.frontLayout = (BackdropLayout_Front) child;
            super.addView(child,index,params);
        }
    }

    public void toggleLayout(Side side){
        this.side = side;
        if(opened){
            closeLayout();
        }else{
            openLayout(side);
        }
    }

    private void openLayout(Side side) {
        Side s = side;
        if (s == Side.START) {
            if(ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL){
                s = Side.RIGHT;
            }else{
                s = Side.LEFT;
            }
        } else if (s == Side.END) {
            if(ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL){
                s = Side.LEFT;
            }else{
                s = Side.RIGHT;
            }
        }

        MarginLayoutParams layoutParams = (MarginLayoutParams) frontLayout.getLayoutParams();
        switch (s){
            case LEFT:
                ValueAnimator leftAnimator = ValueAnimator.ofFloat(frontLayout.getTranslationX(),backLayout.getWidth()-layoutParams.leftMargin);
                leftAnimator.setInterpolator(new AccelerateInterpolator());
                leftAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
                leftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        float animationValue = (float) animation.getAnimatedValue();
                        frontLayout.setTranslationX(animationValue);
                    }
                });
                leftAnimator.start();
                break;
            case RIGHT:
                ValueAnimator rightAnimator = ValueAnimator.ofFloat(frontLayout.getTranslationX(),-backLayout.getWidth() - layoutParams.rightMargin);
                rightAnimator.setInterpolator(new AccelerateInterpolator());
                rightAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
                rightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        float animationValue = (float) animation.getAnimatedValue();
                        frontLayout.setTranslationX(animationValue);
                    }
                });
                rightAnimator.start();
                break;
            case TOP:
                ValueAnimator topAnimator = ValueAnimator.ofFloat(frontLayout.getTranslationY(),backLayout.getHeight() - layoutParams.topMargin);
                topAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                topAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
                topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        float animationValue = (float) animation.getAnimatedValue();
                        frontLayout.setTranslationY(animationValue);
                    }
                });
                topAnimator.start();
                break;
            case BOTTOM:
                ValueAnimator bottomAnimator = ValueAnimator.ofFloat(frontLayout.getTranslationY(),-backLayout.getHeight() - layoutParams.bottomMargin);
                bottomAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                bottomAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
                bottomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        float animationValue = (float) animation.getAnimatedValue();
                        frontLayout.setTranslationY(animationValue);
                    }
                });
                bottomAnimator.start();
                break;
        }
        this.opened = true;
        this.side = s;
    }

    private void closeLayout() {
        if(this.side == Side.LEFT || this.side == Side.RIGHT){
            ValueAnimator horizontalAnimator = ValueAnimator.ofFloat(frontLayout.getTranslationX(),0f);
            horizontalAnimator.setInterpolator(new AccelerateInterpolator());
            horizontalAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
            horizontalAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    float animationValue = (float) animation.getAnimatedValue();
                    frontLayout.setTranslationX(animationValue);
                }
            });
            horizontalAnimator.start();
        }else if(this.side == Side.TOP || this.side == Side.BOTTOM){
            ValueAnimator verticalAnimator = ValueAnimator.ofFloat(frontLayout.getTranslationY(),0f);
            verticalAnimator.setInterpolator(new AccelerateInterpolator());
            verticalAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
            verticalAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    float animationValue = (float) animation.getAnimatedValue();
                    frontLayout.setTranslationY(animationValue);
                }
            });
            verticalAnimator.start();
        }
        this.opened = false;
    }

    public static class BackdropLayout_Back extends LinearLayout{

        public BackdropLayout_Back(Context context) {
            super(context);
        }

        public BackdropLayout_Back(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public BackdropLayout_Back(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public BackdropLayout_Back(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }

    public static class BackdropLayout_Front extends LinearLayout{

        public BackdropLayout_Front(Context context) {
            super(context);
        }

        public BackdropLayout_Front(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public BackdropLayout_Front(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public BackdropLayout_Front(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }

}
