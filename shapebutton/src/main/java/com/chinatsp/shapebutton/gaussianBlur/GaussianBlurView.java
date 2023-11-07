package com.chinatsp.shapebutton.gaussianBlur;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.RectF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.chinatsp.shapebutton.R;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

public class GaussianBlurView extends View{
    private static final String TAG = GaussianBlurView.class.getSimpleName();
    // 图片缩小比例
    private float mDownsampleFactor; // default 4
    // 颜色
    private int mOverlayColor; // default #aaffffff
    // 模糊半径
    private float mBlurRadius; // default 10dp (0 < r <= 25)
    // 模糊前的图片；模糊后的图片
    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;
    private final Rect mRectSrc = new Rect(), mRectDst = new Rect();
    // 窗口
    private View mDecorView;
    // 是否正在渲染
    private boolean mIsRendering;
    // 是否刷新
    private boolean mDirty;

    private float cornerRadius;

    private static int[] cornerRadiusIds = new int[]{
            R.styleable.GaussianBlurView_blurCornerRadiusTopStart,
            R.styleable.GaussianBlurView_blurCornerRadiusTopEnd,
            R.styleable.GaussianBlurView_blurCornerRadiusBottomStart,
            R.styleable.GaussianBlurView_blurCornerRadiusBottomEnd,
            R.styleable.GaussianBlurView_blurCornerRadius,
    };

    public GaussianBlurView(Context context) {
        super(context);
        initView(null,0);
    }

