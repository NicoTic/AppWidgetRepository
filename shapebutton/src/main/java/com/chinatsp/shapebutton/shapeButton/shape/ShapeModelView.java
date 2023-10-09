package com.chinatsp.shapebutton.shapeButton.shape;

import com.google.android.material.shape.ShapeAppearanceModel;

public interface ShapeModelView {
    void setShapeModel(ShapeAppearanceModel shapeModel);
    ShapeAppearanceModel getShapeModel();

    void setCornerCut(float cornerCut);

    void setCornerRadius(float cornerRadius);
}
