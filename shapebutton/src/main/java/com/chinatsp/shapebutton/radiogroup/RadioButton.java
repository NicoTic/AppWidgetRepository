package com.chinatsp.shapebutton.radiogroup;

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
import android.view.ViewDebug;
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
import com.chinatsp.shapebutton.checkbox.ButtonGravity;
import com.chinatsp.shapebutton.checkbox.tint.TintedView;
import com.chinatsp.shapebutton.common.Carbon;
import com.chinatsp.shapebutton.shapeButton.ripple.RippleDrawable;

public class RadioButton extends AppCompatTextView implements Checkable,
        TintedView {
    private Drawable drawable;
    private float drawablePadding;
    private ButtonGravity buttonGravity;

    private static int[] tintIds = new int[]{
            R.styleable.RadioButton_carbon_tint,
            R.styleable.RadioButton_carbon_tintMode,
            R.styleable.RadioButton_carbon_backgroundTint,
            R.styleable.RadioButton_carbon_backgroundTintMode,
            R.styleable.RadioButton_carbon_animateColorChanges
    };

    public RadioButton(Context context) {
        super(context, null, android.R.attr.radioButtonStyle);
        initRadioButton(null, android.R.attr.radioButtonStyle, R.style.carbon_RadioButton);
    }

    public RadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, android.R.attr.radioButtonStyle);
        initRadioButton(attrs, android.R.attr.radioButtonStyle, R.style.carbon_RadioButton);
    }

    public RadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRadioButton(attrs, defStyleAttr, R.style.carbon_RadioButton);
    }

    public void initRadioButton(AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RadioButton, defStyleAttr, defStyleRes);

        Carbon.initTint(this,a,tintIds);
        setButtonDrawable(Carbon.getDrawable(this, a, R.styleable.RadioButton_android_button, R.drawable.carbon_radio_anim));

        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.RadioButton_android_drawablePadding) {
                drawablePadding = a.getDimension(attr, 0);
            } else if (attr == R.styleable.RadioButton_android_checked) {
                setChecked(a.getBoolean(attr, false));
            } else if (attr == R.styleable.RadioButton_carbon_buttonGravity) {
                buttonGravity = ButtonGravity.values()[a.getInt(attr, 0)];
            }
        }

        a.recycle();
    }

    public ButtonGravity getButtonGravity() {
        return buttonGravity;
    }

    public void setButtonGravity(ButtonGravity buttonGravity) {
        this.buttonGravity = buttonGravity;
    }

    private boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    private boolean isButtonOnTheLeft() {
        return buttonGravity == ButtonGravity.LEFT ||
                !isLayoutRtl() && buttonGravity == ButtonGravity.START ||
                isLayoutRtl() && buttonGravity == ButtonGravity.END;
    }

    /**
     * Set the button graphic to a given Drawable
     *
     * @param d The Drawable to use as the button graphic
     */
    public void setButtonDrawable(Drawable d) {
        if (drawable != d) {
            if (drawable != null) {
                drawable.setCallback(null);
                unscheduleDrawable(drawable);
            }

            drawable = d;

            if (d != null) {
                drawable = DrawableCompat.wrap(d);
                d.setCallback(this);
                if (d.isStateful()) {
                    d.setState(getDrawableState());
                }
                d.setVisible(getVisibility() == VISIBLE, false);
                setMinHeight(d.getIntrinsicHeight());
                applyTint();
            }
        }
    }



    // -------------------------------
    // checkable
    // -------------------------------

    private boolean checked;
    private boolean mBroadcasting;
    // View check 状态改变时的回调监听，用于外部调用
    private OnCheckableChangeListener onCheckedChangeListener;
    // View check 状态改变时的回调监听，用于RadioGroup内部调用
    private OnCheckableChangeListener mOnCheckedChangeInternalListener;

    /**
     * 当Button的check状态改变时的回调，用于外部通信
     *
     * @param onCheckedChangeListener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckableChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    /**
     * 当Button的check状态改变时的回调，只用于内部
     * Register a callback to be invoked when the checked state of this button changes. This
     * callback is used for internal purpose only.
     *
     * @param onCheckedChangeListener the callback to call on checked state change
     * @hide
     */
    public void setOnCheckedChangeInternalListener(OnCheckableChangeListener mOnCheckedChangeInternalListener) {
        this.mOnCheckedChangeInternalListener = mOnCheckedChangeInternalListener;
    }

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public boolean performClick() {
        // RadioButton 点击不可取消，因此，这里点击后就需要设置check状态为true
        setChecked(true);

        if (onCheckedChangeListener != null)
            onCheckedChangeListener.onCheckedChanged(this, checked);

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return checked;
    }

    /**
     * <p>Changes the checked state of this button.</p>
     *
     * @param checked true to check the button, false to uncheck it
     */
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
            //notifyViewAccessibilityStateChangedIfNeeded(
            //      AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED);

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeInternalListener != null) {
                mOnCheckedChangeInternalListener.onCheckedChanged(this, checked);
            }

            mBroadcasting = false;
        }
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

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (drawable != null) drawable.jumpToCurrentState();
    }


    // -------------------------------
    // 存储记录
    // -------------------------------
    static class SavedState extends BaseSavedState {
        boolean checked;

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
            checked = (Boolean) in.readValue(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(checked);
        }

        @Override
        public String toString() {
            return "RadioButton.SavedState{"
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

        ss.checked = isChecked();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    // -------------------------------
    // 绘制
    // -------------------------------
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
        final Drawable buttonDrawable = drawable;
        if (buttonDrawable != null) {
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

            buttonDrawable.setBounds(left, top, right, bottom);

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


    // -------------------------------
    // Tint 着色
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
    public void setBackgroundTint(int color) {
        setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void setBackgroundTint(ColorStateList list) {
        this.backgroundTint = list == null ? null : animateColorChanges && !(list instanceof AnimatedColorStateList) ? AnimatedColorStateList.fromList(list, backgroundTintAnimatorListener) : list;
        applyBackgroundTint();
    }

    @Override
    public ColorStateList getBackgroundTint() {
        return backgroundTint;
    }

    @Override
    public void setBackgroundTintMode(PorterDuff.Mode backgroundTintMode) {
        this.backgroundTintMode = backgroundTintMode;
        applyBackgroundTint();
    }

    @Nullable
    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        return backgroundTintMode;
    }

    @Override
    public boolean isAnimateColorChangesEnabled() {
        return animateColorChanges;
    }

    @Override
    public void setAnimateColorChangesEnabled(boolean animateColorChanges) {
        if (this.animateColorChanges == animateColorChanges)
            return;
        this.animateColorChanges = animateColorChanges;
        setTintList(tint);
        setBackgroundTintList(backgroundTint);
        setTextColor(getTextColors());
    }

    protected void applyTint() {
        // 为TextView的CompoundDrawables着色
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

        // 为自定义的drawable着色
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

    protected void updateTint() {
        Drawable[] drawables = getCompoundDrawables();
        if (tint != null && tintMode != null) {
            for (Drawable drawable : drawables) {
                if (drawable != null)
                    drawable.setColorFilter(new PorterDuffColorFilter(tint.getColorForState(getDrawableState(), tint.getDefaultColor()), tintMode));
            }
        }
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

    protected void updateBackgroundTint() {
        Drawable background = getBackground();
        if (background instanceof RippleDrawable)
            background = ((RippleDrawable) background).getBackground();
        if (background != null && backgroundTint != null && backgroundTintMode != null)
            background.setColorFilter(new PorterDuffColorFilter(backgroundTint.getColorForState(getDrawableState(), backgroundTint.getDefaultColor()), backgroundTintMode));
    }
}
