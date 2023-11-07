package com.chinatsp.shapebutton.recyclerview;


import com.chinatsp.shapebutton.recyclerview.component.ItemTransformer;

public class RowDescriptor<TypeFrom, TypeTo> {
    public ItemTransformer<TypeFrom, TypeTo> transformer;
    public RowFactory<TypeTo> factory;

    public RowDescriptor(ItemTransformer<TypeFrom, TypeTo> transformer, RowFactory<TypeTo> factory) {
        this.transformer = transformer;
        this.factory = factory;
    }
}
