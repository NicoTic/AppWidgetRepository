package com.chinatsp.shapebutton.recyclerview.adapter;

import androidx.recyclerview.widget.RecyclerView;

public abstract class Adapter<VH extends RecyclerView.ViewHolder,I> extends RecyclerView.Adapter<VH> {
    abstract <Type extends I> Type getItem(int position);
}
