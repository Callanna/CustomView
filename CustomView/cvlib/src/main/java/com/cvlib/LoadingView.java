package com.cvlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.callanna.viewlibrary.R;

/**
 * Created by Callanna on 2017/6/17.
 */

public class LoadingView extends View {



    private Paint mPaint;


    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        int textColor = array.getColor(R.styleable.LoadingView_loadcolor, 0XFF00FF00); //提供默认值，放置未指定
        float textSize = array.getDimension(R.styleable.LoadingView_loadtextSize, 20);
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        array.recycle();
        mPaint = new Paint();
    }
}
