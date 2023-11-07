package com.chinatsp.shapebutton.floatingActionBtn;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Menu;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatImageView;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.popupwindow.FloatingActionMenu;
import com.chinatsp.shapebutton.recyclerview.OnItemClickedListener;

public class FloatingActionButton extends AppCompatImageView {
    FloatingActionMenu floatingActionMenu;
    public FloatingActionButton(@NonNull Context context) {
        super(context, null, R.attr.carbon_fabStyle);
        initFloatingActionButton(null, R.attr.carbon_fabStyle, R.style.carbon_FloatingActionButton);
    }

    public FloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.attr.carbon_fabStyle);
        initFloatingActionButton(attrs, R.attr.carbon_fabStyle, R.style.carbon_FloatingActionButton);
    }

    public FloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFloatingActionButton(attrs, defStyleAttr, R.style.carbon_FloatingActionButton);
    }

    private void initFloatingActionButton(AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatingActionButton, defStyleAttr, defStyleRes);

        if (a.hasValue(R.styleable.FloatingActionButton_carbon_menu)) {
            int resId = a.getResourceId(R.styleable.FloatingActionButton_carbon_menu, 0);
            if (resId != 0)
                setMenu(resId);
        }

        a.recycle();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        invalidateMenu();
    }

    public void invalidateMenu() {
        if (floatingActionMenu != null)
            floatingActionMenu.invalidate();
    }

    public void setMenu(int resId) {
        floatingActionMenu = new FloatingActionMenu(getContext());
        floatingActionMenu.setMenu(resId);
        floatingActionMenu.setAnchor(this);

        setOnClickListener(__ -> floatingActionMenu.show());
    }

    public void setMenu(Menu menu) {
        if (menu != null) {
            floatingActionMenu = new FloatingActionMenu(getContext());
            floatingActionMenu.setMenu(menu);
            floatingActionMenu.setAnchor(this);

            setOnClickListener(__ -> floatingActionMenu.show());
        } else {
            floatingActionMenu = null;
            setOnClickListener(null);
        }
    }

    public void setMenuItems(FloatingActionMenu.Item[] items) {
        floatingActionMenu = new FloatingActionMenu(getContext());
        floatingActionMenu.setMenuItems(items);
        floatingActionMenu.setAnchor(this);

        setOnClickListener(__ -> floatingActionMenu.show());
    }

    public FloatingActionMenu getFloatingActionMenu() {
        return floatingActionMenu;
    }

    public void setOnItemClickedListener(OnItemClickedListener<FloatingActionMenu.Item> listener) {
        if (floatingActionMenu != null)
            floatingActionMenu.setOnItemClickedListener(listener);
    }
}
