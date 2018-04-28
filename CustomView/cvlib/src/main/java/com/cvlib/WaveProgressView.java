package com.cvlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.callanna.viewlibrary.R;


/**
 * Created by Callanna on 2018/2/1.
 */

public class WaveProgressView extends View {
    private static final int DEFAULT_SIZE = 200;

    private static final int DEFAULT_WAVE_WIDTH  = 20;

    private static final int DEFAULT_WAVE_HIGHT  = 10;

    private static final int DEFAULT_COLOR_BG = 0x66333333;

    private static final int DEFAULT_COLOR_WATER = 0x880000aa;
    // 波纹颜色
    private static final int WAVE_PAINT_COLOR = 0x880000aa;

    private static final int WaveType_Rect = 0;

    private static final int WaveType_Circle = 1;
    private float mWidth,mHeight;

    private float mWaveWidth,mWaveHeight;

    private int progressBgColor,waterColor,secondWaterColor;

    private Paint wavePaint,waveSeconfPaint,bgPaint;

    private Path wavePath,waveSecondPath;

    private boolean isHasSecondWave = true;

    private float mProgress = 0f;
    private float waveMovingDistance = 0f;
    private int waveNum = 0;

    private int mWaveType ;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private ValueAnimator anim_waveProgress;
    private ValueAnimator waveDistance;


    public WaveProgressView(Context context) {
        this(context,null);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        mWaveWidth = typedArray.getDimension(R.styleable.WaveProgressView_wave_width,dp2px(DEFAULT_WAVE_WIDTH));
        mWaveHeight = typedArray.getDimension(R.styleable.WaveProgressView_wave_hight,dp2px(DEFAULT_WAVE_HIGHT));
        waterColor = typedArray.getColor(R.styleable.WaveProgressView_wave_color,DEFAULT_COLOR_WATER) ;
        progressBgColor = typedArray.getColor(R.styleable.WaveProgressView_bg_color,DEFAULT_COLOR_BG);
        isHasSecondWave = typedArray.getBoolean(R.styleable.WaveProgressView_has_second_wave,true);
        mWaveType = typedArray.getInt(R.styleable.WaveProgressView_wave_type,WaveType_Rect);
        typedArray.recycle();
        secondWaterColor = waterColor;
        init( );
    }

