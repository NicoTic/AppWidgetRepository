package com.chinatsp.shapebutton;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatButton;

import com.chinatsp.shapebutton.common.Carbon;
import com.chinatsp.shapebutton.ripple.RippleDrawable;
import com.chinatsp.shapebutton.ripple.RippleView;
import com.chinatsp.shapebutton.shadow.ShadowView;
import com.chinatsp.shapebutton.shape.ShapeModelView;
import com.chinatsp.shapebutton.stateAnimator.StateAnimator;
import com.chinatsp.shapebutton.stateAnimator.StateAnimatorView;
import com.chinatsp.shapebutton.stroke.StrokeView;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

public class ShapeButton extends AppCompatButton
        implements ShadowView,
        ShapeModelView,
        RippleView,
        StrokeView,
        StateAnimatorView {
    public ShapeButton(@NonNull Context context) {
        super(context);
        initButton(null, android.R.attr.buttonStyle, R.style.carbon_Button);
    }

    public ShapeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initButton(attrs, android.R.attr.buttonStyle, R.style.carbon_Button);
    }

    public ShapeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton(attrs, defStyleAttr, R.style.carbon_Button);
    }

    public ShapeButton(Context context, String text, OnClickListener listener) {
        super(context);
        initButton(null, android.R.attr.buttonStyle, R.style.carbon_Button);
        setText(text);
        setOnClickListener(listener);
    }

    private static int[] elevationIds = new int[]{
            R.styleable.shape_button_carbon_elevation,
            R.styleable.shape_button_carbon_elevationShadowColor,
            R.styleable.shape_button_carbon_elevationAmbientShadowColor,
            R.styleable.shape_button_carbon_elevationSpotShadowColor
    };

    private static int[] cornerCutRadiusIds = new int[]{
            R.styleable.shape_button_carbon_cornerRadiusTopStart,
            R.styleable.shape_button_carbon_cornerRadiusTopEnd,
            R.styleable.shape_button_carbon_cornerRadiusBottomStart,
            R.styleable.shape_button_carbon_cornerRadiusBottomEnd,
            R.styleable.shape_button_carbon_cornerRadius,
            R.styleable.shape_button_carbon_cornerCutTopStart,
            R.styleable.shape_button_carbon_cornerCutTopEnd,
            R.styleable.shape_button_carbon_cornerCutBottomStart,
            R.styleable.shape_button_carbon_cornerCutBottomEnd,
            R.styleable.shape_button_carbon_cornerCut
    };

    private static int[] rippleIds = new int[]{
            R.styleable.shape_button_carbon_rippleColor,
            R.styleable.shape_button_carbon_rippleStyle,
            R.styleable.shape_button_carbon_rippleHotspot,
            R.styleable.shape_button_carbon_rippleRadius
    };

    private static int[] strokeIds = new int[]{
            R.styleable.shape_button_carbon_stroke,
            R.styleable.shape_button_carbon_strokeWidth
    };

    protected TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private void initButton(AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.shape_button, defStyleAttr, defStyleRes);
        Carbon.initElevation(this, a, elevationIds);
        Carbon.initCornerCutRadius(this,a,cornerCutRadiusIds);
        Carbon.initRippleDrawable(this,a,rippleIds);
        Carbon.initStroke(this,a,strokeIds);
        a.recycle();
    }



    // -------------------------------
    // shadow
    // -------------------------------
    private float elevation = 0;
    private float translationZ = 0;
    private ColorStateList ambientShadowColor, spotShadowColor;
    @Override
    public float getElevation() {
        return elevation;
    }

    @Override
    public void setElevation(float elevation) {
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setElevation(elevation);
            super.setTranslationZ(translationZ);
        } else if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            if (ambientShadowColor == null || spotShadowColor == null) {
                super.setElevation(elevation);
                super.setTranslationZ(translationZ);
            } else {
                super.setElevation(0);
                super.setTranslationZ(0);
            }
        } else if (elevation != this.elevation && getParent() != null) {
            ((View) getParent()).postInvalidate();
        }
        this.elevation = elevation;
    }

    @Override
    public float getTranslationZ() {
        return translationZ;
    }

    public void setTranslationZ(float translationZ) {
        if (translationZ == this.translationZ)
            return;
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setTranslationZ(translationZ);
        } else if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            if (ambientShadowColor == null || spotShadowColor == null) {
                super.setTranslationZ(translationZ);
            } else {
                super.setTranslationZ(0);
            }
        } else if (translationZ != this.translationZ && getParent() != null) {
            ((View) getParent()).postInvalidate();
        }
        this.translationZ = translationZ;
    }

    @Override
    public ColorStateList getElevationShadowColor() {
        return ambientShadowColor;
    }

    @Override
    public void setElevationShadowColor(ColorStateList shadowColor) {
        ambientShadowColor = spotShadowColor = shadowColor;
        setElevation(elevation);
        setTranslationZ(translationZ);
    }

    @Override
    public void setElevationShadowColor(int color) {
        ambientShadowColor = spotShadowColor = ColorStateList.valueOf(color);
        setElevation(elevation);
        setTranslationZ(translationZ);
    }

    @Override
    public void setOutlineAmbientShadowColor(ColorStateList color) {
        ambientShadowColor = color;
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setOutlineAmbientShadowColor(color.getColorForState(getDrawableState(), color.getDefaultColor()));
        } else {
            setElevation(elevation);
            setTranslationZ(translationZ);
        }
    }

    @Override
    public void setOutlineAmbientShadowColor(int color) {
        setOutlineAmbientShadowColor(ColorStateList.valueOf(color));
    }

    @Override
    public int getOutlineAmbientShadowColor() {
        return ambientShadowColor.getDefaultColor();
    }

    @Override
    public void setOutlineSpotShadowColor(int color) {
        setOutlineSpotShadowColor(ColorStateList.valueOf(color));
    }

    @Override
    public void setOutlineSpotShadowColor(ColorStateList color) {
        spotShadowColor = color;
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setOutlineSpotShadowColor(color.getColorForState(getDrawableState(), color.getDefaultColor()));
        } else {
            setElevation(elevation);
            setTranslationZ(translationZ);
        }

    }

    @Override
    public int getOutlineSpotShadowColor() {
        return ambientShadowColor.getDefaultColor();
    }

    @Override
    public boolean hasShadow() {
        return false;
    }

    @Override
    public void drawShadow(Canvas canvas) {

    }

    @Override
    public void draw(Canvas canvas) {
        boolean c = !Carbon.isShapeRect(shapeModel, boundsRect);

        if (Carbon.IS_PIE_OR_HIGHER) {
            if (spotShadowColor != null)
                super.setOutlineSpotShadowColor(spotShadowColor.getColorForState(getDrawableState(), spotShadowColor.getDefaultColor()));
            if (ambientShadowColor != null)
                super.setOutlineAmbientShadowColor(ambientShadowColor.getColorForState(getDrawableState(), ambientShadowColor.getDefaultColor()));
        }

        // 判断如果不是圆角矩形,需要使用轮廓Path,绘制一下Path,不然显示会很奇怪
        if (getWidth() > 0 && getHeight() > 0 && ((c && !Carbon.IS_LOLLIPOP_OR_HIGHER) || !shapeModel.isRoundRect(boundsRect))) {
            int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            drawInternal(canvas);
            paint.setXfermode(Carbon.CLEAR_MODE);
            if (c) {
                cornersMask.setFillType(Path.FillType.INVERSE_WINDING);
                canvas.drawPath(cornersMask, paint);
            }
            canvas.restoreToCount(saveCount);
            paint.setXfermode(null);
        }else{
            drawInternal(canvas);
        }
    }

    public void drawInternal(@NonNull Canvas canvas) {
        super.draw(canvas);
        if(stroke!=null){
            drawStroke(canvas);
        }
        if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Over)
            rippleDrawable.draw(canvas);
    }

    // -------------------------------
    // shape
    // -------------------------------
    private ShapeAppearanceModel shapeModel = new ShapeAppearanceModel();
    private MaterialShapeDrawable shadowDrawable = new MaterialShapeDrawable(shapeModel);
    @Override
    public void setShapeModel(ShapeAppearanceModel shapeModel) {
        this.shapeModel = shapeModel;
        shadowDrawable = new MaterialShapeDrawable(shapeModel);
        if (getWidth() > 0 && getHeight() > 0)
            updateCorners();
        if (!Carbon.IS_LOLLIPOP_OR_HIGHER)
            postInvalidate();
    }
    // View的轮廓形状
    private RectF boundsRect = new RectF();
    // View的轮廓形状形成的Path路径
    private Path cornersMask = new Path();

    /**
     * 更新圆角
     */
    private void updateCorners() {
        if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            // 如果不是矩形,裁剪View的轮廓
            if (!Carbon.isShapeRect(shapeModel, boundsRect)){
                setClipToOutline(true);
            }
            //该方法返回一个Outline对象，它描述了该视图的形状。
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Carbon.isShapeRect(shapeModel, boundsRect)) {
                        outline.setRect(0, 0, getWidth(), getHeight());
                    } else {
                        shadowDrawable.setBounds(0, 0, getWidth(), getHeight());
                        shadowDrawable.setShadowCompatibilityMode(MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER);
                        shadowDrawable.getOutline(outline);
                    }
                }
            });
        }
        // 拿到圆角矩形的形状
        boundsRect.set(shadowDrawable.getBounds());
        // 拿到圆角矩形的Path
        shadowDrawable.getPathForSize(getWidth(), getHeight(), cornersMask);
    }

    @Override
    public ShapeAppearanceModel getShapeModel() {
        return this.shapeModel;
    }

    @Override
    public void setCornerCut(float cornerCut) {
        shapeModel = ShapeAppearanceModel.builder().setAllCorners(new CutCornerTreatment(cornerCut)).build();
        setShapeModel(shapeModel);
    }

    @Override
    public void setCornerRadius(float cornerRadius) {
        shapeModel = ShapeAppearanceModel.builder().setAllCorners(new RoundedCornerTreatment(cornerRadius)).build();
        setShapeModel(shapeModel);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed)
            return;

        if (getWidth() == 0 || getHeight() == 0)
            return;

        updateCorners();
        if (rippleDrawable != null)
            rippleDrawable.setBounds(0, 0, getWidth(), getHeight());
    }

    // -------------------------------
    // ripple
    // -------------------------------

    private RippleDrawable rippleDrawable;

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {

        if (rippleDrawable != null && event.getAction() == MotionEvent.ACTION_DOWN)
            rippleDrawable.setHotspot(event.getX(),event.getY());

        return super.dispatchTouchEvent(event);
    }

    @Override
    public RippleDrawable getRippleDrawable() {
        return rippleDrawable;
    }

    @Override
    public void setRippleDrawable(RippleDrawable newRipple) {
        if (rippleDrawable != null) {
            rippleDrawable.setCallback(null);
            if (rippleDrawable.getStyle() == RippleDrawable.Style.Background)
                super.setBackgroundDrawable(rippleDrawable.getBackground());
        }

        if (newRipple != null) {
            newRipple.setCallback(this);
            newRipple.setBounds(0, 0, getWidth(), getHeight());
            newRipple.setState(getDrawableState());
            ((Drawable) newRipple).setVisible(getVisibility() == VISIBLE, false);
            if (newRipple.getStyle() == RippleDrawable.Style.Background)
                super.setBackgroundDrawable((Drawable) newRipple);
        }

        rippleDrawable = newRipple;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (rippleDrawable != null && rippleDrawable.getStyle() != RippleDrawable.Style.Background)
            rippleDrawable.setState(getDrawableState());

        if (stateAnimator != null)
            stateAnimator.setState(getDrawableState());
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || rippleDrawable == who;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        invalidateParentIfNeeded();
    }

    @Override
    public void invalidate(@NonNull Rect dirty) {
        super.invalidate(dirty);
        invalidateParentIfNeeded();
    }

    @Override
    public void invalidate(int l, int t, int r, int b) {
        super.invalidate(l, t, r, b);
        invalidateParentIfNeeded();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateParentIfNeeded();
    }

    private void invalidateParentIfNeeded() {
        if (getParent() == null || !(getParent() instanceof View))
            return;

        if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Borderless)
            ((View) getParent()).invalidate();
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (background instanceof RippleDrawable) {
            setRippleDrawable((RippleDrawable) background);
            return;
        }

        if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Background) {
            rippleDrawable.setCallback(null);
            rippleDrawable = null;
        }
        super.setBackgroundDrawable(background);
    }

    // -------------------------------
    // stroke
    // -------------------------------

    private ColorStateList stroke;
    private float strokeWidth;
    private Paint strokePaint;

    private void drawStroke(Canvas canvas) {
        strokePaint.setStrokeWidth(strokeWidth * 2);
        strokePaint.setColor(stroke.getColorForState(getDrawableState(), stroke.getDefaultColor()));
        cornersMask.setFillType(Path.FillType.WINDING);
        canvas.drawPath(cornersMask, strokePaint);
    }

    @Override
    public void setStroke(ColorStateList colorStateList) {
        stroke = colorStateList;

        if (stroke == null)
            return;

        if (strokePaint == null) {
            strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokePaint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    public void setStroke(int color) {
        setStroke(ColorStateList.valueOf(color));
    }

    @Override
    public ColorStateList getStroke() {
        return stroke;
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    public float getStrokeWidth() {
        return strokeWidth;
    }

    // -------------------------------
    // stateAnimator
    // -------------------------------

    private StateAnimator stateAnimator = new StateAnimator(this);
    @Override
    public void setStateAnimator(StateAnimator stateAnimator) {
        this.stateAnimator = stateAnimator;
    }

    @Override
    public StateAnimator getStateAnimator() {
        return stateAnimator;
    }
}
