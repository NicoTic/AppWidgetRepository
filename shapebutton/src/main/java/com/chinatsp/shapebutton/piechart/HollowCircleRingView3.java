package com.chinatsp.shapebutton.piechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.chinatsp.shapebutton.R;

public class HollowCircleRingView3 extends View {
    private static final String TAG = HollowCircleRingView3.class.getSimpleName();
    private int DEFAULT_DEMENSION;
    private Context mContext;
    // 外部圆的半径
    private int mOuterRadius;
    // 内部圆的半径
    private float mInnerRadius;
    // 扇形圆弧所属的圆的半径
    private float mViewRadius;
    // View的中心坐标
    private float mViewCenterX,mViewCenterY;
    // 绘制外部圆
    private RectF mOuterCircleRect;
    private Paint mOuterCirclePaint;
    // 绘制内部圆
    private RectF mInnerCircleRect;
    private Paint mInnerCirclePaint;
    // 绘制扇形圆弧
    private RectF mArcRect;
    private Paint mArcPaint;
    // 绘制刻度和文字
    private Paint mTextPaint,mScalePaint;
    private float mRingWidth = 20; // 外部圆和内部圆形成的圆环宽度，单位为像素
    private float mArcWidth = 20; // 扇形圆弧的宽度，单位为像素
    private float startAngle = 330; // 扇形圆弧的起始角度
    private float sweepAngle = 60; // 弧度角度
    private float endAngle = 0f; //扇形圆弧的结束角度
    private Drawable startDrawable; // 起始端的图片
    private Drawable endDrawable; // 结束端的图片
    // 触摸点的坐标
    private float touchStartX;
    private float touchStartY;
    // 按下时的触摸点对应的角度
    private float startAngleOnTouchStart;
    // 按下时记录扇形弧度开始的角度；记录扇形弧度扫过的角度；记录扇形弧度结束的角度
    private float startAngleOnTouch,sweepAngleInTouch,endAngleOnTouch;
    // 是否正在移动
    private boolean isMovingArc = false;
    // 是否触摸的扇形圆弧
    private boolean isTouchingArc = false;
    // 是否触摸的是开始图片
    private boolean isTouchingStartDrawable = false;
    // 是否触摸的是结束图片
    private boolean isTouchingEndDrawable = false;
    private float startDrawableX,startDrawableY;
    private float endDrawableX,endDrawableY;

    private boolean isCanTouching = false;
    public HollowCircleRingView3(Context context) {
        super(context);
        init(context);
    }

    public HollowCircleRingView3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HollowCircleRingView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mOuterCircleRect = new RectF();
        mInnerCircleRect = new RectF();
        mArcRect = new RectF();
        DEFAULT_DEMENSION = PieChartUtils.dipToPx(context, 50);

        mOuterCirclePaint = new Paint();
        mOuterCirclePaint.setAntiAlias(true);
        mOuterCirclePaint.setStyle(Paint.Style.FILL);
        mOuterCirclePaint.setStrokeWidth(2);
        mOuterCirclePaint.setShadowLayer(2, 0, 0, Color.BLACK);
        mOuterCirclePaint.setColor(getResources().getColor(android.R.color.holo_blue_light)); // 外部圆颜色

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setStyle(Paint.Style.FILL);
        mInnerCirclePaint.setStrokeWidth(2);
        mInnerCirclePaint.setShadowLayer(2, 0, 0, Color.BLACK);
        mInnerCirclePaint.setColor(getResources().getColor(android.R.color.white)); // 内部圆颜色

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(40);

        mScalePaint = new Paint();
        mScalePaint.setColor(Color.GRAY);
        mScalePaint.setStyle(Paint.Style.FILL);
        mScalePaint.setStrokeWidth(4);
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setTextSize(40);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(getResources().getColor(android.R.color.holo_green_light)); // 扇形圆弧颜色

