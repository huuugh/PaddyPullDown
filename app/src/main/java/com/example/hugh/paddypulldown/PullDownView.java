package com.example.hugh.paddypulldown;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by 60352 on 2018/3/7.
 *
 */

public class PullDownView extends View {
    private float mProgress;
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mBezierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Path BezierPath = new Path();
    DecelerateInterpolator ProgressInterpolator = new DecelerateInterpolator();

    private float mCenterX;
    private float mCenterY;
    private float mRadius = 80;
    private float MaxDrag = 400;
    private float BezierStartX;
    private float BezierEndX;
    private float BezierEndY;
    private float mControlY;
    private float mControlX;
    private float MinWidth = 250;
    private float MaxControlY = 20;
    private ValueAnimator valueAni;
    private int mColor = Color.rgb(	255,105,180);
    private double MaxAngle = 110;

    public PullDownView(Context context) {
        super(context);
        mPaint.setColor(mColor);
        mBezierPaint.setColor(mColor);
    }

    public PullDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(mColor);
        mBezierPaint.setColor(mColor);
    }

    public PullDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(mColor);
        mBezierPaint.setColor(mColor);

    }

    public void release(){
        if (valueAni == null){
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mProgress, 0f);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue instanceof Float){
                        setProgress((Float) animatedValue);
                    }
                }
            });
            valueAni = valueAnimator;
        }else {
            valueAni.cancel();
            valueAni.setFloatValues(mProgress,0f);
        }
        valueAni.start();
    }

    public void setProgress(float progress){
        mProgress = ProgressInterpolator.getInterpolation(progress);
        Log.e("mProgress",mProgress+"");
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthByProgress = (int) (mRadius*2 + getPaddingLeft() + getPaddingRight());
        int heightByProgress = (int) (MaxDrag*mProgress + getPaddingTop() + getPaddingBottom());

        Log.e(widthByProgress+"",heightByProgress+"");

        int width = 0,height = 0;
        if (widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else if (widthMode == MeasureSpec.AT_MOST){
            width = Math.min(widthByProgress,widthSize);
        }else if (widthMode == MeasureSpec.UNSPECIFIED){
            width = widthByProgress;
        }

        if (heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else if (heightMode == MeasureSpec.AT_MOST){
            height = Math.min(heightByProgress,heightSize);
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            height = heightByProgress;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenterX,mCenterY,mRadius,mPaint);
        BezierPath.reset();
        BezierPath.moveTo(BezierStartX,0);
        BezierPath.quadTo(mControlX,mControlY,BezierEndX,BezierEndY);
        BezierPath.lineTo(mCenterX + mCenterX - BezierEndX,BezierEndY);
        BezierPath.quadTo(mCenterX + mCenterX - mControlX,mControlY,mCenterX + mCenterX - MinWidth *mProgress,0);
        canvas.drawPath(BezierPath,mBezierPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = getWidth()/2;
        mCenterY = getHeight() - mRadius;

        double maxRadian = Math.toRadians(MaxAngle);
        //贝塞尔曲线的终点
        BezierEndX = (float) (mCenterX - mRadius*Math.sin(maxRadian *mProgress));
        BezierEndY = (float) (mCenterY + mRadius*Math.cos(maxRadian *mProgress));
        //控制点的坐标
        mControlY = MaxControlY*mProgress;
        mControlX = (float) (mCenterX - (BezierEndY - mControlY)/Math.tan(maxRadian *mProgress));
        //贝塞尔曲线的起点
        BezierStartX = mProgress* MinWidth;
    }
}
