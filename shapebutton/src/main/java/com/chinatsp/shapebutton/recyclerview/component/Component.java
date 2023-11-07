package com.chinatsp.shapebutton.recyclerview.component;

import android.content.Context;
import android.view.View;

public abstract class Component<Type> {
    public View view;
    private Context context;
    private Type data;

    public Context getContext() {
        return context;
    }

    public void setView(View view) {
        this.view = view;
        this.context = view.getContext();
    }

    public Type getData() {
        return data;
    }

    public void setData(Type data) {
        this.data = data;
        bindData(data);
    }

    protected abstract void bindData(Type data);

    public View getView() {
        return view;
    }
}
