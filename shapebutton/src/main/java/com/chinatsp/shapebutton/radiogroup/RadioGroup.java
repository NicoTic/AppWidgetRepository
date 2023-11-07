package com.chinatsp.shapebutton.radiogroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.chinatsp.shapebutton.R;

public class RadioGroup extends LinearLayoutCompat {
    // 记录被选中的View的id
    private int mCheckedId = -1;
    // 跟踪子View radio buttons 选中状态的改变,当RadioButton被选中时会回调
    private OnCheckableChangeListener mChildOnCheckedChangeListener;
    // RadioGroup提供给外部的监听回调
    private OnRadioGroupChangeListener mOnRadioGroupCheckedChangeListener;

    public void setOnRadioGroupCheckedChangeListener(OnRadioGroupChangeListener mOnRadioGroupCheckedChangeListener) {
        this.mOnRadioGroupCheckedChangeListener = mOnRadioGroupCheckedChangeListener;
    }

    // 监听 ViewGroup 的子 View 变化来实现在子 View 添加到 ViewGroup 时执行一些操作的需求
    private PassThroughHierarchyChangeListener mPassThroughListener;


    private boolean mProtectFromCheckedChange = false;
    public RadioGroup(@NonNull Context context) {
        super(context);
        init();
    }

    public RadioGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.RadioGroup, androidx.appcompat.R.attr.radioButtonStyle, 0);

        int value = attributes.getResourceId(R.styleable.RadioGroup_android_checkedButton, View.NO_ID);
        if (value != View.NO_ID) {
            mCheckedId = value;
        }

        attributes.recycle();
        init();
    }

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof Checkable) {
            final Checkable button = (Checkable) child;
            if (button.isChecked()) {
                mProtectFromCheckedChange = true;
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = child.hashCode();
                    child.setId(id);
                }
                setCheckedId(child.getId());
            }
        }

        super.addView(child, index, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 检查xml中RadioButton的check状态
        if (mCheckedId != -1) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(mCheckedId, true);
            mProtectFromCheckedChange = false;
            setCheckedId(mCheckedId);
        }
    }

    /**
     * 清除RadioGroup中的选项的选中
     */
    public void clearCheck() {
        check(-1);
    }

    public void check(int id) {
        // don't even bother
        if (id != -1 && (id == mCheckedId)) {
            return;
        }

        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    public interface OnRadioGroupChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(RadioGroup group, int checkedId);
    }

    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener{
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == RadioGroup.this && child instanceof RadioButton) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = child.hashCode();
                    child.setId(id);
                }
                ((RadioButton) child).setOnCheckedChangeInternalListener(
                        mChildOnCheckedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (parent == RadioGroup.this && child instanceof RadioButton) {
                ((RadioButton) child).setOnCheckedChangeInternalListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    private class CheckedStateTracker implements OnCheckableChangeListener {
        public void onCheckedChanged(Checkable view, boolean isChecked) {// 当RadioButton被选中时，将会调用
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            // 如果之前有被选中的RadioButton,先取消之前的RadioButton的选中状态
            if (mCheckedId != -1) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            int id = ((View) view).getId();
            setCheckedId(id);
        }
    }


    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof Checkable) {
            ((Checkable) checkedView).setChecked(checked);
        }
    }

    private void setCheckedId(int id) {
        mCheckedId = id;
        if (mOnRadioGroupCheckedChangeListener != null) {
            mOnRadioGroupCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }

}
