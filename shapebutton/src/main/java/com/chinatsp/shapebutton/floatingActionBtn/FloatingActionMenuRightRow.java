package com.chinatsp.shapebutton.floatingActionBtn;

import android.view.ViewGroup;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.databinding.CarbonFloatingactionmenuRightBinding;
import com.chinatsp.shapebutton.popupwindow.FloatingActionMenu;
import com.chinatsp.shapebutton.recyclerview.component.LayoutComponent;

public class FloatingActionMenuRightRow extends LayoutComponent<FloatingActionMenu.Item> {

    private final CarbonFloatingactionmenuRightBinding binding;

    public FloatingActionMenuRightRow(ViewGroup parent) {
        super(parent, R.layout.carbon_floatingactionmenu_right);
        binding = CarbonFloatingactionmenuRightBinding.bind(getView());
    }

    @Override
    public void bindData(FloatingActionMenu.Item data) {
//        binding.carbonFab.setImageDrawable(data.getIcon());
        binding.carbonFab.setEnabled(data.isEnabled());
        binding.carbonTooltip.setText(data.getTitle());
        binding.carbonTooltip.setEnabled(data.isEnabled());
        if (data.getIconTintList() != null)
            binding.carbonFab.setImageTintList(data.getIconTintList());
        if (data.getBackgroundDrawable() != null)
            binding.carbonFab.setBackgroundDrawable(data.getBackgroundDrawable());
    }
}
