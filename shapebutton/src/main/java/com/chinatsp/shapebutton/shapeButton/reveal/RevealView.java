package com.chinatsp.shapebutton.shapeButton.reveal;

import android.animation.Animator;

public interface RevealView {
    public static final float MAX_RADIUS = -1f;
    Animator createCircularReveal(int x, int y, float startRadius, float finishRadius);
}