        startDrawable = getResources().getDrawable(R.drawable.baseline_android_24_start);
        endDrawable = getResources().getDrawable(R.drawable.baseline_android_24);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_DEMENSION, DEFAULT_DEMENSION);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_DEMENSION, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, DEFAULT_DEMENSION);
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int sideLength = Math.min(w, h);
        mOuterRadius = sideLength / 2;
        mInnerRadius = mOuterRadius * 0.7f;
        mViewRadius = mOuterRadius * 0.85f;
        mViewCenterX = w / 2f;
        mViewCenterY = h / 2f;
        mRingWidth = mOuterRadius * 0.3f;

        mOuterCircleRect.set(0, 0, sideLength, sideLength);
        mInnerCircleRect.set(mRingWidth,mRingWidth,sideLength-mRingWidth,sideLength-mRingWidth);
        mArcRect.set(mOuterRadius*0.15f,mOuterRadius*0.15f,sideLength-mOuterRadius*0.15f,sideLength-mOuterRadius*0.15f);

        mArcWidth = mOuterRadius*0.2f;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制外部圆
        canvas.drawArc(mOuterCircleRect, 0, 360, false, mOuterCirclePaint);
        // 绘制内部圆
        canvas.drawArc(mInnerCircleRect,0,360,false,mInnerCirclePaint);
        // 绘制刻度
        for (int i = 0; i < 24; i++) {
            int angle = i * 15;
            int startX, startY, endX, endY;
            if (i % 3 == 0) {
                // 加粗加长的刻度
                startX = (int) (mViewCenterX + (mOuterRadius*0.7f - 20) * Math.cos(Math.toRadians(angle)));
                startY = (int) (mViewCenterY + (mOuterRadius*0.7f - 20) * Math.sin(Math.toRadians(angle)));
                endX = (int) (mViewCenterX + (mOuterRadius*0.7f - 40) * Math.cos(Math.toRadians(angle)));
                endY = (int) (mViewCenterY + (mOuterRadius*0.7f - 40) * Math.sin(Math.toRadians(angle)));
                canvas.drawLine(startX, startY, endX, endY, mScalePaint);

                float rotateAngle = (angle+270)%360; //转换角度
                Rect textRect = new Rect();
                String number = String.valueOf(i);
                mTextPaint.getTextBounds(number,0,number.length(),textRect);
                float textWidth = mTextPaint.measureText(number);
                float textHeight = mTextPaint.descent() - mTextPaint.ascent();
                float textHeight2 = textRect.height();
                float x = (int) (mViewCenterX + (mOuterRadius*0.7f - 70) * Math.cos(Math.toRadians(rotateAngle))) - textWidth/2f;
                float y = (int) (mViewCenterY + (mOuterRadius*0.7f - 70) * Math.sin(Math.toRadians(rotateAngle))) + textHeight2/2f;
                if(i%6==0){
                    mTextPaint.setColor(Color.BLACK);
                }else{
                    mTextPaint.setColor(Color.LTGRAY);
                }
                canvas.drawText(number, x, y, mTextPaint);
            } else {
                // 普通的刻度
                startX = (int) (mViewCenterX + (mOuterRadius*0.7 - 20) * Math.cos(Math.toRadians(angle))) ;
                startY = (int) (mViewCenterY + (mOuterRadius*0.7 - 20) * Math.sin(Math.toRadians(angle)));
                endX = (int) (mViewCenterX + (mOuterRadius*0.7 - 30) * Math.cos(Math.toRadians(angle)));
                endY = (int) (mViewCenterY + (mOuterRadius*0.7 - 30) * Math.sin(Math.toRadians(angle)));
                canvas.drawLine(startX, startY, endX, endY, mScalePaint);
            }

        }

        endAngle = (startAngle + sweepAngle)%360;
        Log.d(TAG,"【onDraw】startAngle="+startAngle  + " ,sweepAngle="
                + sweepAngle + " ,endAngle="+endAngle);

        // 绘制扇形圆弧
        canvas.drawArc(mArcRect, startAngle, sweepAngle, false, mArcPaint);
        // 绘制起始端的图片
        float startX = (float) (mViewCenterX + mViewRadius * Math.cos(Math.toRadians(startAngle)));
        float startY = (float) (mViewCenterY + mViewRadius * Math.sin(Math.toRadians(startAngle)));
        drawDrawable(canvas, startDrawable, startX, startY);

        // 绘制结束端的图片
        float endX = (float) (mViewCenterX + mViewRadius * Math.cos(Math.toRadians(startAngle + sweepAngle)));
        float endY = (float) (mViewCenterY + mViewRadius * Math.sin(Math.toRadians(startAngle + sweepAngle)));
        drawDrawable(canvas, endDrawable, endX, endY);
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, float x, float y) {
        int drawableSize = (int) mRingWidth; // Drawable的大小不超过圆环的宽度
        int halfDrawableSize = drawableSize / 2;
        drawable.setBounds((int) (x - halfDrawableSize), (int) (y - halfDrawableSize),
                (int) (x + halfDrawableSize), (int) (y + halfDrawableSize));
        drawable.draw(canvas);
    }

    private float downX,downY;
    private float startHandleX,startHandleY;
    private float endHandleX,endHandleY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                isTouchingArc = isInArc(x,y);
                isTouchingStartDrawable = isInStartDrawable(x,y);
                isTouchingEndDrawable = isInEndDrawable(x,y);
                Log.d(TAG," isTouchingArc="+isTouchingArc+ " ,isTouchingStartDrawable="+isTouchingStartDrawable
                        + " ,isTouchingEndDrawable="+isTouchingEndDrawable);
                if(isTouchingArc){
                    touchStartX = x;
                    touchStartY = y;
                    startAngleOnTouch = startAngle;
                    // 计算按下时触摸点对应的角度
                    startAngleOnTouchStart = getAngleDegreeByPoint(touchStartX,touchStartY);
                    isMovingArc = false;
                    return true;
                }else if(isTouchingStartDrawable){
                    startHandleX = x;
                    startHandleY = y;
                    startAngleOnTouch = startAngle;
                    sweepAngleInTouch = sweepAngle;
                    endAngleOnTouch = endAngle;
                    // 计算按下时触摸点对应的角度
                    startAngleOnTouchStart = getAngleDegreeByPoint(startHandleX,startHandleY);
                    return true;
                }else if(isTouchingEndDrawable){
                    endHandleX = x;
                    endHandleY = y;
                    startAngleOnTouch = startAngle;
                    sweepAngleInTouch = sweepAngle;
                    endAngleOnTouch = endAngle;
                    // // 计算按下时触摸点对应的角度
                    startAngleOnTouchStart = getAngleDegreeByPoint(endHandleX,endHandleY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final float xDistance = event.getX() - downX;
                final float yDistance = event.getY() - downY;
                // 当手指移动时，且手指在圆环上，才会进行重绘
                if((xDistance != 0 || yDistance != 0) && isInCircle(x,y)){
                    isMovingArc = true;
                    if(isTouchingArc){//如果手指是在圆弧上
                        // 获取移动所对应的角度
                        float angleDelta = calculateAngleDelta(x, y);
                        // 更新开始角度=原开始角度加上变化的角度，%360保证开始角度在0-360度之间
                        startAngle = (startAngleOnTouch + angleDelta) % 360;
                        invalidate();
                    }else if(isTouchingStartDrawable){//如果手指是在开始图片上
                        adjustStartHandle(x,y);
                    }else if(isTouchingEndDrawable){//如果手指是在结束的图片上
                        adjustEndHandle(x,y);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isMovingArc = false;
                isTouchingArc = false;
                isTouchingStartDrawable = false;
                isTouchingEndDrawable = false;
                if(mOnTouchArcChangeListener!=null){
                    String startTime = getNumberByAngle(this.startAngle);
                    String endTime = getNumberByAngle(this.endAngle);
                    mOnTouchArcChangeListener.onChangeArc(startTime,endTime);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断触摸点是否在扇形圆弧的结束图片上
     * @param x
     * @param y
     * @return
     */
    private boolean isInEndDrawable(float x, float y) {
        // 获取扇形圆弧结束角度对应的x和y坐标
        float endX = (float) (mViewCenterX + mViewRadius * Math.cos(Math.toRadians(startAngle + sweepAngle)));
        float endY = (float) (mViewCenterY + mViewRadius * Math.sin(Math.toRadians(startAngle + sweepAngle)));
        int drawableSize = (int) mRingWidth; // Drawable的大小不超过圆环的宽度
        int halfDrawableSize = drawableSize / 2;
        RectF endDrawbleRectF = new RectF((endX - halfDrawableSize),  (endY - halfDrawableSize),
                (endX + halfDrawableSize),  (endY + halfDrawableSize));
        Log.d(TAG,"【isInEndDrawable】: pointX = " + x + " ,pointY ="+y + " ,startX="+endX + " ,startY="+endY + " ,endDrawbleRectF="+endDrawbleRectF);
        return endDrawbleRectF.contains(x,y);

    }

    /**
     * 判断触摸点是否在扇形圆弧的开始图片上
     * @param x
     * @param y
     * @return
     */
    private boolean isInStartDrawable(float x, float y) {
        // 获取扇形圆弧开始角度对应的x和y坐标
        float startX = (float) (mViewCenterX + mViewRadius * Math.cos(Math.toRadians(startAngle)));
        float startY = (float) (mViewCenterY + mViewRadius * Math.sin(Math.toRadians(startAngle)));
        int drawableSize = (int) mRingWidth; // Drawable的大小不超过圆环的宽度
        int halfDrawableSize = drawableSize / 2;
        RectF startDrawbleRectF = new RectF((startX - halfDrawableSize),  (startY - halfDrawableSize),
                (startX + halfDrawableSize),  (startY + halfDrawableSize));
        Log.d(TAG,"【isInStartDrawable】: pointX = " + x + " ,pointY ="+y + " ,startX="+startX + " ,startY="+startY + " ,startDrawbleRectF="+startDrawbleRectF);
        return startDrawbleRectF.contains(x,y);
    }

    /**
     * 更新结束图片的拖动
     * @param x
     * @param y
     */
    private void adjustEndHandle(float x, float y) {
        // 计算变化的角度，即手指移动距离所对应的角度
        float angleDelta = calculateAngleDelta(x, y);
        // 更新结束的角度=原结束角度+变化的角度，%360表示0-360之间
        float finalEndAngle = (endAngleOnTouch + angleDelta) % 360;
        // 更新扇形圆弧的角度=新的结束的角度-原开始的角度；因为原开始角度是不变的
        float finalSweepAngle = finalEndAngle - startAngleOnTouch;
        Log.d(TAG, "【adjustEndHandle】angleDelta="+angleDelta
                + " ,startAngleOnTouch="+startAngleOnTouch + " ,startAngle="+startAngle
                + " ,endAngleOnTouch="+endAngleOnTouch + " ,endAngle="+endAngle
                + " ,finalEndAngle="+finalEndAngle+" ,finalSweepAngle_before="+finalSweepAngle
                + " ,sweepAngle="+sweepAngle);
        finalSweepAngle = (finalSweepAngle + 360)%360;//确保扇形圆弧角度在0-360度之间
        Log.d(TAG,"【adjustEndHandle】finalSweepAngle_after="+finalSweepAngle);
        // 控制扇形圆弧的角度在30-300度之间，当扇形圆弧的角度小于30或者大于300度时就不重绘
        if(finalSweepAngle < 30 || finalSweepAngle > 300){
            return;
        }
        endAngle = finalEndAngle;
        sweepAngle = finalSweepAngle;
        invalidate();
    }

    /**
     * 更新开始图片的拖动
     * @param x
     * @param y
     */
    private void adjustStartHandle(float x, float y) {
        // 计算变化的角度，即手指移动距离所对应的角度
        float angleDelta = calculateAngleDelta(x, y);
        // 更新开始的角度=原开始角度+变化的角度，%360表示0-360之间
        float finalStartAngle = (startAngleOnTouch + angleDelta) % 360;
        // 更新扇形圆弧的角度=原结束的角度-新的开始的角度；因为原结束角度是不变的
        float finalSweepAngle = endAngleOnTouch - finalStartAngle;
        Log.d(TAG, "【adjustStartHandle】angleDelta="+angleDelta
                + " ,startAngleOnTouch="+startAngleOnTouch + " ,startAngle="+startAngle
                + " ,endAngleOnTouch="+endAngleOnTouch + " ,endAngle="+endAngle
                + " ,finalStartAngle="+finalStartAngle+" ,finalSweepAngle_before="+finalSweepAngle
                + " ,sweepAngle="+sweepAngle);
        finalSweepAngle = (finalSweepAngle + 360)%360;//确保扇形圆弧的角度在0-360度之间
        Log.d(TAG,"【adjustStartHandle】finalSweepAngle_after="+finalSweepAngle);
        // 控制扇形圆弧的角度在30-300度之间，当扇形圆弧的角度小于30或者大于300度时就不重绘
        if(finalSweepAngle<30 || finalSweepAngle>300){
            return;
        }
        startAngle = finalStartAngle;
        sweepAngle = finalSweepAngle;
        invalidate();
    }

    /**
     * 判断触摸点是否在扇形圆弧上
     * @param x
     * @param y
     * @return
     */
    private boolean isInArc(float x, float y) {
        float angleDegrees = getAngleDegreeByPoint(x,y);
        float endAngle = (startAngle + sweepAngle)%360;// 保证计算是在0度到360度之间
        boolean isInArc = false;
        // 有可能endAngle会小于startAngle
        if(startAngle<=endAngle){
            isInArc =  angleDegrees >= startAngle && angleDegrees <= endAngle;
        }else{
            // 此时分为两部分来判断
            isInArc =  (angleDegrees >= startAngle && angleDegrees <= 360) || (angleDegrees >= 0 && angleDegrees <= endAngle);
        }
        return isInArc;
    }

    /**
     * 根据坐标计算出当前的角度
     * @param pointX
     * @param pointY
     * @return
     */
    private float getAngleDegreeByPoint(float pointX,float pointY){
        double angleRad = Math.atan2(pointY - mViewCenterY, pointX - mViewCenterX);
        float angleDegrees = (float) Math.toDegrees(angleRad);
        angleDegrees = (angleDegrees + 360)%360; //将angleDegree设置范围在0-360度
        return angleDegrees;
    }

    /**
     * 判断坐标是否在外部圆和内部圆所形成的圆环内
     * @param x
     * @param y
     * @return
     */
    private boolean isInCircle(float x, float y) {
        double distanceToCenter = Math.sqrt(Math.pow(x - mViewCenterX, 2) + Math.pow(y - mViewCenterY, 2));
        // 这里增加了160px，判断坐标是否在外部圆和内部圆所形成的圆环+-160px的范围内
        boolean isInCenter =  distanceToCenter >= (mOuterRadius - mRingWidth / 2 - 160) &&
                distanceToCenter <= (mOuterRadius + mRingWidth / 2 + 160);
        return isInCenter;
    }

    /**
     * 计算移动的距离所对应的角度
     * @param x
     * @param y
     * @return
     */
    private float calculateAngleDelta(float x, float y) {
        // 计算触摸点对应的角度
        float moveDegree = getAngleDegreeByPoint(x,y);
        // 用触摸点的角度-开始的角度，得到移动的角度
        float angleDeltaDegree = moveDegree - startAngleOnTouchStart;
        Log.d(TAG,"【calculateAngleDelta】：x="+x+ " ,y="+y + " ,moveDegree="+moveDegree
                + " ,startAngleOnTouchStart="+startAngleOnTouchStart + " ,angleDeltaDegree_before="+angleDeltaDegree);
        angleDeltaDegree = (angleDeltaDegree + 360)%360; // 确保移动的角度在0-360度之间
        Log.d(TAG,"【calculateAngleDelta】：angleDeltaDegree_after="+angleDeltaDegree);
        return angleDeltaDegree;
    }

    /**
     * 将开始和结束角度对应的时间回调出去
     */
    public interface IOnTouchArChangedListener{
        void onChangeArc(String start,String end);
    }

    private IOnTouchArChangedListener mOnTouchArcChangeListener;

    public IOnTouchArChangedListener getOnTouchArcChangeListener() {
        return mOnTouchArcChangeListener;
    }

    public void setOnTouchArcChangeListener(IOnTouchArChangedListener mOnTouchArcChangeListener) {
        this.mOnTouchArcChangeListener = mOnTouchArcChangeListener;
    }

    public void setStartAngle(float startAngle) {
        if(startAngle>=0 && this.sweepAngle>=0)
            invalidate();
        this.startAngle = startAngle;
        if(mOnTouchArcChangeListener!=null){
            String startTime = getNumberByAngle(this.startAngle);
            float endAngle = this.endAngle<=0 ? (this.startAngle+this.sweepAngle+360)%360 : this.endAngle;
            String endTime = getNumberByAngle(endAngle);
            mOnTouchArcChangeListener.onChangeArc(startTime,endTime);
        }
    }

    public void setSweepAngle(float sweepAngle){
        this.sweepAngle = sweepAngle;
        invalidate();
        if(mOnTouchArcChangeListener!=null){
            float finalEndAngle = (this.startAngle + this.sweepAngle + 360)%360;
            String startTime = getNumberByAngle(this.startAngle);
            String endTime = getNumberByAngle(finalEndAngle);
            mOnTouchArcChangeListener.onChangeArc(startTime,endTime);
        }
    }

    /**
     * 根据角度换算时间
     * @param finalAngle
     * @return
     */
    private String getNumberByAngle(float finalAngle) {
        // 每一个刻度之间的角度为15度，一共24个刻度，加起来就是360度
        int segementAngle = 15;
        // 根据图片上6点对应的角度为圆形中的0/360度，因此要从这里开始计算
        int startHour = 6;
        int hourNumber = (int) (finalAngle / segementAngle);// 计算有几个15度，表示该角度覆盖了几个小时，例如当前角度为30度，则为2个小时，而30度所对应的时间则为6+2=8点
        double remindHourNumber = (finalAngle%segementAngle);// 计算剩下的度数，例如当前为33度，则包含2个15度，剩下3度，这里表示剩下的三度
        int minuteNumber = (int) (remindHourNumber*(60/segementAngle));//60min/15度 表示一度表示4min，再乘以刚刚的3度，表示现在是12min
        int hour = (startHour + hourNumber + 24) % 24;// 同样的，计算小时的结果有可能大于24小时，保证计算结果在0-24小时之间
        return PieChartUtils.assemableTimeStr(hour,minuteNumber);
    }
}