    public GaussianBlurView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs,0);
    }

    public GaussianBlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs,defStyleAttr);
    }

    private void initView(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GaussianBlurView,defStyleAttr,0);
        mBlurRadius = a.getDimension(R.styleable.GaussianBlurView_blurRadius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getContext().getResources().getDisplayMetrics()));
        mDownsampleFactor = a.getFloat(R.styleable.GaussianBlurView_blurFactor, 4);
        mOverlayColor = a.getColor(R.styleable.GaussianBlurView_blurOverlayColor, 0xAA000000);
        initCornerCutRadius(a, cornerRadiusIds);
        a.recycle();
    }

    private void initCornerCutRadius(TypedArray a, int[] ids) {
        int shape_cornerRadiusTopStart = ids[0];
        int shape_cornerRadiusTopEnd = ids[1];
        int shape_cornerRadiusBottomStart = ids[2];
        int shape_cornerRadiusBottomEnd = ids[3];
        int shape_cornerRadius = ids[4];

        float cornerRadius = Math.max(a.getDimension(shape_cornerRadius, 0), 0.1f);
        this.cornerRadius = cornerRadius;
        float cornerRadiusTopStart = a.getDimension(shape_cornerRadiusTopStart, cornerRadius);
        float cornerRadiusTopEnd = a.getDimension(shape_cornerRadiusTopEnd, cornerRadius);
        float cornerRadiusBottomStart = a.getDimension(shape_cornerRadiusBottomStart, cornerRadius);
        float cornerRadiusBottomEnd = a.getDimension(shape_cornerRadiusBottomEnd, cornerRadius);

        ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                .setTopLeftCorner(new RoundedCornerTreatment(cornerRadiusTopStart))
                .setTopRightCorner(new RoundedCornerTreatment(cornerRadiusTopEnd))
                .setBottomLeftCorner(new RoundedCornerTreatment(cornerRadiusBottomStart))
                .setBottomRightCorner(new RoundedCornerTreatment(cornerRadiusBottomEnd))
                .build();
        setShapeModel(model);
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        ShapeAppearanceModel model = ShapeAppearanceModel.builder()
                .setAllCorners(new RoundedCornerTreatment(cornerRadius))
                .build();
        setShapeModel(model);
    }

    public void setBlurRadius(@FloatRange(from = 0f,to = 25f) float radius) {
        if (mBlurRadius != radius) {
            mBlurRadius = radius;
            mDirty = true;
            invalidate();
        }
    }

    public void setSampleFactor(float factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("factor must be > 0.");
        }

        if (mDownsampleFactor != factor) {
            mDownsampleFactor = factor;
            mDirty = true;
            invalidate();
        }
    }

    public void setOverlayColor(int color) {
        if (mOverlayColor != color) {
            mOverlayColor = color;
            invalidate();
        }
    }

    private void releaseBitmap() {
        if (mBlurInput != null) {
            mBlurInput.destroy();
            mBlurInput = null;
        }
        if (mBlurOutput != null) {
            mBlurOutput.destroy();
            mBlurOutput = null;
        }
        if (mBitmapToBlur != null) {
            mBitmapToBlur.recycle();
            mBitmapToBlur = null;
        }
        if (mBlurredBitmap != null) {
            mBlurredBitmap.recycle();
            mBlurredBitmap = null;
        }
    }

    private void releaseScript() {
        if (mRenderScript != null) {
            mRenderScript.destroy();
            mRenderScript = null;
        }
        if (mBlurScript != null) {
            mBlurScript.destroy();
            mBlurScript = null;
        }
    }

    protected void release() {
        releaseBitmap();
        releaseScript();
    }

    protected View getActivityDecorView() {
        Context ctx = getContext();
        for (int i = 0; i < 4 && !(ctx instanceof Activity) && ctx instanceof ContextWrapper; i++) {
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }
        if (ctx instanceof Activity) {
            return ((Activity) ctx).getWindow().getDecorView();
        } else {
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 获取窗口的DecoreView
        mDecorView = getActivityDecorView();
        if (mDecorView != null) {
            mDecorView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDecorView != null) {
            mDecorView.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
        }
        release();
        super.onDetachedFromWindow();
    }

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            Log.d(TAG,"onPreDraw: before_mBlurredBitmap="+mBlurredBitmap);
            final int[] locations = new int[2];
            Bitmap oldBmp = mBlurredBitmap;
            View decor = mDecorView;
            if (decor != null && isShown() && prepared()) {
                Log.d(TAG,"onPreDraw: after_mBlurredBitmap="+mBlurredBitmap);
                boolean redrawBitmap = mBlurredBitmap != oldBmp;
                decor.getLocationOnScreen(locations);
                int x = -locations[0];
                int y = -locations[1];

                getLocationOnScreen(locations);
                x += locations[0];
                y += locations[1];

                // just erase transparent
//                mBitmapToBlur.eraseColor(Color.parseColor("#aaff0000"));

                int rc = mBlurringCanvas.save();
                mIsRendering = true;
                try {
                    mBlurringCanvas.scale(1.f * mBitmapToBlur.getWidth() / getWidth(), 1.f * mBitmapToBlur.getHeight() / getHeight());
                    mBlurringCanvas.translate(-x, -y);
                    Log.d(TAG,"decor.rootView="+decor.getRootView() + " ,decor.rootView is CoordinatorLayout = " + (decor.getRootView() instanceof CoordinatorLayout)
                    + " ,rootView="+getRootView());
                    // 绘制ContentView中的内容到Bitmap--start
//                    View contentView = decor.findViewById(android.R.id.content);
//                    if (contentView.getBackground() != null) {
//                        contentView.getBackground().draw(mBlurringCanvas);
//                    }
//                    contentView.draw(mBlurringCanvas);
                    // 绘制ContentView中的内容到Bitmap--end

                    // 绘制DecorView中的内容到Bitmap--start
                    if (decor.getBackground() != null) {
                        decor.getBackground().draw(mBlurringCanvas);
                    }
                    decor.draw(mBlurringCanvas);
                    // 绘制DecorView中的内容到Bitmap--start
                } catch (StopException e) {
                } finally {
                    mIsRendering = false;
                    mBlurringCanvas.restoreToCount(rc);
                }

                blur(mBitmapToBlur, mBlurredBitmap);

                if (redrawBitmap) {
                    invalidate();
                }
            }

            return true;
        }
    };

    protected void blur(Bitmap bitmapToBlur, Bitmap blurredBitmap) {
        mBlurInput.copyFrom(bitmapToBlur);
        mBlurScript.setInput(mBlurInput);
        mBlurScript.forEach(mBlurOutput);
        mBlurOutput.copyTo(blurredBitmap);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mIsRendering) {
            throw STOP_EXCEPTION;
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 将模糊后的图像绘制在canvas上
        if (mBlurredBitmap != null) {
            mRectSrc.right = mBlurredBitmap.getWidth();
            mRectSrc.bottom = mBlurredBitmap.getHeight();
            mRectDst.right = getWidth();
            mRectDst.bottom = getHeight();
            canvas.drawBitmap(mBlurredBitmap, mRectSrc, mRectDst, null);
        }
        // 在模糊的图像上添加一层颜色遮罩
        canvas.drawColor(mOverlayColor);

    }

    private boolean prepared() {
        if (mBlurRadius == 0) {
            release();
            return false;
        }

        float downsampleFactor = mDownsampleFactor;

        Log.d(TAG,"prepared: mDirty="+mDirty + " ,mRenderScript="+mRenderScript);
        if (mDirty || mRenderScript == null) {
            if (mRenderScript == null) {
                try {
                    mRenderScript = RenderScript.create(getContext());
                    mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
                } catch (RSRuntimeException e) {
                    if (isDebug(getContext())) {
                        if (e.getMessage() != null && e.getMessage().startsWith("Error loading RS jni library: java.lang.UnsatisfiedLinkError:")) {
                            throw new RuntimeException("Error loading RS jni library, Upgrade buildToolsVersion=\"24.0.2\" or higher may solve this issue");
                        } else {
                            throw e;
                        }
                    } else {
                        // In release mode, just ignore
                        releaseScript();
                        return false;
                    }
                }
            }

            mDirty = false;
            float radius = mBlurRadius / downsampleFactor;
            if (radius > 25) {
                downsampleFactor = downsampleFactor * radius / 25;
                radius = 25;
            }
            mBlurScript.setRadius(radius);
        }
        final int width = getWidth();
        final int height = getHeight();

        int scaledWidth = Math.max(1, (int) (width / downsampleFactor));
        int scaledHeight = Math.max(1, (int) (height / downsampleFactor));
        Log.d(TAG,"prepared: mBlurringCanvas="+mBlurringCanvas + " ,mBlurredBitmap="+mBlurredBitmap + " ," );
        if (mBlurringCanvas == null || mBlurredBitmap == null
                || mBlurredBitmap.getWidth() != scaledWidth
                || mBlurredBitmap.getHeight() != scaledHeight) {
            releaseBitmap();

            boolean r = false;
            try {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }
                mBlurringCanvas = new Canvas(mBitmapToBlur);

                mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur,
                        Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
                mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());

                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }

                r = true;
            } catch (OutOfMemoryError e) {
                // Bitmap.createBitmap() may cause OOM error
                // Simply ignore and fallback
            } finally {
                if (!r) {
                    releaseBitmap();
                    return false;
                }
            }
        }
        return true;
    }

    private ShapeAppearanceModel shapeModel = new ShapeAppearanceModel();
    private MaterialShapeDrawable shadowDrawable = new MaterialShapeDrawable(shapeModel);
    private RectF boundsRect = new RectF();

    public void setShapeModel(ShapeAppearanceModel shapeModel) {
        this.shapeModel = shapeModel;
        shadowDrawable = new MaterialShapeDrawable(shapeModel);
        if (getWidth() > 0 && getHeight() > 0){
            updateCorners();
        }
    }

    private void updateCorners() {
        if (!isShapeRect(shapeModel, boundsRect)) {
            setClipToOutline(true);
        }
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (isShapeRect(shapeModel, boundsRect)) {
                    outline.setRect(0, 0, getWidth(), getHeight());
                } else {
                    shadowDrawable.setBounds(0, 0, getWidth(), getHeight());
                    shadowDrawable.getOutline(outline);
                }
            }
        });
    }

    public static boolean isShapeRect(ShapeAppearanceModel model, RectF bounds) {
        return model.getTopLeftCornerSize().getCornerSize(bounds) <= 0.2f &&
                model.getTopRightCornerSize().getCornerSize(bounds) <= 0.2f &&
                model.getBottomLeftCornerSize().getCornerSize(bounds) <= 0.2f &&
                model.getBottomRightCornerSize().getCornerSize(bounds) <= 0.2f;
    }

    public ShapeAppearanceModel getShapeModel() {
        return this.shapeModel;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed)
            return;

        if (getWidth() == 0 || getHeight() == 0)
            return;

        updateCorners();
    }

    private static class StopException extends RuntimeException {
    }

    private static StopException STOP_EXCEPTION = new StopException();

    // android:debuggable="true" in AndroidManifest.xml (auto set by build tool)
    static Boolean DEBUG = null;

    static boolean isDebug(Context ctx) {
        if (DEBUG == null && ctx != null) {
            DEBUG = (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return DEBUG == Boolean.TRUE;
    }
}
