package com.chinatsp.shapebutton.popupwindow;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.IntFunction;
import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.common.Carbon;
import com.chinatsp.shapebutton.floatingActionBtn.FloatingActionMenuLeftRow;
import com.chinatsp.shapebutton.floatingActionBtn.FloatingActionMenuRightRow;
import com.chinatsp.shapebutton.recyclerview.OnItemClickedListener;
import com.chinatsp.shapebutton.recyclerview.RowFactory;
import com.chinatsp.shapebutton.recyclerview.adapter.RowArrayAdapter;
import com.chinatsp.shapebutton.recyclerview.adapter.RowListAdapter;
import com.chinatsp.shapebutton.recyclerview.component.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FloatingActionMenu extends PopupWindow {
    public static class Item implements Serializable{
        Drawable icon;
        ColorStateList tint;
        Drawable background;
        private boolean  enabled;
        private CharSequence title;

        public Item(MenuItem menuItem) {
            icon = menuItem.getIcon();
            tint = MenuItemCompat.getIconTintList(menuItem);
            enabled = menuItem.isEnabled();
            title = menuItem.getTitle();
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIconTintList(ColorStateList tint) {
            this.tint = tint;
        }

        public ColorStateList getIconTintList() {
            return tint;
        }

        public void setBackgroundDrawable(Drawable background) {
            this.background = background;
        }

        public Drawable getBackgroundDrawable() {
            return background;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setTitle(CharSequence title) {
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
    }
    // 菜单项列表
    private Item[] items;

    private Handler handler;
    // 锚点，就是以它为参考显示坐标
    private View anchor;
    private RecyclerView content;
    OnItemClickedListener<Item> listener;
    RowArrayAdapter<Item> adapter;
    public FloatingActionMenu(Context context) {
        super(new RecyclerView(context), ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        content = (RecyclerView) getContentView();
        content.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        content.setLayoutManager(new LinearLayoutManager(context));
        content.setPadding(0, content.getResources().getDimensionPixelSize(R.dimen.carbon_paddingHalf),0, content.getResources().getDimensionPixelSize(R.dimen.carbon_paddingHalf));

        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));

        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setClippingEnabled(false);

        handler = new Handler();
    }
    public void setMenuItems(Item[] items) {
        this.items = items;
    }

    public Item[] getMenuItems() {
        return items;
    }

    public View getAnchor() {
        return anchor;
    }

    public void setAnchor(View anchor) {
        this.anchor = anchor;
    }

    public void setMenu(int resId) {
        setMenu(Carbon.getMenu(getContentView().getContext(), resId));
    }

    public void setMenu(final android.view.Menu menu) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).isVisible())
                items.add(new Item(menu.getItem(i)));
        }

        this.items = com.annimon.stream.Stream.of(items).toArray(Item[]::new);
    }

    public void show(){
        int[] locationScreen = new int[2];
        if(anchor == null){
            return;
        }
        anchor.getLocationOnScreen(locationScreen);
        WindowManager windowManager = (WindowManager) anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        boolean left = locationScreen[0] < screenWidth - locationScreen[0] + anchor.getWidth();
        boolean top = locationScreen[1] < screenHeight - locationScreen[1] + anchor.getHeight();

        adapter = new RowArrayAdapter<>(items, left ? (RowFactory<Item>) parent -> new FloatingActionMenuLeftRow(parent) : new RowFactory<Item>() {
            @Override
            public Component<Item> create(ViewGroup parent) {
                return new FloatingActionMenuRightRow(parent);
            }
        });
        content.setAdapter(adapter);

        adapter.setOnItemClickedListener((view, o, position) -> {
            if (listener != null)
                listener.onItemClicked(view, o, position);
            dismiss();
        });
        content.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        super.showAtLocation(anchor, Gravity.TOP | Gravity.LEFT, 0, 0);
        if (!left & top) {  // right top
            update(locationScreen[0] - content.getMeasuredWidth() + anchor.getWidth(), locationScreen[1] + anchor.getHeight(), content.getMeasuredWidth(), content.getMeasuredHeight());
        } else if (!left & !top) {  // right bottom
            update(locationScreen[0] - content.getMeasuredWidth() + anchor.getWidth(), locationScreen[1] - content.getMeasuredHeight(), content.getMeasuredWidth(), content.getMeasuredHeight());
        } else if (left & !top) { // left bottom
            update(locationScreen[0], locationScreen[1] - content.getMeasuredHeight(), content.getMeasuredWidth(), content.getMeasuredHeight());
        } else {    // left top
            update(locationScreen[0], locationScreen[1] + anchor.getHeight(), content.getMeasuredWidth(), content.getMeasuredHeight());
        }

        for (int i = 0; i < content.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) content.getChildAt(i);
            item.setVisibility(View.INVISIBLE);
            int delay = top ? i * 50 : (items.length - 1 - i) * 50;
            handler.postDelayed(() -> item.setVisibility(View.VISIBLE), delay);
        }

        content.setAlpha(1);
        content.setVisibility(View.VISIBLE);
    }

    public void setOnItemClickedListener(OnItemClickedListener<Item> listener) {
        this.listener = listener;
    }

    public void invalidate() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
