package com.chinatsp.shapebutton.radiogroup;

import android.widget.Checkable;

/**
 * 当一个可选中视图（即实现了Checkable接口的View）的选中状态发生变化时调用的回调的接口定义
 */
public interface OnCheckableChangeListener {

    void onCheckedChanged(Checkable view, boolean isChecked);
}
