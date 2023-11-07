package com.chinatsp.shapebutton.expansion;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.shapeButton.utils.AnimUtils;

public class ExpansionPanel extends LinearLayoutCompat {
    public ExpansionPanel(@NonNull Context context) {
        super(context);
        initExpansionPanel(null, 0, 0);
    }

    public ExpansionPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initExpansionPanel(attrs, 0, 0);
    }

    public ExpansionPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initExpansionPanel(attrs, defStyleAttr, 0);
    }
    interface OnExpandedStateChangedListerner {
        void onExpandedStateChanged(boolean isExpanded);
    }

    private OnExpandedStateChangedListerner onExpandedStateChangedListerner;
    private ExpansionPanel_Header header;
    private ExpansionPanel_Content content;

    public ExpansionPanel_Header getHeader() {
        return header;
    }

    public void setHeader(ExpansionPanel_Header header) {
        this.header = header;
    }

    public ExpansionPanel_Content getContent() {
        return content;
    }

    public void setContent(ExpansionPanel_Content content) {
        this.content = content;
    }

    private ImageView expandedIndicator;
    private boolean isExpanded;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        expandedIndicator.setRotation(expanded ? 180f : 0f);
        getContent().setVisibility(expanded ? View.VISIBLE : View.GONE);
    }

    private void initExpansionPanel(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setOrientation(VERTICAL);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpansionPanel,defStyleAttr,defStyleRes);
        isExpanded = a.getBoolean(R.styleable.ExpansionPanel_carbon_expanded,true);
        a.recycle();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(child instanceof ExpansionPanel_Header && this.header==null){
            this.header = (ExpansionPanel_Header) child;
            expandedIndicator = header.findViewById(R.id.carbon_panelExpandedIndicator);
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle();
                    if(onExpandedStateChangedListerner!=null){
                        onExpandedStateChangedListerner.onExpandedStateChanged(isExpanded);
                    }
                }
            });
            isExpanded = true;
            super.addView(child,index,params);
        }

        if(child instanceof ExpansionPanel_Content && this.content == null){
            this.content = (ExpansionPanel_Content) child;
            isExpanded = true;
            super.addView(child,index,params);
        }
    }

    private void toggle() {
        if(isExpanded){
            collapse();
        }else{
            expand();
        }
    }

    private void expand() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float animationValue = (float) animation.getAnimatedValue();
                if(expandedIndicator!=null){
                    expandedIndicator.setRotation(180 * animationValue);
                    expandedIndicator.postInvalidate();
                }
            }
        });
        valueAnimator.start();
        if(content != null){
            content.setVisibility(VISIBLE);
        }
        isExpanded = true;
    }

    private void collapse() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f,0f);
        valueAnimator.setDuration(AnimUtils.SHORT_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                float animationValue = (float) animation.getAnimatedValue();
                if(expandedIndicator!=null){
                    expandedIndicator.setRotation(180 * animationValue);
                    expandedIndicator.postInvalidate();
                }
            }
        });
        valueAnimator.start();
        if(content != null){
            content.setVisibility(GONE);
        }
        isExpanded = false;
    }

    public static class ExpansionPanel_Header extends RelativeLayout {

        public ExpansionPanel_Header(@NonNull Context context) {
            super(context);
            initView(context);
        }

        public ExpansionPanel_Header(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            initView(context);
        }

        public ExpansionPanel_Header(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView(context);
        }

        public ExpansionPanel_Header(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            initView(context);
        }

        private void initView(Context context) {
            View.inflate(context,R.layout.carbon_expansionpanel_header,this);
        }
    }

    public static class ExpansionPanel_Content extends LinearLayoutCompat {
        public ExpansionPanel_Content(@NonNull Context context) {
            super(context);
        }

        public ExpansionPanel_Content(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public ExpansionPanel_Content(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.expanded = isExpanded;
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        isExpanded = ss.expanded;
        requestLayout();
    }

    class SavedState extends BaseSavedState{
        private boolean expanded = false;

        public SavedState(Parcel in) {
            super(in);
            expanded =  in.readInt() == 1;
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(expanded ? 1 : 0);
        }

        @NonNull
        @Override
        public String toString() {
            return ("ExpansionPanel.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " expanded=" + expanded + "}");
        }

        Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
