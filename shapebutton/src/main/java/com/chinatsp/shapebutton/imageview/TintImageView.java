package com.chinatsp.shapebutton.imageview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.view.ViewCompat;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.animation.AnimatedColorStateList;
import com.chinatsp.shapebutton.shapeButton.ripple.RippleDrawable;

public class TintImageView extends androidx.appcompat.widget.AppCompatImageView implements TintedImageView {

    private static int[] tintIds = new int[]{
            R.styleable.TintImageView_carbon_tint,
            R.styleable.TintImageView_carbon_tintMode,
            R.styleable.TintImageView_carbon_backgroundTint,
            R.styleable.TintImageView_carbon_backgroundTintMode,
            R.styleable.TintImageView_carbon_animateColorChanges
    };
    public TintImageView(@NonNull Context context) {
        super(context, null, R.attr.carbon_imageViewStyle);
        initImageView(null, R.attr.carbon_imageViewStyle, R.style.carbon_ImageView);
    }

    public TintImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.attr.carbon_imageViewStyle);
        initImageView(attrs, R.attr.carbon_imageViewStyle, R.style.carbon_ImageView);
    }

    public TintImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initImageView(attrs, defStyleAttr, R.style.carbon_ImageView);
    }

    private void initImageView(AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TintImageView, defStyleAttr, defStyleRes);

        Drawable d = ImageViewUtils.getDrawable(this, a, R.styleable.TintImageView_carbon_src, R.drawable.carbon_iconplaceholder);
        if (d != null)
            setImageDrawable(d);


        ImageViewUtils.initTint(this, a, tintIds);

        setEnabled(a.getBoolean(R.styleable.TintImageView_android_enabled, true));

        a.recycle();
    }

    // -------------------------------
    // tint
    // -------------------------------

    ColorStateList tint;
    PorterDuff.Mode tintMode;
    ColorStateList backgroundTint;
    PorterDuff.Mode backgroundTintMode;
    boolean animateColorChanges;

    ValueAnimator.AnimatorUpdateListener tintAnimatorListener = animation -> {
        updateTint();
        ViewCompat.postInvalidateOnAnimation(this);
    };
    ValueAnimator.AnimatorUpdateListener backgroundTintAnimatorListener = animation -> {
        updateBackgroundTint();
        ViewCompat.postInvalidateOnAnimation(this);
    };

    @Override
    public void setTintList(ColorStateList list) {
        this.tint = list == null ? null : animateColorChanges && !(list instanceof AnimatedColorStateList) ? AnimatedColorStateList.fromList(list, tintAnimatorListener) : list;
        applyTint();
    }

    @Override
    public void setTint(int color) {
        setTintList(ColorStateList.valueOf(color));
    }

    @Override
    public ColorStateList getTint() {
        return tint;
    }

    protected void updateTint() {
        Drawable drawable = getDrawable();
        if (drawable != null && tint != null && tintMode != null)
            drawable.setColorFilter(new PorterDuffColorFilter(tint.getColorForState(getDrawableState(), tint.getDefaultColor()), tintMode));
    }

    protected void applyTint() {
        Drawable drawable = getDrawable();
        if (drawable == null)
            return;

        if (tint != null && tintMode != null) {
            ImageViewUtils.setTintListMode(drawable, tint, tintMode);
        } else {
            ImageViewUtils.clearTint(drawable);
        }

        if (drawable.isStateful())
            drawable.setState(getDrawableState());
        if (tint != null && tint instanceof AnimatedColorStateList)
            ((AnimatedColorStateList) tint).setStates(getDrawableState());
    }

    @Override
    public void setTintMode(@NonNull PorterDuff.Mode mode) {
        this.tintMode = mode;
        applyTint();
    }

    @Override
    public PorterDuff.Mode getTintMode() {
        return tintMode;
    }

    public void setBackgroundTint(ColorStateList list) {
        this.backgroundTint = list == null ? null : animateColorChanges && !(list instanceof AnimatedColorStateList) ? AnimatedColorStateList.fromList(list, backgroundTintAnimatorListener) : list;
        applyBackgroundTint();
    }

    @Override
    public void setBackgroundTint(int color) {
        setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public ColorStateList getBackgroundTint() {
        return backgroundTint;
    }

    protected void updateBackgroundTint() {
        Drawable background = getBackground();
        if (background instanceof RippleDrawable)
            background = ((RippleDrawable) background).getBackground();
        if (background != null && backgroundTint != null && backgroundTintMode != null)
            background.setColorFilter(new PorterDuffColorFilter(backgroundTint.getColorForState(getDrawableState(), backgroundTint.getDefaultColor()), backgroundTintMode));
    }

    protected void applyBackgroundTint() {
        Drawable background = getBackground();
        if (background instanceof RippleDrawable)
            background = ((RippleDrawable) background).getBackground();
        if (background == null)
            return;

        if (backgroundTint != null && backgroundTintMode != null) {
            ImageViewUtils.setTintListMode(background, backgroundTint, backgroundTintMode);
        } else {
            ImageViewUtils.clearTint(background);
        }

        if (background.isStateful())
            background.setState(getDrawableState());
        if (backgroundTint != null && backgroundTint instanceof AnimatedColorStateList)
            ((AnimatedColorStateList) backgroundTint).setStates(getDrawableState());
    }

    @Override
    public void setBackgroundTintMode(PorterDuff.Mode mode) {
        this.backgroundTintMode = mode;
        applyBackgroundTint();
    }

    @Nullable
    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        return backgroundTintMode;
    }

    public boolean isAnimateColorChangesEnabled() {
        return animateColorChanges;
    }

    public void setAnimateColorChangesEnabled(boolean animateColorChanges) {
        if (this.animateColorChanges == animateColorChanges)
            return;
        this.animateColorChanges = animateColorChanges;
        setTintList(tint);
        setBackgroundTintList(backgroundTint);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        applyTint();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        applyTint();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        applyTint();
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        applyBackgroundTint();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (tint != null && tint instanceof AnimatedColorStateList)
            ((AnimatedColorStateList) tint).setStates(getDrawableState());
        if (backgroundTint != null && backgroundTint instanceof AnimatedColorStateList)
            ((AnimatedColorStateList) backgroundTint).setStates(getDrawableState());
    }
}
