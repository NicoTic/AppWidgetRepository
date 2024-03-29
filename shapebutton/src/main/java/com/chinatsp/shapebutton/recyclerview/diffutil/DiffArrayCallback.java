package com.chinatsp.shapebutton.recyclerview.diffutil;

import androidx.recyclerview.widget.DiffUtil;

public class DiffArrayCallback<T> extends DiffUtil.Callback {
    protected T[] items, newItems;

    public void setArrays(T[] items, T[] newItems) {
        this.items = items;
        this.newItems = newItems;
    }

    public T[] getItems() {
        return items;
    }

    public T[] getNewItems() {
        return newItems;
    }
    @Override
    public int getOldListSize() {
        return items.length;
    }

    @Override
    public int getNewListSize() {
        return newItems.length;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return items[oldItemPosition] == newItems[newItemPosition];
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return items[oldItemPosition].equals(newItems[newItemPosition]);
    }
}
