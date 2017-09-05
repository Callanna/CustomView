package com.cvlib.progress;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import com.callanna.viewlibrary.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.R.attr.value;
import static com.cvlib.progress.PSeekBar.PSeekBarMode.PS_CIRCLE;
import static com.cvlib.progress.PSeekBar.PSeekBarMode.PS_HRECT;
import static com.cvlib.progress.PSeekBar.PSeekBarMode.PS_VRECT;

/**
 * Created by Callanna on 2017/6/22.
 */

public class PSeekBar extends View {

    private static final long INDICATOR_DELAY_FOR_TAPS = 150;
    private static final int DEFAULT_THUMB_COLOR = 0xff009688;
    private static final int DEFAULT_TRACK_COLOR = Color.GRAY;
    private static final int DEFAULT_TRACK_HEIGHT = 5;
    private static final int mDefShadowOffset = 10;
    private PSeekBarBuilder mPSeekBarBuilder;


    @IntDef({PS_HRECT, PS_VRECT, PS_CIRCLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PSeekBarMode {
        int PS_HRECT = 1, PS_VRECT = 2, PS_CIRCLE = 3;
    }


    private Paint mTrackPaint, mProgressPaint, mThumbPaint, mThumbDotPaint, mLinePaint, mTextPaint;
    private int mTrackHeight;
    private int mTrackColor;

    private int mProgressHeight;
    private int mProgressColor;

    private int mThumbSize;
    private int mThumbColor;

    private int mThumbDotSize;
    private int mThumbDotColor;

    private int mMax = 100;
    private int mMin = 0;
    private int mValue;
    @PSeekBarMode
    private int mPSMode;

    private boolean isHasThumbShadow = true;
    private float mThumbShadowRadius = 12;
    private boolean isRound = true;
    private int radiusSize;
    private boolean isHasBubble = true;
    private int bubbleTextSize;
    private int bubbleTextColor;
    private int mBubbleColor;
    private int mBubbleDistance;
    private boolean isClickToDrag = true;
    private boolean isLineTrack = false;
    private int mLineTrackSize;
    private boolean isColorGradient = false;
    private int mStartColor, mCenterColor, mEndColor;
    private boolean isHasTextEnd = false;
    private int mAddedTouchBounds;
    private int mArcFullDegree;
    private RectF rectFTrack, rectFProgress;
    private boolean isDragging = false;
    private float density;
    private float mDownX = 0, mDownY = 0;
    private float mStartX = 0, mStartY = 0;

    private MarkerIndicator mIndicator;
    private float mProgress = 0.3f;

    private OnProgressChangeListener mPublicChangeListener;
    private Rect mTextBounds;
    private boolean isToChangeColor;

    public PSeekBar(Context context) {
        this(context, null);
    }

    public PSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        initPaints();
        Log.d("duanyl", "PSeekBar: ");
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        density = getResources().getDisplayMetrics().density;
        int touchBounds = (int) (density * 32);
        mAddedTouchBounds = Math.max(0, Math.abs(touchBounds - mThumbSize) / 2);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PSeekBar,
                defStyleAttr, R.style.Widget_PSeekBar);

        mProgressHeight = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_progressheight, DEFAULT_TRACK_HEIGHT);
        mProgressColor = a.getColor(R.styleable.PSeekBar_ps_progresscolor, DEFAULT_THUMB_COLOR);

