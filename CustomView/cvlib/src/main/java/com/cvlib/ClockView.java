package com.cvlib;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import com.callanna.viewlibrary.R;

/**
 * Created by Callanna on 2016/3/30.
 *  屏保时钟
 *  使用时参看 app 中 activity_clock布局文件
 */
public class ClockView extends View {
    /**
     * 钟盘图片资源
     */
    private Drawable clockDrawable;

    /**
     钟盘中心点图片资源
     */
    private Drawable centerDrawable;
    /**
     * 时针图片资源
     */
    private Drawable hourDrawable;
    /**
     * 分针图片资源
     */
    private Drawable minuteDrawable;
    /**
     * 秒针图片资源
     */
    private Drawable secondDrawable;
    /**
     * 针头长度
     */
    private int headDistance;
    /**
     * 针头长度
     */
    private int endDistance;
    /**
     * 画笔
     */
    private Paint paint;

    /**
     * 是否发生变化
     */
    private boolean isChange;

    /**
     * 时间类，用来获取系统的时间
     */
    private Time time;
    /**
     * 每1秒重绘线程
     */
    private Thread clockThread;
    /**
     * 构造方法
     */
    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 初始化图片资源和画笔
     */
    public ClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SrceenClockStyleable, defStyle, 0);
        //获取自定义属性
        clockDrawable = ta.getDrawable(R.styleable.SrceenClockStyleable_clock);
        centerDrawable = ta.getDrawable(R.styleable.SrceenClockStyleable_center_dot);
        hourDrawable = ta.getDrawable(R.styleable.SrceenClockStyleable_hour);
        minuteDrawable = ta.getDrawable(R.styleable.SrceenClockStyleable_minute);
        secondDrawable = ta.getDrawable(R.styleable.SrceenClockStyleable_second);
        headDistance = (int) ta.getDimension(R.styleable.SrceenClockStyleable_head_dis,0);
        endDistance = (int) ta.getDimension(R.styleable.SrceenClockStyleable_end_dis,0);
        //释放资源，清注意TypedArray对象是一个shared资源，必须被在使用后进行回收。
        ta.recycle();
        //初始化画笔
        paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);
        //开启线程1秒重绘一次
        time = new Time();
        clockThread = new Thread() {
            Thread thisThread = Thread.currentThread();
            public void run() {
                while(isChange){
                    postInvalidate();
                    try {
                        thisThread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        };
        clockThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置当前的时间
        time.setToNow();
        //计算自定义view 的中心位置
        int viewCenterX = (getRight() - getLeft()) / 2;
        int viewCenterY = (getBottom() - getTop()) / 2;
        final Drawable dial = clockDrawable;
        //获取clockDrawable 的高度和宽度(即图片的高度和宽度)

        int h = dial.getIntrinsicHeight();
        int w = dial.getIntrinsicWidth();
        if ((getRight() - getLeft()) < h || (getBottom() - getTop()) < w) {
            float scale = Math.min((float) (getRight() - getLeft()) / w,
                    (float) (getBottom() - getTop()) / h);
            canvas.save();
            canvas.scale(scale, scale, viewCenterX, viewCenterY);
        }
        if (isChange) {
            //设置界线
            dial.setBounds(viewCenterX - (w/2), viewCenterY - (h/2), viewCenterX + (w /2), viewCenterY + (h /2));
        }
        dial.draw(canvas);
        canvas.save();

        //用canvas 画中心点
        if(centerDrawable != null) {
            Drawable dot = centerDrawable;
            if (isChange) {
                h = dot.getIntrinsicHeight();
                w = dot.getIntrinsicWidth();
                //设置界线
                dot.setBounds(viewCenterX - (w / 2), viewCenterY - (h / 2), viewCenterX + (w / 2), viewCenterY + (h / 2));
            }
            dot.draw(canvas);
            canvas.save();
        }
        //用canvas 画时针
        if(hourDrawable != null) {
            canvas.rotate(time.hour / 12.0f * 360.0f + time.minute / 60 * 30.0f, viewCenterX, viewCenterY);
            Drawable mHour = hourDrawable;
            h = mHour.getIntrinsicHeight();
            w = mHour.getIntrinsicWidth();
            if (isChange) { //设置界线
                mHour.setBounds(viewCenterX-6 , viewCenterY -(h/2), viewCenterX + (w ), viewCenterY  +(h/2));
            }
            mHour.draw(canvas);
            canvas.restore();
            canvas.save();
        }
        //用canvas 话分针
        if(minuteDrawable != null) {
            canvas.rotate(time.minute / 60.0f * 360.0f, viewCenterX, viewCenterY);
            Drawable mMinute = minuteDrawable;
            if (isChange) {
                w = mMinute.getIntrinsicWidth();
                h = mMinute.getIntrinsicHeight();
                //设置界线
                mMinute.setBounds(viewCenterX-6, viewCenterY - (h / 2), viewCenterX + w, viewCenterY +(h/2));
            }
            mMinute.draw(canvas);
            canvas.restore();
            canvas.save();
        }

        //用canvas 话秒针
        if(secondDrawable != null) {
            canvas.rotate(time.minute / 60.0f * 360.0f, viewCenterX, viewCenterY);
            Drawable mSecond = secondDrawable;
            if (isChange) {
                w = mSecond.getIntrinsicWidth();
                h = mSecond.getIntrinsicHeight();
                //设置界线
                mSecond.setBounds(viewCenterX-20 , viewCenterY - (h / 2), viewCenterX + w, viewCenterY +(h/2));
            }
            mSecond.draw(canvas);
            canvas.restore();
            canvas.save();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isChange = true;
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        isChange = false;
    }
}
