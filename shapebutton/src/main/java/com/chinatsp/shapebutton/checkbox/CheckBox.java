package com.chinatsp.shapebutton.checkbox;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.widget.Checkable;
import android.widget.CompoundButton;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.animation.AnimatedColorStateList;
import com.chinatsp.shapebutton.checkbox.tint.TintedView;
import com.chinatsp.shapebutton.common.Carbon;
import com.chinatsp.shapebutton.shapeButton.ripple.RippleDrawable;

public class CheckBox extends AppCompatTextView implements Checkable, TintedView {
    private Drawable drawable;
    private float drawablePadding;
    private ButtonGravity buttonGravity;

    CheckedState checkedState = CheckedState.UNCHECKED;

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };
    private static final int[] INDETERMINATE_STATE_SET = {
            R.attr.carbon_state_indeterminate
    };

    private static int[] tintIds = new int[]{
            R.styleable.CheckBox_carbon_tint,
            R.styleable.CheckBox_carbon_tintMode,
            R.styleable.CheckBox_carbon_backgroundTint,
            R.styleable.CheckBox_carbon_backgroundTintMode,
            R.styleable.CheckBox_carbon_animateColorChanges
    };

    public CheckBox(@NonNull Context context) {
        super(context,null, android.R.attr.checkboxStyle);
        initCheckBox(null, android.R.attr.checkboxStyle, R.style.carbon_CheckBox);
    }

    public CheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, android.R.attr.checkboxStyle);
        initCheckBox(attrs, android.R.attr.checkboxStyle, R.style.carbon_CheckBox);
    }

    public CheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCheckBox(attrs, defStyleAttr, R.style.carbon_CheckBox);
    }

    public void initCheckBox(AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CheckBox, defStyleAttr, defStyleRes);

        Carbon.initTint(this, a, tintIds);
        setButtonDrawable(Carbon.getDrawable(this, a, R.styleable.CheckBox_android_button, R.drawable.carbon_checkbox_anim));

        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CheckBox_android_drawablePadding) {
                drawablePadding = a.getDimension(attr, 0);
            } else if (attr == R.styleable.CheckBox_android_checked) {
                setChecked(a.getBoolean(attr, false));
            } else if (attr == R.styleable.CheckBox_carbon_buttonGravity) {
                buttonGravity = ButtonGravity.values()[a.getInt(attr, 0)];
            }
        }

        a.recycle();
    }

    private void setButtonDrawable(Drawable d) {
        if(this.drawable != d){
            if(this.drawable != null){
                this.drawable.setCallback(null);
                unscheduleDrawable(this.drawable);
            }
            this.drawable = d;

            if(d != null){
                this.drawable = DrawableCompat.wrap(d);
                d.setCallback(this);
                if(d.isStateful()){
                    d.setState(getDrawableState());
                }
                d.setVisible(getVisibility() == VISIBLE,false);
                setMinHeight(d.getIntrinsicHeight());

                applyTint();
            }
        }
    }

    private boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    private boolean isButtonOnTheLeft() {
        return buttonGravity == ButtonGravity.LEFT ||
                !isLayoutRtl() && buttonGravity == ButtonGravity.START ||
                isLayoutRtl() && buttonGravity == ButtonGravity.END;
    }

    @Override
    public int getCompoundPaddingLeft() {
        int padding = super.getCompoundPaddingLeft();
        if (isButtonOnTheLeft()) {
            final Drawable buttonDrawable = drawable;
            if (buttonDrawable != null) {
                padding += buttonDrawable.getIntrinsicWidth() + drawablePadding;
            }
        }
        return padding;
    }

    @Override
    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight();
        if (!isButtonOnTheLeft()) {
            final Drawable buttonDrawable = drawable;
            if (buttonDrawable != null) {
                padding += buttonDrawable.getIntrinsicWidth() + drawablePadding;
            }
        }
        return padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final Drawable buttonDrawable = this.drawable;
        if(buttonDrawable!=null){
            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            final int drawableHeight = buttonDrawable.getIntrinsicHeight();
            final int drawableWidth = buttonDrawable.getIntrinsicWidth();

            final int top;
            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    top = getHeight() - drawableHeight;
                    break;
                case Gravity.CENTER_VERTICAL:
                    top = (getHeight() - drawableHeight) / 2;
                    break;
                default:
                    top = 0;
            }

            final int bottom = top + drawableHeight;
            final int left = isButtonOnTheLeft() ? getPaddingLeft() : getWidth() - drawableWidth - getPaddingRight();
            final int right = isButtonOnTheLeft() ? drawableWidth + getPaddingLeft() : getWidth() - getPaddingRight();

            buttonDrawable.setBounds(left,top,right,bottom);
        }
        super.onDraw(canvas);
        if (buttonDrawable != null) {
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();
            if (scrollX == 0 && scrollY == 0) {
                buttonDrawable.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                buttonDrawable.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }
    }

    private OnCheckedChangeListener onCheckedChangeListener;

    /**
     * Interface definition for a callback to be invoked when the checked state of a compound button
     * changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(CheckBox buttonView, CheckedState isChecked);
    }

    /**
     * Register a callback to be invoked when the checked state of this button changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    @Override
    public boolean performClick() {
        toggle();

        if (onCheckedChangeListener != null)
            onCheckedChangeListener.onCheckedChanged(this, checkedState);

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked ? CheckedState.CHECKED : CheckedState.UNCHECKED);
    }

    public void setChecked(CheckedState state) {
        if (this.checkedState != state) {
            checkedState = state;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return checkedState == CheckedState.CHECKED;
    }

    public boolean isIndeterminate() {
        return checkedState == CheckedState.INDETERMINATE;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace);
        if (isChecked()) {
            int[] state = new int[drawableState.length + 1];
            System.arraycopy(drawableState, 0, state, 0, drawableState.length);
            drawableState = state;
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        if (isIndeterminate()) {
            int[] state = new int[drawableState.length + 1];
            System.arraycopy(drawableState, 0, state, 0, drawableState.length);
            drawableState = state;
            mergeDrawableStates(drawableState, INDETERMINATE_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        Drawable d = drawable;
        if (d != null && d.isStateful()
                && d.setState(getDrawableState())) {
            invalidateDrawable(d);
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == drawable;
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
    ValueAnimator.AnimatorUpdateListener textColorAnimatorListener = animation -> setHintTextColor(getHintTextColors());

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
        Drawable[] drawables = getCompoundDrawables();
        if (tint != null && tintMode != null) {
            for (Drawable drawable : drawables) {
                if (drawable != null)
                    drawable.setColorFilter(new PorterDuffColorFilter(tint.getColorForState(getDrawableState(), tint.getDefaultColor()), tintMode));
            }
        }
        if (drawable != null && tint != null && tintMode != null)
            drawable.setColorFilter(new PorterDuffColorFilter(tint.getColorForState(getDrawableState(), tint.getDefaultColor()), tintMode));
    }

    protected void applyTint() {
        // 为TextView的CompoundDrawables 着色
        Drawable[] drawables = getCompoundDrawables();
        if (tint != null && tintMode != null) {
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    Carbon.setTintListMode(drawable, tint, tintMode);

                    if (drawable.isStateful())
                        drawable.setState(getDrawableState());
                }
            }
        } else {
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    Carbon.clearTint(drawable);

                    if (drawable.isStateful())
                        drawable.setState(getDrawableState());
                }
            }
        }
        // 为自定义设置的drawable着色
        if (drawable != null) {
            if (tint != null && tintMode != null) {
                Carbon.setTintListMode(drawable, tint, tintMode);
            } else {
                Carbon.clearTint(drawable);
            }

            // The drawable (or one of its children) may not have been
            // stateful before applying the tint, so let's try again.
            if (drawable.isStateful())
                drawable.setState(getDrawableState());
        }
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

    @Override
    public void setBackgroundTintList(ColorStateList list) {
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
            Carbon.setTintListMode(background, backgroundTint, backgroundTintMode);
        } else {
            Carbon.clearTint(background);
        }

        if (background.isStateful())
            background.setState(getDrawableState());
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        this.backgroundTintMode = mode;
        applyBackgroundTint();
    }

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
        setTextColor(getTextColors());
    }

    static class SavedState extends BaseSavedState {
        CheckedState checked;

        /**
         * Constructor called from {@link CompoundButton#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            checked = CheckedState.values()[in.readInt()];
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(checked.ordinal());
        }

        @Override
        public String toString() {
            return "CheckBox.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.checked = checkedState;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

}
