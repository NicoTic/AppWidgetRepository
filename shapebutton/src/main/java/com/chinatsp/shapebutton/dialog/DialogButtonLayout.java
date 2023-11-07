package com.chinatsp.shapebutton.dialog;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DialogButtonLayout extends LinearLayout {
    private int mChildViewCount;
    private int mChildViewWidth;
    private int mChildViewSpacing;
    public DialogButtonLayout(Context context) {
        super(context);
    }

    public DialogButtonLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogButtonLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChildViewCount = getChildCount();
        if (mChildViewCount > 0) {
            View childView = getChildAt(0);
            mChildViewWidth = childView.getWidth();
            mChildViewSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childWidth = totalWidth / childCount;
        int totalHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
            // 计算totalHeight，取所有子视图中高度最大的一个
            totalHeight = Math.max(totalHeight, child.getMeasuredHeight());
        }

        setMeasuredDimension(totalWidth, totalHeight);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//        if(mChildViewCount>0){
//            int childWidthSpec = MeasureSpec.makeMeasureSpec(width / mChildViewCount, MeasureSpec.EXACTLY);
//            int childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//            for (int i = 0; i < mChildViewCount; i++) {
//                getChildAt(i).measure(childWidthSpec, childHeightSpec);
//            }
//        }
    }
    private int spacing = 10; // 你可以根据需要调整间距的大小
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        int childWidth = (right - left - (childCount - 1) * spacing) / childCount;
        int parentHeight = bottom - top;
        if (childCount == 1) {
            View child = getChildAt(0);
            int childLeft = (right - left - child.getMeasuredWidth()) / 2;
            int childTop = (bottom - top - child.getMeasuredHeight()) / 2;
            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
        } else {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if(child instanceof TextView){
                    TextView textView = (TextView) child;
                    float textWidth = textView.getPaint().measureText(textView.getText().toString());
                    int textHeight = textView.getLayout().getLineTop(textView.getLineCount());
                    int paddingHorizontal = (int) Math.max(0,(child.getMeasuredWidth() - textWidth)/2);
                    int paddingVertical = Math.max(0,(child.getMeasuredHeight()-textHeight)/2);
                    textView.setGravity(Gravity.CENTER);
//                    textView.setPadding(paddingHorizontal,paddingVertical,paddingHorizontal,paddingVertical);
                    textView.setPadding(child.getPaddingLeft(),paddingVertical,child.getPaddingRight(),paddingVertical);
                }
                int childLeft = i * (childWidth + spacing); // 考虑间距
                int childTop = (parentHeight - child.getMeasuredHeight()) / 2;

                child.layout(childLeft, childTop, childLeft + childWidth, childTop + child.getMeasuredHeight());
            }
        }

    }

}
