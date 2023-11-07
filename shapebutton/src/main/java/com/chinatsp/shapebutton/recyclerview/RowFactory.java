package com.chinatsp.shapebutton.recyclerview;

import android.view.ViewGroup;

import com.chinatsp.shapebutton.recyclerview.component.Component;

public interface RowFactory<Type>{
    Component<Type> create(ViewGroup parent);
}