    private void init( ) {
        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setColor(waterColor);
        wavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        wavePaint.setAlpha(200);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(progressBgColor);

        waveSeconfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        waveSeconfPaint.setColor(secondWaterColor);
        waveSeconfPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        waveSeconfPaint.setAlpha(180);
        wavePath = new Path();
        waveSecondPath = new Path();
        setLayerType(View.LAYER_TYPE_HARDWARE,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredSize(widthMeasureSpec,true);
        mHeight = getMeasuredSize(heightMeasureSpec,false);
        waveNum = (int) Math.ceil( mWidth/mWaveWidth);

        //Log.d("duanyl", "onMeasure--> mWidth:"+mWidth+",mHeight:"+mHeight +",mWaveWidth:"+mWaveWidth+",mWaveHeight:"+mWaveHeight);
        setMeasuredDimension((int)mWidth,(int)mHeight);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        startMoveWave();
    }

    private void startMoveWave() {
        if(waveDistance == null){
            waveDistance = ValueAnimator.ofFloat(0.5f,1.0f);
            waveDistance.setInterpolator(new LinearInterpolator());
            waveDistance.setRepeatCount(ValueAnimator.INFINITE);
            waveDistance.setRepeatMode(ValueAnimator.REVERSE);
            waveDistance.setDuration(2000);
            waveDistance.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float val = valueAnimator.getAnimatedFraction();
                    waveMovingDistance += 0.1f;
                    if(waveMovingDistance > 2 * mWidth){
                        waveMovingDistance = val/2f;
                    }
                    mWaveWidth = val * DEFAULT_WAVE_WIDTH+DEFAULT_WAVE_WIDTH /2;
                    mWaveHeight = val * DEFAULT_WAVE_HIGHT;
                    postInvalidate();
                }
            });
        }
        if(!waveDistance.isRunning()) {
            waveDistance.start();
        }
    }

    private float getMeasuredSize(int widthMeasureSpec, boolean b) {
        float result = dp2px(DEFAULT_SIZE);
        if(b){
            result = result + getPaddingLeft() +getPaddingRight();
        }else{
            result = result + getPaddingBottom() +getPaddingTop();
        }
        switch (MeasureSpec.getMode(widthMeasureSpec)){
            case MeasureSpec.AT_MOST:
                result = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.EXACTLY:
                result = Math.min(result, MeasureSpec.getSize(widthMeasureSpec)) ;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bitmap = Bitmap.createBitmap((int)mWidth, (int)mHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);

        if(mWaveType == 1) {
            bitmapCanvas.drawCircle(mWidth / 2, mHeight / 2, (Math.min(mWidth, mHeight) / 2), bgPaint);
        }else {
            bitmapCanvas.drawRect(0,0,mWidth , mHeight  , bgPaint);
        }
        bitmapCanvas.drawPath(getWavePath(),wavePaint);
        if(isHasSecondWave){
            bitmapCanvas.drawPath(getSecondWavePath(),waveSeconfPaint);
        }
        bitmapCanvas.save();
        canvas.drawBitmap(bitmap,0,0,null);
    }

    private Path getSecondWavePath() {
        waveSecondPath.reset();
        waveSecondPath.moveTo(0,(1.0f-mProgress) * mHeight +DEFAULT_WAVE_HIGHT);
        waveSecondPath.lineTo(0,mHeight);
        waveSecondPath.lineTo(mWidth,mHeight );
        waveSecondPath.lineTo(mWidth + waveMovingDistance,(1f-mProgress) * mHeight +DEFAULT_WAVE_HIGHT);
        for (int i = 0;i < waveNum * 2; i++){
            waveSecondPath.rQuadTo(-mWaveWidth/2  ,mWaveHeight,-mWaveWidth,0);
            waveSecondPath.rQuadTo(-mWaveWidth/2  ,-mWaveHeight,-mWaveWidth,0);
        }
        waveSecondPath.close();
        return waveSecondPath;
    }

    private Path getWavePath() {
        wavePath.reset();
        wavePath.moveTo(mWidth,(1f-mProgress) * mHeight+DEFAULT_WAVE_HIGHT);
        wavePath.lineTo(mWidth,mHeight);
        wavePath.lineTo(0,mHeight);
        wavePath.lineTo(-waveMovingDistance,(1f-mProgress) * mHeight +DEFAULT_WAVE_HIGHT);
        for (int i = 0;i < waveNum * 2; i++){
            wavePath.rQuadTo(mWaveWidth/2  ,mWaveHeight,mWaveWidth,0);
            wavePath.rQuadTo(mWaveWidth/2  ,-mWaveHeight,mWaveWidth,0);
        }
        wavePath.close();
        return wavePath;
    }

    public void setProgress(float val){
        Log.d("duanyl", "setProgress: "+mProgress);
        if(anim_waveProgress != null && anim_waveProgress.isRunning()){
            anim_waveProgress.end();
        }
        anim_waveProgress = ValueAnimator.ofFloat(mProgress,val);
        anim_waveProgress.setDuration(3000);
        anim_waveProgress.setInterpolator(new DecelerateInterpolator());
        anim_waveProgress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress  = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        anim_waveProgress.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }
        });
        anim_waveProgress.start();
        postInvalidate();
    }
    public  float dp2px(float val){
        float scale = getContext().getResources().getDisplayMetrics().density;
        return   (scale  * val +0.5f);
    }
    public   float px2dp(int val){
        float scale = getContext().getResources().getDisplayMetrics().density;
        return   (val/scale +0.5f);
    }
}
