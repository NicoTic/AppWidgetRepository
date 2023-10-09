package com.chinatsp.shapebutton.chip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.shapeButton.common.Carbon;


public class Chip extends LinearLayoutCompat implements Checkable {

    /**
     * Interface definition for a callback to be invoked when the checked state of a chip
     * changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a chip has changed.
         *
         * @param chip      The chip whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(Chip chip, boolean isChecked);
    }

    private FrameLayout content;
    private ImageView check;
    private TextView title;
    private ImageView close;
    private OnRemoveListener onRemoveListener;
    private boolean checkedState = false;
    private OnCheckedChangeListener onCheckedChangeListener;

    // 定义状态集
    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    public interface OnRemoveListener {
        void onDismiss();
    }

    public Chip(Context context) {
        super(context, null, R.attr.carbon_chipStyle);
        initChip(null, R.attr.carbon_chipStyle, R.style.carbon_Chip);
    }

    public Chip(Context context, CharSequence text) {
        super(context, null, R.attr.carbon_chipStyle);
        initChip(null, R.attr.carbon_chipStyle, R.style.carbon_Chip);
        setText(text);
    }

    public Chip(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.carbon_chipStyle);
        initChip(attrs, R.attr.carbon_chipStyle, R.style.carbon_Chip);
    }

    public Chip(Context context, AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initChip(attrs, defStyleAttr, R.style.carbon_Chip);
    }

    private static int[] colorStateIds = new int[]{
            R.styleable.Chip_android_background,
            R.styleable.Chip_pressed_color,
            R.styleable.Chip_checked_color,
            R.styleable.Chip_un_enable_color
    };

    private void initChip(AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        inflate(getContext(), R.layout.carbon_chip, this);
        title = findViewById(R.id.carbon_chipText);
        content = findViewById(R.id.carbon_chipContent);
        check = findViewById(R.id.carbon_chipCheck);
        close = findViewById(R.id.carbon_chipClose);

        close.setOnClickListener(v -> {
            if (onRemoveListener != null)
                onRemoveListener.onDismiss();
        });

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Chip, defStyleAttr, defStyleRes);

        Carbon.initDefaultBackground(this, a, colorStateIds);

        setText(a.getString(R.styleable.Chip_android_text));
        setIcon(Carbon.getDrawable(this, a, R.styleable.Chip_carbon_icon, 0));
        setRemovable(a.getBoolean(R.styleable.Chip_carbon_removable, false));
        setChecked(a.getBoolean(R.styleable.Chip_android_checked, false));

        a.recycle();
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public boolean performClick() {
        toggle();

        if (onCheckedChangeListener != null)
            onCheckedChangeListener.onCheckedChanged(this, isChecked());

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
        return checkedState;
    }

    /**
     * <p>Changes the checked state of this chip.</p>
     *
     * @param checked true to check the chip, false to uncheck it
     */
    public void setChecked(boolean checked) {
        if (this.checkedState != checked) {
            checkedState = checked;
            check.setVisibility(checked ? VISIBLE : GONE);
            refreshDrawableState();
        }
    }

    /**
     * 先调用父类的onCreateDrawableState方法得到状态数组对象drawableState，但是参数extraSpace要加上1，因为我们要往里面增加一个状态。
     * 然后判断在代码逻辑中，是否为选中状态，如果是的话，调用mergeDrawableStates(drawableState, CHECKED_STATE_SET)方法把我们的状态值给加进去，
     * 最终返回drawableState。
     * @param extraSpace if non-zero, this is the number of extra entries you
     * would like in the returned array in which you can place your own
     * states.
     *
     * @return
     */
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /**
     * Register a callback to be invoked when the checked state of this chip changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    @Deprecated
    public void setText(String text) {
        setText((CharSequence) text);
    }

    public void setText(CharSequence text) {
        if (text != null) {
            title.setText(text);
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
        }
    }

    public void setText(int resId) {
        setText(getResources().getString(resId));
    }

    public String getText() {
        return (String) title.getText();
    }

    public View getTitleView() {
        return title;
    }

    public void setIcon(int iconRes) {
        content.removeAllViews();
        if (iconRes == 0) {
            content.setVisibility(GONE);
            return;
        }
        content.setVisibility(VISIBLE);
        ImageView icon = new ImageView(getContext());
        content.addView(icon);
        icon.setImageResource(iconRes);
    }

    public void setIcon(Drawable drawable) {
        content.removeAllViews();
        if (drawable == null) {
            content.setVisibility(GONE);
            return;
        }
        content.setVisibility(VISIBLE);
        ImageView icon = new ImageView(getContext());
        content.addView(icon);
        icon.setImageDrawable(drawable);
    }

    public void setIcon(Bitmap bitmap) {
        content.removeAllViews();
        if (bitmap == null) {
            content.setVisibility(GONE);
            return;
        }
        content.setVisibility(VISIBLE);
        ImageView icon = new ImageView(getContext());
        content.addView(icon);
        icon.setImageBitmap(bitmap);
    }

    @Deprecated
    public Drawable getIcon() {
        if (content.getChildCount() > 0 && content.getChildAt(0) instanceof ImageView)
            return ((ImageView) content.getChildAt(0)).getDrawable();
        return null;
    }

    @Deprecated
    public View getIconView() {
        if (content.getChildCount() > 0 && content.getChildAt(0) instanceof ImageView)
            return content.getChildAt(0);
        return null;
    }

    public View getContentView() {
        if (content.getChildCount() > 0)
            return content.getChildAt(0);
        return null;
    }

    public void setContentView(View view) {
        content.removeAllViews();
        if (view != null) {
            content.setVisibility(VISIBLE);
            content.addView(view);
        } else {
            content.setVisibility(GONE);
        }
    }

    public void setRemovable(boolean removable) {
        close.setVisibility(removable ? VISIBLE : GONE);
    }

    public boolean isRemovable() {
        return close.getVisibility() == VISIBLE;
    }

    public void setOnRemoveListener(OnRemoveListener onRemoveListener) {
        this.onRemoveListener = onRemoveListener;
    }

}
