package com.chinatsp.shapebutton.recyclerview;

import android.view.View;

public interface OnItemClickedListener<Type> {
    void onItemClicked(View view, Type item, int position);
}
