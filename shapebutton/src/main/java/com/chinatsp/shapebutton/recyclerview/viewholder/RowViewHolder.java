package com.chinatsp.shapebutton.recyclerview.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinatsp.shapebutton.recyclerview.component.Component;

public class RowViewHolder<Type> extends RecyclerView.ViewHolder {
    private Component<Type> component;

    public RowViewHolder(Component<Type> component) {
        super(component.getView());
        this.component = component;
    }

    public Component<Type> getComponent() {
        return component;
    }
}
