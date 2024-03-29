package com.chinatsp.shapebutton.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chinatsp.shapebutton.R;

public class TextDialog extends DialogBase {
    private TextView textView;

    public TextDialog(@NonNull Context context) {
        super(context);
        initProgressDialog();
    }

    public TextDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initProgressDialog();
    }

    private void initProgressDialog() {
        setContentView(R.layout.carbon_textdialog);
        textView = findViewById(R.id.carbon_dialogText);
        setMinimumWidth(0);
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }

    public void setText(CharSequence text) {
        textView.setText(text);
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            setMinimumWidth(0);
        } else {
            textView.setVisibility(View.VISIBLE);
            setMinimumWidth(getContext().getResources().getDimensionPixelSize(R.dimen.carbon_dialogMinimumWidth));
        }
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);
        int padding = getContext().getResources().getDimensionPixelSize(R.dimen.carbon_dialogPadding);
        getContentView().setPadding(padding, 0, padding, hasButtons() ? 0 : padding);
    }

    @Override
    public void addButton(String text, View.OnClickListener listener) {
        super.addButton(text, listener);
        int padding = getContext().getResources().getDimensionPixelSize(R.dimen.carbon_dialogPadding);
        getContentView().setPadding(padding, hasTitle() ? 0 : padding, padding, 0);
    }
}
