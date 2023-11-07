package com.chinatsp.shapebutton.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatButton;
import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.common.Carbon;

import java.util.Map;
import java.util.function.Consumer;

public abstract class DialogBase extends android.app.Dialog {
    private static final String TAG = DialogBase.class.getSimpleName();
    private LinearLayout container;
    private LinearLayout header;
    private LinearLayout footer;

    private TextView titleTextView;

    protected View topDivider;

    protected View bottomDivider;

    private ViewGroup buttonContainer;

    private View dialogLayout;

    private View contentView;

    public DialogBase(@NonNull Context context) {
        super(context, Carbon.getThemeResId(context, android.R.attr.dialogTheme));
        initLayout();
    }

    public DialogBase(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initLayout();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null), null);
    }

    @Override
    public void setContentView(@NonNull View view) {
        setContentView(view, null);
    }

    @Override
    public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
        contentView = view;
        container.addView(view);
    }

    public View getContentView() {
        return contentView;
    }

    public View getButtonContainer() {
        return buttonContainer;
    }

    public View getTitleView() {
        return titleTextView;
    }

    public View getContainer(){
        return container;
    }

    private void initLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogLayout = getLayoutInflater().inflate(R.layout.dialog_base, null);
        container = dialogLayout.findViewById(R.id.carbon_windowContent);
        super.setContentView(dialogLayout);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        if (header == null && title != null) {
            header = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_header_base, null);
            container.addView(header, 0);
            titleTextView = header.findViewById(R.id.dialog_windowTitle);
            titleTextView.setText(title);
            topDivider = header.findViewById(R.id.dialog_topDivider);
        } else if (header != null && title == null) {
            container.removeViewAt(0);
            header = null;
            titleTextView = null;
            topDivider = null;
        }
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        setTitle(getContext().getResources().getString(titleId));
    }

    @Deprecated
    public void setNegativeButton(String text, View.OnClickListener listener) {
        addButton(text, listener);
    }

    @Deprecated
    public void setPositiveButton(String text, View.OnClickListener listener) {
        addButton(text, listener);
    }
    public void addButton(String text,TextView button,View.OnClickListener listener){
        if (footer == null) {
            footer = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_footer_base, null);
            container.addView(footer);
            buttonContainer = footer.findViewById(R.id.dialog_buttonContainer);
            bottomDivider = footer.findViewById(R.id.dialog_bottomDivider);
        }
        button.setText(text);
        button.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            dismiss();
        });

        buttonContainer.addView(button);
        buttonContainer.setVisibility(View.VISIBLE);
    }
    public void addButton(String text, View.OnClickListener listener) {
        AppCompatButton button = (AppCompatButton) LayoutInflater.from(getContext()).inflate(R.layout.dialog_button_base, buttonContainer, false);
        addButton(text,button,listener);
    }

    public boolean hasButtons() {
        return buttonContainer != null;
    }

    public boolean hasTitle() {
        return titleTextView != null;
    }

    public void setMinimumWidth(int minimumWidth) {
        container.setMinimumWidth(minimumWidth);
    }

    public void setMinimumHeight(int minimumHeight) {
        container.setMinimumHeight(minimumHeight);
    }
}
