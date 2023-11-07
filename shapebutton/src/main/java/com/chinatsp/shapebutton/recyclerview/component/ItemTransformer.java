package com.chinatsp.shapebutton.recyclerview.component;

public interface ItemTransformer<TypeFrom, TypeTo> {
    ItemTransformer EMPTY = new ItemTransformer() {
        @Override
        public Object transform(Object item) {
            return item;
        }
    };

    TypeTo transform(TypeFrom item);
}
