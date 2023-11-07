package com.chinatsp.shapebutton.floatingActionBtn;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chinatsp.shapebutton.R;
import com.chinatsp.shapebutton.databinding.CarbonFloatingactionmenuLeftBinding;
import com.chinatsp.shapebutton.popupwindow.FloatingActionMenu;
import com.chinatsp.shapebutton.recyclerview.component.LayoutComponent;

public class FloatingActionMenuLeftRow extends LayoutComponent<FloatingActionMenu.Item> {
    private final CarbonFloatingactionmenuLeftBinding binding;
    public FloatingActionMenuLeftRow(@NonNull ViewGroup parent) {
        super(parent, R.layout.carbon_floatingactionmenu_left);
        binding = CarbonFloatingactionmenuLeftBinding.bind(getView());
    }

    @Override
    protected void bindData(FloatingActionMenu.Item data) {
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
