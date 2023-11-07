package com.chinatsp.shapebutton.recyclerview.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chinatsp.shapebutton.recyclerview.RowDescriptor;
import com.chinatsp.shapebutton.recyclerview.RowFactory;
import com.chinatsp.shapebutton.recyclerview.component.Component;
import com.chinatsp.shapebutton.recyclerview.component.ItemTransformer;
import com.chinatsp.shapebutton.recyclerview.selection.SelectionMode;
import com.chinatsp.shapebutton.recyclerview.viewholder.RowViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RowListAdapter<Type> extends ListAdapter<RowViewHolder<Type>,Type>{
    // viewType和ItemView的工厂关联
    private SparseArray<RowDescriptor<? extends Type,? extends Type>> factories = new SparseArray<>();
    // viewType和Item的类型相关联
    private Map<Class<? extends Type>, Integer> types = new HashMap<>();

    public RowListAdapter() {
    }

    public <ItemType extends Type> RowListAdapter(@NonNull Class<ItemType> type, @NonNull RowFactory<ItemType> factory) {
        putFactory(type, factory);
    }

    public <ItemType extends Type> RowListAdapter(@NonNull List<ItemType> items, @NonNull RowFactory<ItemType> factory) {
        super(new ArrayList<>(items));
        putFactory((Class<ItemType>) items.get(0).getClass(), factory);
    }

    public <ItemType extends Type, FactoryType extends Type> RowListAdapter(@NonNull Class<ItemType> type, @NonNull ItemTransformer<ItemType, FactoryType> transformer, @NonNull RowFactory<FactoryType> factory) {
        putFactory(type, transformer, factory);
    }

    public <ItemType extends Type, FactoryType extends Type> RowListAdapter(@NonNull List<ItemType> items, @NonNull ItemTransformer<ItemType, FactoryType> transformer, @NonNull RowFactory<FactoryType> factory) {
        super(new ArrayList<>(items));
        putFactory((Class<ItemType>) items.get(0).getClass(), transformer, factory);
    }

    public <ItemType extends Type> void putFactory(@NonNull Class<ItemType> type, @NonNull RowFactory<ItemType> factory) {
        putFactory(type, ItemTransformer.EMPTY, factory);
    }


    public <ItemType extends Type, FactoryType extends Type> void putFactory(@NonNull Class<ItemType> type, @NonNull ItemTransformer<ItemType, FactoryType> transformer, @NonNull RowFactory<FactoryType> factory) {
        int viewType = types.containsKey(type) ? types.get(type) : types.size();
        factories.put(viewType, new RowDescriptor<>(transformer, factory));
        types.put(type, viewType);
    }

    @NonNull
    @Override
    public RowViewHolder<Type> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowDescriptor<? extends Type,? extends Type> rowDescriptor = factories.get(viewType);
        Component<Type> component = (Component<Type>) rowDescriptor.factory.create(parent);
        return new RowViewHolder<>(component);
    }

    @Override
    public void onBindViewHolder(RowViewHolder<Type> holder, int position) {
        super.onBindViewHolder(holder, position);
        Type data = getItem(position);
        Component<Type> component = holder.getComponent();
        ItemTransformer transformer = factories.get(getItemViewType(position)).transformer;
        component.setData((Type) transformer.transform(data));
        if (getSelectionMode() != SelectionMode.NONE)
            component.getView().setSelected(getSelectedIndices().contains(position));
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder<Type> holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        Type data = getItem(position);
        Component<Type> component = holder.getComponent();
        ItemTransformer transformer = factories.get(getItemViewType(position)).transformer;
        component.setData((Type) transformer.transform(data));
        if (getSelectionMode() != SelectionMode.NONE)
            component.getView().setSelected(getSelectedIndices().contains(position));
    }

    @Override
    public int getItemViewType(int position) {
        return types.get(getItems().get(position));
    }
}