        mTrackHeight = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_trackheight, mProgressHeight);
        mTrackColor = a.getColor(R.styleable.PSeekBar_ps_trackcolor, DEFAULT_TRACK_COLOR);

        mThumbSize = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_thumbsize, mProgressHeight);
        mThumbColor = a.getColor(R.styleable.PSeekBar_ps_thumbcolor, DEFAULT_THUMB_COLOR);

        mThumbDotSize = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_thumbdotsize, DEFAULT_TRACK_HEIGHT);
        mThumbDotColor = a.getColor(R.styleable.PSeekBar_ps_thumbdotcolor, DEFAULT_THUMB_COLOR);

        mBubbleDistance = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_bubbledistance, 0);
        mBubbleColor = a.getColor(R.styleable.PSeekBar_ps_bubblecolor, DEFAULT_THUMB_COLOR);

        mArcFullDegree = a.getInteger(R.styleable.PSeekBar_ps_arcfulldegree, 360);

        mMax = a.getInteger(R.styleable.PSeekBar_ps_max, 100);
        mMin = a.getInteger(R.styleable.PSeekBar_ps_min, 0);
        int mode = a.getInteger(R.styleable.PSeekBar_ps_mode, PS_HRECT);
        if (mode == 1) {
            mPSMode = PS_HRECT;
        } else if (mode == 2) {
            mPSMode = PS_VRECT;
        } else if (mode == 3) {
            mPSMode = PS_CIRCLE;
        }


        isClickToDrag = a.getBoolean(R.styleable.PSeekBar_ps_isClickToDrag, true);
        isColorGradient = a.getBoolean(R.styleable.PSeekBar_ps_isColorGradient, false);
        isHasBubble = a.getBoolean(R.styleable.PSeekBar_ps_isShowBubble, true);
        isHasTextEnd = a.getBoolean(R.styleable.PSeekBar_ps_isHasEndText, false);
        isLineTrack = a.getBoolean(R.styleable.PSeekBar_ps_islinetrack, false);
        isRound = a.getBoolean(R.styleable.PSeekBar_ps_isRound, true);
        isToChangeColor = a.getBoolean(R.styleable.PSeekBar_ps_isToChangColor, false);
        radiusSize = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_roundsize, 5);
        mLineTrackSize = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_linesize, 0);
        mStartColor = a.getColor(R.styleable.PSeekBar_ps_startcolor, DEFAULT_THUMB_COLOR);
        mCenterColor = a.getColor(R.styleable.PSeekBar_ps_centercolor, DEFAULT_THUMB_COLOR);
        mEndColor = a.getColor(R.styleable.PSeekBar_ps_endcolor, DEFAULT_THUMB_COLOR);
        bubbleTextSize = a.getDimensionPixelSize(R.styleable.PSeekBar_ps_textSize, 20);
        bubbleTextColor = a.getColor(R.styleable.PSeekBar_ps_textcolor, Color.WHITE);
        mProgress = a.getInteger(R.styleable.PSeekBar_ps_progress,0);

        a.recycle();
    }

    private void initPaints() {
        //progress bg
        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setColor(mTrackColor);
        mTrackPaint.setStyle(Paint.Style.FILL);
        mTrackPaint.setAntiAlias(true);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mThumbColor);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setAntiAlias(true);
        if(mPSMode == PS_CIRCLE){
            mTrackPaint.setStrokeWidth(mTrackHeight);
            mTrackPaint.setStyle(Paint.Style.STROKE);//设置空心
            mProgressPaint.setStrokeWidth(mProgressHeight);
            mProgressPaint.setStyle(Paint.Style.STROKE);//设置空心
        }
        if (isRound && mPSMode == PS_CIRCLE) {
            mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setColor(mThumbColor);
        mThumbPaint.setStyle(Paint.Style.FILL);
        mThumbPaint.setAntiAlias(true);
        if (isHasThumbShadow) {
            mThumbPaint.setShadowLayer(mThumbShadowRadius, mDefShadowOffset, mDefShadowOffset, Color.GRAY);
        }
        mThumbDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbDotPaint.setColor(mThumbDotColor);
        mThumbDotPaint.setDither(true);
        mThumbDotPaint.setStyle(Paint.Style.FILL);
        mThumbDotPaint.setAntiAlias(true);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mTrackColor);
        mLinePaint.setDither(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mLineTrackSize);
        mLinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        mLinePaint.setAntiAlias(true);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(bubbleTextColor);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(bubbleTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0, height = 0;
        switch (mPSMode) {
            case PS_HRECT:
                width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
                height = mThumbSize + getPaddingTop() + getPaddingBottom();
                height += (mAddedTouchBounds * 2);
                break;
            case PS_VRECT:
                height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec) +(mAddedTouchBounds * 2) +mThumbSize;
                width = mThumbSize + getPaddingLeft() + getPaddingRight();
                width += (mAddedTouchBounds * 2);
                break;
            case PS_CIRCLE:
                width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
                height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
                height = width = (Math.max(width, height) + Math.max(mThumbSize, mProgressHeight) + (mAddedTouchBounds * 2));
                break;
        }
        Log.d("duanyl", "onMeasure: " + width + "," + height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("duanyl", "onSizeChanged: " + w + "," + h);
        float left = getPaddingLeft() / 2;
        float top = getPaddingTop() / 2;
        float right = getWidth() - getPaddingRight() / 2;
        float bottom = getHeight() - getPaddingBottom() / 2;
        float centerX = (left + right) / 2;
        float centerY = (top + bottom) / 2;
        mTextBounds = new Rect();
        mTextPaint.getTextBounds(String.valueOf(mMax), 0, String.valueOf(mMax).length(), mTextBounds);
        switch (mPSMode) {
            case PS_HRECT:
                centerX = 0;
                rectFTrack = new RectF(left+mThumbSize/2, centerY - mTrackHeight / 2, right - mTextBounds.width()-mThumbSize/2, centerY + mTrackHeight / 2);
                rectFProgress = new RectF(left+mThumbSize/2, centerY - mProgressHeight / 2, centerX, centerY + mProgressHeight / 2);
                if (!isRound) {
                    radiusSize = 0;
                }
                break;
            case PS_VRECT:
                centerY = 0;
                rectFTrack = new RectF(centerX - mTrackHeight / 2, top + mTextBounds.height() + mThumbSize/2, centerX + mTrackHeight / 2, bottom -mThumbSize/2);
                rectFProgress = new RectF(centerX - mProgressHeight / 2, centerY, centerX + mProgressHeight / 2, bottom-mThumbSize/2);
                if (!isRound) {
                    radiusSize = 0;
                }
                break;
            case PS_CIRCLE:
                int offset = mTrackHeight / 2 +mThumbSize/2;
                rectFTrack = new RectF(left + offset, top + offset, right - offset, bottom -offset);
                rectFProgress = new RectF(left + offset, top + offset, right - offset, bottom - offset);
                if (!isRound) {
                    radiusSize = 0;
                }
                break;
        }
        Log.d("duanyl", "onSizeChanged: left:" + left + ",top:" + top + ",right:" + right + ",bottom:" + bottom + ",cX:" + centerX + ",cY:" + centerY);
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    private float getProgress() {
        return mProgress;
    }

    private int getCurrentValue() {
        return mValue;
    }

    public void setOnProgressChangeListener(@Nullable OnProgressChangeListener listener) {
        mPublicChangeListener = listener;
    }

    public void config(PSeekBarBuilder pSeekBarBuilder) {
        mTrackHeight = pSeekBarBuilder.getmTrackHeight();
        mTrackColor = pSeekBarBuilder.getmTrackColor();

        mProgressHeight = pSeekBarBuilder.getmProgressHeight();
        mProgressColor = pSeekBarBuilder.getmProgressColor();

        mThumbSize = pSeekBarBuilder.getmThumbSize();
        mThumbColor = pSeekBarBuilder.getmThumbColor();

        mThumbDotSize = pSeekBarBuilder.getmThumbDotSize();
        mThumbDotColor = pSeekBarBuilder.getmThumbDotColor();

        mMax = pSeekBarBuilder.getmMax();
        mMin = pSeekBarBuilder.getmMin();
        mPSMode = pSeekBarBuilder.getmPSMode();
        isRound = pSeekBarBuilder.isRound();
        radiusSize = pSeekBarBuilder.getRadiusSize();
        isHasBubble = pSeekBarBuilder.isHasBubble();
        bubbleTextSize = pSeekBarBuilder.getBubbleTextSize();
        bubbleTextColor = pSeekBarBuilder.getBubbleTextColor();
        mBubbleColor = pSeekBarBuilder.getmBubbleColor();
        mBubbleDistance = pSeekBarBuilder.getmBubbleDistance();
        isClickToDrag = pSeekBarBuilder.isClickToDrag();
        isLineTrack = pSeekBarBuilder.isLineTrack();
        mLineTrackSize = pSeekBarBuilder.getmLineTrackSize();
        isColorGradient = pSeekBarBuilder.isColorGradient();
        mStartColor = pSeekBarBuilder.getmStartColor();
        mCenterColor = pSeekBarBuilder.getmCenterColor();
        mEndColor = pSeekBarBuilder.getmEndColor();
        isHasTextEnd = pSeekBarBuilder.isHasTextEnd();
        mArcFullDegree = pSeekBarBuilder.getmArcFullDegree();
        isToChangeColor = pSeekBarBuilder.isToChangeColor();

        mThumbSize = Math.max(mThumbSize,mProgressHeight);

        initPaints();
        requestLayout();
        postInvalidate();
        mPSeekBarBuilder = null;
        Log.d("duanyl", "config: ");
    }

    public PSeekBarBuilder getConfigBuilder() {
        if (mPSeekBarBuilder == null) {
            mPSeekBarBuilder = new PSeekBarBuilder(this);
        }
        mPSeekBarBuilder.setmTrackHeight(mTrackHeight);
        mPSeekBarBuilder.setmTrackColor(mTrackColor);

        mPSeekBarBuilder.setmProgressHeight(mProgressHeight);
        mPSeekBarBuilder.setmProgressColor(mProgressColor);

        mPSeekBarBuilder.setmThumbSize(mThumbSize);
        mPSeekBarBuilder.setmThumbColor(mThumbColor);

        mPSeekBarBuilder.setmThumbDotSize(mThumbDotSize);
        mPSeekBarBuilder.setmThumbDotColor(mThumbDotColor);

        mPSeekBarBuilder.setmMax(mMax);
        mPSeekBarBuilder.setmMin(mMin);
        mPSeekBarBuilder.setmPSMode(mPSMode);

        mPSeekBarBuilder.setRound(isRound);
        mPSeekBarBuilder.setRadiusSize(radiusSize);
        mPSeekBarBuilder.setHasBubble(isHasBubble);
        mPSeekBarBuilder.setBubbleTextColor(bubbleTextSize);
        mPSeekBarBuilder.setBubbleTextColor(bubbleTextColor);
        mPSeekBarBuilder.setmBubbleColor(mBubbleColor);
        mPSeekBarBuilder.setmBubbleDistance(mBubbleDistance);
        mPSeekBarBuilder.setClickToDrag(isClickToDrag);
        mPSeekBarBuilder.setLineTrack(isLineTrack);
        mPSeekBarBuilder.setmLineTrackSize(mLineTrackSize);
        mPSeekBarBuilder.setColorGradient(isColorGradient);
        mPSeekBarBuilder.setmStartColor(mStartColor);
        mPSeekBarBuilder.setmCenterColor(mCenterColor);
        mPSeekBarBuilder.setmEndColor(mEndColor);
        mPSeekBarBuilder.setHasTextEnd(isHasTextEnd);
        mPSeekBarBuilder.setmArcFullDegree(mArcFullDegree);
        mPSeekBarBuilder.setToChangeColor(isToChangeColor);
        return mPSeekBarBuilder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("duanyl", "onDraw: ");
        mValue = (int) ((mMax - mMin) * mProgress);
        mTextPaint.getTextBounds(String.valueOf(mValue), 0, String.valueOf(mValue).length(), mTextBounds);
        switch (mPSMode) {
            case PS_HRECT:
                toDrawHrect(canvas);
                break;
            case PS_VRECT:
                toDrawVrect(canvas);
                break;
            case PS_CIRCLE:
                toDrawArc(canvas);
                break;
        }

    }

    private void toDrawArc(Canvas canvas) {
        float start = 90 + ((360 - mArcFullDegree) >> 1); //进度条起始点
        float sweep1 = mArcFullDegree * mProgress; //进度划过的角度
        float sweep2 = mArcFullDegree - sweep1; //剩余的角度
        float centerX = rectFProgress.centerX();
        float centerY = rectFProgress.centerY();
        canvas.drawArc(rectFTrack, start + sweep1, sweep2, false, mTrackPaint);
        canvas.drawArc(rectFProgress, start, sweep1, false, mProgressPaint);
        if (isLineTrack) {
            float a = 0;
            float s = ((360 - mArcFullDegree) >> 1);
            double step = (mLineTrackSize * 1.0f /( rectFProgress.width() * 1.0f  * Math.PI)) * mArcFullDegree;
            a +=  step * 2 + s;
            while (a < sweep1 + s) {

                double r = a / 180 * Math.PI;
                float x = centerX - (rectFProgress.width()+mProgressHeight)  /2  * (float) Math.sin(r);
                float y = centerY + (rectFProgress.width()+mProgressHeight ) /2  * (float) Math.cos(r);
                float x2 = centerX - (rectFProgress.width()-mProgressHeight) /2* (float) Math.sin(r);
                float y2 = centerY + (rectFProgress.width()-mProgressHeight) /2 * (float) Math.cos(r);
                canvas.drawLine(x, y, x2,y2, mLinePaint);
                a += step * 2;
            }
        }
        //绘制进度位置，也可以直接替换成一张图片
        float progressRadians = (float) (((360.0f - mArcFullDegree) / 2 + sweep1) / 180 * Math.PI);
        float thumbX = centerX - rectFProgress.width() * (float) Math.sin(progressRadians);
        float thumbY = centerY + rectFProgress.width() * (float) Math.cos(progressRadians);
        canvas.drawCircle(thumbX, thumbY, mThumbSize / 2, mThumbPaint);
        canvas.drawCircle(thumbX, thumbY, mThumbDotSize / 2, mThumbDotPaint);

        if (isHasTextEnd) {
            canvas.drawText(String.valueOf(mValue), centerX - mTextBounds.width() / 2, centerY , mTextPaint);
        }
    }

    private void toDrawVrect(Canvas canvas) {
        canvas.drawRoundRect(rectFTrack, radiusSize, radiusSize, mTrackPaint);

        rectFProgress.top = (rectFTrack.top - rectFTrack.bottom) * mProgress + rectFTrack.bottom;
        if (isToChangeColor) {
            mProgressPaint.setColor(calColor(1 - mProgress, mStartColor, mEndColor));
        } else if (isColorGradient) {
            Shader mShader = new LinearGradient(rectFProgress.left, rectFProgress.top, rectFProgress.right, rectFProgress.bottom, new int[]{mStartColor, mCenterColor, mEndColor}, null, Shader.TileMode.CLAMP);
            mProgressPaint.setShader(mShader);
        }
        canvas.drawRoundRect(rectFProgress, radiusSize, radiusSize, mProgressPaint);
        if (isLineTrack) {
            float h = mLineTrackSize * 2 +rectFTrack.bottom;
            while (h < rectFTrack.bottom - mLineTrackSize * 2) {
                canvas.drawLine(rectFProgress.left, h, rectFProgress.right, h, mLinePaint);
                h += mLineTrackSize * 2;
            }
        }

        if (mThumbSize > mProgressHeight) {
            canvas.drawRect(rectFProgress.left, rectFProgress.top, rectFProgress.right, rectFProgress.top - radiusSize, mProgressPaint);
            canvas.drawCircle(rectFProgress.centerX(), rectFProgress.top, mThumbSize / 2, mThumbPaint);
        }
        if (mThumbDotSize >mProgressHeight) {
            canvas.drawCircle(rectFProgress.centerX(), rectFProgress.top, mThumbDotSize / 2, mThumbDotPaint);
        }

        if (isHasTextEnd) {
            canvas.drawText(String.valueOf(mValue), rectFTrack.centerX()-(mTextBounds.width()/2), rectFTrack.top - mThumbSize/2, mTextPaint);
        }
    }

    private void toDrawHrect(Canvas canvas) {
        canvas.drawRoundRect(rectFTrack, radiusSize, radiusSize, mTrackPaint);

        rectFProgress.right =  rectFTrack.left +( rectFTrack.right - rectFTrack.left ) * mProgress;
        if (isToChangeColor) {
            mProgressPaint.setColor(calColor(1 - mProgress, mStartColor, mEndColor));
        } else if (isColorGradient) {
            Shader mShader = new LinearGradient(rectFProgress.left, rectFProgress.top, rectFProgress.right, rectFProgress.bottom, new int[]{mStartColor, mCenterColor, mEndColor}, null, Shader.TileMode.CLAMP);
            mProgressPaint.setShader(mShader);
        }
        canvas.drawRoundRect(rectFProgress, radiusSize, radiusSize, mProgressPaint);
        if (isLineTrack) {
            float w = rectFTrack.left + mLineTrackSize * 2;
            while (w < rectFTrack.right - mLineTrackSize*2) {
                canvas.drawLine(w, rectFProgress.top, w, rectFProgress.bottom, mLinePaint);
                w += mLineTrackSize * 2;
            }
        }

        if (mThumbSize > mProgressHeight) {
            canvas.drawRect(rectFProgress.right - radiusSize, rectFProgress.top, rectFProgress.right, rectFProgress.bottom, mProgressPaint);
            canvas.drawCircle(rectFProgress.right, rectFProgress.centerY(), mThumbSize / 2, mThumbPaint);
        }
        if (mThumbDotSize > mProgressHeight) {
            canvas.drawCircle(rectFProgress.right, rectFProgress.centerY(), mThumbDotSize / 2, mThumbDotPaint);
        }

        if (isHasTextEnd) {
            canvas.drawText(String.valueOf(mValue), rectFTrack.right + mThumbSize/2, rectFTrack.centerY()+mTextBounds.height()/2, mTextPaint);
        }
    }

    /**
     * 计算渐变效果中间的某个颜色值。
     * 仅支持 #aarrggbb 模式,例如 #ccc9c9b2
     */
    public int calColor(float fraction, int startValue, int endValue) {
        int start_r = Color.red(startValue);
        int end_r = Color.red(endValue);

        int start_g = Color.green(startValue);
        int end_g = Color.green(endValue);

        int start_b = Color.blue(startValue);
        int end_b = Color.blue(endValue);
        return Color.rgb((int)((end_r - start_r) * fraction +start_r),(int)((end_g - start_g) * fraction +start_g),(int)((end_b - start_b) * fraction +start_b));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDownX = event.getX();
        mDownY = event.getY();
        if (!isEnabled() || !isClickToDrag) {
            return false;
        }
        int actionMasked = MotionEventCompat.getActionMasked(event);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                startDragging(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging()) {
                    updateDragging(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                stopDragging();
                break;
            case MotionEvent.ACTION_CANCEL:
                stopDragging();
                break;
        }
        return true;
    }

    private void stopDragging() {
        hideMarker();
        isDragging = false;
        if (mPublicChangeListener != null) {
            mPublicChangeListener.onStopTrackingTouch(PSeekBar.this);
        }

    }

    private void updateDragging(MotionEvent event) {
        if (rectFTrack.contains(event.getX(),event.getY())) {
            switch (mPSMode) {
                case PS_CIRCLE:
                    //判断拖动时是否移出去了
                    if (checkOnArc(mDownX, mDownY)) {
                        mProgress = (calDegreeByPosition(mDownX, mDownY) / mArcFullDegree * 1.0f);
                        moveMarker((int) rectFProgress.centerX());
                    } else {
                        isDragging = false;
                    }
                    break;
                case PS_VRECT:
                    mProgress = (mDownY - rectFTrack.bottom) / (rectFTrack.top - rectFTrack.bottom);
                    moveMarker((int) rectFProgress.centerX());
                    break;
                case PS_HRECT:
                    mProgress = (mDownX - rectFTrack.left) / (rectFTrack.right - rectFTrack.left);
                    moveMarker((int) (mDownX - mStartX));
                    break;
            }
            postInvalidate();
            if (mPublicChangeListener != null) {
                mPublicChangeListener.onProgressChanged(PSeekBar.this, value, isDragging);
            }
        }
    }

    private void startDragging(MotionEvent event) {
        if (rectFTrack.contains((int) event.getX(), (int) event.getY())) {
            isDragging = true;
            mStartX = 0;
            updateDragging(event);
            if (mPublicChangeListener != null) {
                mPublicChangeListener.onStartTrackingTouch(this);
            }
            removeCallbacks(mShowIndicatorRunnable);
            postDelayed(mShowIndicatorRunnable, INDICATOR_DELAY_FOR_TAPS);
        }
    }

    public boolean isDragging() {
        return isDragging;
    }

    private Runnable mShowIndicatorRunnable = new Runnable() {
        @Override
        public void run() {
            showMarker();
        }
    };

    private void showMarker() {
        if (!isHasThumbShadow) {
            return;
        }
        if (mIndicator == null) {
            mIndicator = new MarkerIndicator(getContext(), this, mMax + "");
        }
        mIndicator.show();
    }

    private void hideMarker() {
        if (!isHasThumbShadow) {
            return;
        }
        removeCallbacks(mShowIndicatorRunnable);
        if (mIndicator != null) {
            mIndicator.dismiss();
        }
    }

    public void moveMarker(int x) {
        if (!isHasThumbShadow) {
            return;
        }
        if (mIndicator != null) {
            mIndicator.move(x - mThumbSize / 2, getCurrentValue() + "");
        }
    }

    private float calDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }


    /**
     * 判断该点是否在弧线上（附近）
     */
    private boolean checkOnArc(float currentX, float currentY) {
        float distance = calDistance(currentX, currentY, rectFProgress.centerX(), rectFProgress.centerY());
        float degree = calDegreeByPosition(currentX, currentY);
        return distance > (rectFProgress.width() - mProgressHeight - 5) / 2 && distance < (rectFProgress.width() + mProgressHeight + 5) / 2
                && (degree >= -2 && degree <= mArcFullDegree + 2);
    }


    /**
     * 根据当前位置，计算出进度条已经转过的角度。
     */
    private float calDegreeByPosition(float currentX, float currentY) {
        float a1 = (float) (Math.atan(1.0f * (rectFProgress.centerX() - currentX) / (currentY - rectFProgress.centerY())) / Math.PI * 180);
        if (currentY < rectFProgress.centerY()) {
            a1 += 180;
        } else if (currentY > rectFProgress.centerY() / 2 && currentX > rectFProgress.centerX()) {
            a1 += 360;
        }


        return a1 - (360 - mArcFullDegree) / 2;
    }

    public class MarkerIndicator {
        private WindowManager mWindowManager;
        private WindowManager.LayoutParams windowManagerParams;
        private boolean mShowing;
        private Marker marker;
        private String mValue;

        public MarkerIndicator(Context context, View anchor, String maxValue) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            marker = new Marker(context, maxValue);
            windowManagerParams = new WindowManager.LayoutParams();
            initWindow(anchor);
        }

        public void initWindow(View anchor) {
            windowManagerParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL; // 设置window type
            windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
            // 设置Window flag
            windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            windowManagerParams.gravity = Gravity.START | Gravity.TOP;
            // 以屏幕左下角为原点，设置x、y初始值，使按钮定位在右下角
            Log.d("duanyl", "initWindow: " + anchor.getLeft() + "," + anchor.getTop()+","+((View)anchor.getParent()).getTop());
            windowManagerParams.x = anchor.getLeft()+((View)anchor.getParent()).getLeft();
            windowManagerParams.y = anchor.getTop()+((View)anchor.getParent()).getTop() + mBubbleDistance;
            // 设置悬浮窗口长宽数据
            windowManagerParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            windowManagerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            if (anchor.getWindowToken() != null) {
                windowManagerParams.token = anchor.getWindowToken();
            }
            windowManagerParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        }

        public void move(int x, String value) {
            windowManagerParams.x = x;
            this.mValue = value;
            if (mShowing) {
                marker.setValue(mValue);
                mWindowManager.updateViewLayout(marker, windowManagerParams);
            }
        }

        public void show() {
            if (!mShowing) {
                mWindowManager.addView(marker, windowManagerParams);
                marker.animateToPressed();
                mShowing = true;
            }
        }

        public void dismiss() {
            if (mShowing) {
                marker.animateToNormal();
                marker.removeCallbacks(runstop);
                marker.postDelayed(runstop, 250);

            }
        }
        Runnable runstop = new Runnable() {
            @Override
            public void run() {
                mWindowManager.removeViewImmediate(marker);
                mShowing = false;
            }
        };
        public boolean isShowing() {
            return mShowing;
        }
    }

    public interface OnProgressChangeListener {
        public void onProgressChanged(PSeekBar seekBar, int value, boolean fromUser);

        public void onStartTrackingTouch(PSeekBar seekBar);

        public void onStopTrackingTouch(PSeekBar seekBar);
    }

    public class Marker extends View {
        private static final int PADDING_DP = 4;
        private static final int ELEVATION_DP = 8;
        private final Paint mPaint, mPaintText;
        private final Rect textBounds;
        private int mWidth;
        private String strMax;
        private int padding;
        Path mPath = new Path();
        RectF mRect = new RectF();
        Matrix mMatrix = new Matrix();
        ValueAnimator animation;

        public Marker(Context context, String max) {
            super(context);
            this.strMax = max;
            setFocusable(true);
            setWillNotDraw(false);
            setVisibility(View.INVISIBLE);
            padding = (int) (PADDING_DP * density) * 2;
//            setPadding(padding, padding, padding, padding);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mBubbleColor);

            mPaintText = new Paint();
            mPaintText.setColor(Color.WHITE);
            mPaintText.setAntiAlias(true);
            mPaintText.setTextSize(20);
            textBounds = new Rect();
            mPaintText.getTextBounds(strMax, 0, strMax.length(), textBounds);
            mWidth = Math.max(textBounds.width(), textBounds.height()) + padding * 2;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(mWidth, mWidth * 2);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.d("duanyl", "onDraw: Maker :" + strMax);
            canvas.drawPath(mPath, mPaint);
            canvas.restore();
            mPaintText.getTextBounds(strMax, 0, strMax.length(), textBounds);
            float t = getMeasuredWidth() / 2 - textBounds.width() / 2;
            float b = getHeight() / 4 + textBounds.height() / 2;
            canvas.drawText(strMax, t, b, mPaintText);

        }

        public void setValue(String value) {
            strMax = value;
            postInvalidate();
        }

        private void computePath(float mCurrentScale) {
            final float currentScale = mCurrentScale;
            final Path path = mPath;
            final RectF rect = mRect;
            final Matrix matrix = mMatrix;
            final float left = getLeft();
            final float top = getTop();
            final float right = getRight();
            final float bottom = getBottom();
            path.reset();
            int totalSize = Math.min(getWidth(), getHeight());

            float initial = getWidth() / 2;
            float destination = totalSize;
            float currentSize = initial + (destination - initial) * currentScale;
            Log.d("duanyl", "computePath:initial  " + initial);
            float halfSize = currentSize / 2f;
            float inverseScale = 1f - currentScale;
            float cornerSize = halfSize * inverseScale;
            Log.d("duanyl", "computePath  cornerSize: " + cornerSize);
            float[] corners = new float[]{halfSize, halfSize, halfSize, halfSize, halfSize, halfSize, cornerSize, cornerSize};
            rect.set(left, top, left + currentSize, top + currentSize);
            path.addRoundRect(rect, corners, Path.Direction.CCW);
            matrix.reset();
            matrix.postRotate(-45, left + halfSize, top + halfSize);
            matrix.postTranslate((totalSize - currentSize) / 2, 0);
            float hDiff = (bottom - currentSize - padding) * inverseScale;
            matrix.postTranslate(0, hDiff);
            path.transform(matrix);
            path.close();
        }


        private void updateAnimation(final float factor) {
            computePath(factor);
            postInvalidate();
            post(new Runnable() {
                @Override
                public void run() {
                    setAlpha(256 * factor);
                }
            });
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void animateToPressed() {
            setVisibility(VISIBLE);
            animation = ValueAnimator.ofFloat(0f, 1f);
            animation.setDuration(250);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Log.i("update", ((Float) animation.getAnimatedValue()).toString());
                    updateAnimation((Float) animation.getAnimatedValue());
                }
            });
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void animateToNormal() {
            strMax = "";
            animation = ValueAnimator.ofFloat(1f, 0f);
            animation.setDuration(250);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Log.i("update", ((Float) animation.getAnimatedValue()).toString());
                    updateAnimation((Float) animation.getAnimatedValue());
                }
            });
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
        }
    }

}
