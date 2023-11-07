package com.chinatsp.shapebutton.recyclerview.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.chinatsp.shapebutton.recyclerview.diffutil.DiffArrayCallback;
import com.chinatsp.shapebutton.recyclerview.OnItemClickedListener;
import com.chinatsp.shapebutton.recyclerview.OnItemClickedListener2;
import com.chinatsp.shapebutton.recyclerview.selection.SelectableItemsAdapter;
import com.chinatsp.shapebutton.recyclerview.selection.SelectionMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ArrayAdapter<VH extends RecyclerView.ViewHolder,I> extends Adapter<VH,I> implements SelectableItemsAdapter<I> {
    private OnItemClickedListener<I> onItemClickedListener;
    private Map<Class<? extends I>, OnItemClickedListener<? extends I>> onItemClickedListeners = new HashMap<>();
    private boolean diff = true;
    private DiffArrayCallback<I> diffCallback;
    private SelectionMode selectionMode = SelectionMode.NONE;
    private ArrayList<I> selectedItems = new ArrayList<>();

    public ArrayAdapter() {
        items = (I[]) new Object[0];    // doesn't really matter
    }

    public ArrayAdapter(I[] items) {
        this.items = items;
    }

    protected I[] items;

    public I getItem(int position) {
        return items[position];
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public void setDiffCallback(DiffArrayCallback<I> diffCallback) {
        this.diffCallback = diffCallback;
    }

    public void setItems(I[] items){
        I[] newItems = Arrays.copyOf(items, items.length);
        if (!diff) {
            this.items = newItems;
            return;
        }
        if (diffCallback == null)
            diffCallback = new DiffArrayCallback<>();
        diffCallback.setArrays(this.items, newItems);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.items = newItems;
        diffResult.dispatchUpdatesTo(this);
        setSelectedItems(selectedItems);
    }

    public I[] getItems() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickedListener(OnItemClickedListener<I> onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void setOnItemClickedListener(OnItemClickedListener2<I> onItemClickedListener2) {
        this.onItemClickedListener = new OnItemClickedListener<I>(){
            @Override
            public void onItemClicked(View view, I item, int position) {
                if (onItemClickedListener2 != null)
                    onItemClickedListener2.onItemClicked(item);
            }
        };
    }

    public <ItemType extends I> void setOnItemClickedListener(Class<ItemType> type,OnItemClickedListener<I> onItemClickedListener){
        this.onItemClickedListeners.put(type, onItemClickedListener);
    }

    public <ItemType extends I> void setOnItemClickedListener(Class<ItemType> type, OnItemClickedListener2<ItemType> onItemClickedListener) {
        this.onItemClickedListeners.put(type, (OnItemClickedListener<ItemType>) (view, item, position) -> {
            if (onItemClickedListener != null)
                onItemClickedListener.onItemClicked(item);
        });
    }

    public void setDiffEnabled(boolean useDiff) {
        this.diff = useDiff;
    }
    public boolean isDiffEnabled() {
        return diff;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        // 点击事件
        holder.itemView.setOnClickListener(view -> fireOnItemClickedEvent(holder.itemView, holder.getAdapterPosition()));
    }

    private void fireOnItemClickedEvent(View itemView, int position) {
        if(position < 0 || position > items.length){
            return;
        }
        I item = items[position];
        OnItemClickedListener<I> typeSpecificListener = (OnItemClickedListener<I>) onItemClickedListeners.get(item.getClass());
        if (typeSpecificListener != null)
            typeSpecificListener.onItemClicked(itemView, item, position);
        if (onItemClickedListener != null)
            onItemClickedListener.onItemClicked(itemView, item, position);
        if (selectionMode != SelectionMode.NONE && itemView.isFocusable() && itemView.isClickable())
            selectItem(item);
    }

    @Override
    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
        setSelectedItems(selectedItems);
    }

    @Override
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    @Override
    public void setSelectedIndices(List<Integer> selectedIndices) {
        setSelectedItems(Stream.of(selectedIndices).map(new Function<Integer, I>() {
            @Override
            public I apply(Integer integer) {
                return items[integer];
            }
        }).toList());
    }

    @Override
    public List<Integer> getSelectedIndices() {
        return Stream.of(selectedItems).map(new Function<I, Integer>() {
            @Override
            public Integer apply(I i) {
                return indexOf(i);
            }
        }).toList();
    }

    @Override
    public void setSelectedItems(List<I> selectedItems) {
        ArrayList<I> prevSelectedItems = this.selectedItems;
        this.selectedItems = new ArrayList<>();
        for (I item : prevSelectedItems)
            notifyItemChanged(indexOf(item), false);
        if (selectionMode != SelectionMode.NONE) {
            for (I item : selectedItems) {
                int index = indexOf(item);
                if (index != -1) {
                    this.selectedItems.add(item);
                    notifyItemChanged(index, true);
                }
            }
        }
    }

    @Override
    public ArrayList<I> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public void selectItem(I item) {
        if(selectionMode == SelectionMode.SINGLE){
            if(selectedItems.size() > 0){
                // 单选,则selectedItems集合中只有一个数据
                int deselectIndex = indexOf(selectedItems.get(0));
                selectedItems.clear();
                notifyItemChanged(deselectIndex,false);
            }
            int selectIndex = indexOf(item);
            selectedItems.add(item);
            notifyItemChanged(selectIndex,true);
        }else if(selectionMode == SelectionMode.MULTI){
            int indexOfSelectedIndex = selectedItems.indexOf(item);
            int selectedIndex = indexOf(item);
            if (indexOfSelectedIndex != -1) {
                selectedItems.remove(item);
                notifyItemChanged(selectedIndex, false);
            } else {
                selectedItems.add(item);
                notifyItemChanged(selectedIndex, true);
            }
        }
    }

    /**
     * 获取已选择列表中的Item在整个列表中的位置
     * @param item
     * @return
     */
    private int indexOf(I item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == item)
                return i;
        }
        return -1;
    }
}
