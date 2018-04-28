package com.cvlib;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Callanna on 2018/1/22.
 */

public class FlipboardView extends View {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Bitmap bitmap;
    Camera camera = new Camera();
    int left_degree = 0,right_degree =  -60,degree_canvas;
    // ObjectAnimator animator1 = ObjectAnimator.ofInt(this, "left_degree", 0, 45);
    // ObjectAnimator animator2 = ObjectAnimator.ofInt(this, "right_degree", 0, -60);
    ObjectAnimator animator3 = ObjectAnimator.ofInt(this, "degree_canvas", 0, 360);
    Handler handler = new Handler();
    AnimatorSet animatorSet = new AnimatorSet();
    public FlipboardView(Context context) {
        super(context);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bg);
        // animator2.setDuration(1000);
        //animator2.setRepeatCount(ValueAnimator.INFINITE);
        //animator2.setRepeatMode(ValueAnimator.RESTART);
        animator3.setDuration(1000);
        animator3.setStartDelay(1000);
        animator3.setRepeatCount(ValueAnimator.INFINITE);
        animator3.setRepeatMode(ValueAnimator.RESTART);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator3.start();
        //animator2.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //animator2.end();
        animator3.end();
    }

    public void setDegree_canvas(int degree_canvas) {
        this.degree_canvas = degree_canvas;
        invalidate();
    }

    public int getDegree_canvas() {
        return degree_canvas;
    }

    public void setLeft_degree(int left_degree) {
        this.left_degree = left_degree;
        invalidate();
    }

    public int getLeft_degree() {
        return left_degree;
    }

    public void setRight_degree(int right_degree) {
        this.right_degree = right_degree;
        invalidate();
    }

    public int getRight_degree() {
        return right_degree;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;

        canvas.save();
        canvas.translate(centerX,centerY);
        canvas.rotate(-degree_canvas);
        camera.save();
        camera.rotateY(right_degree);
        camera.applyToCanvas(canvas);
        canvas.clipRect(0,-centerY,centerX,centerY);
        canvas.rotate(degree_canvas);
        canvas.translate(-centerX,-centerY);
        camera.restore();
        canvas.drawBitmap(bitmap,x,y,paint);
        canvas.restore();

        canvas.save();
        canvas.translate(centerX,centerY);
        canvas.rotate(-degree_canvas);
        camera.save();
        canvas.clipRect(-centerX,-centerY,0,centerY);
        camera.rotateY(left_degree);
        camera.applyToCanvas(canvas);

        canvas.rotate(degree_canvas);
        canvas.translate(-centerX,-centerY);
        camera.restore();
        canvas.drawBitmap(bitmap,x,y,paint);
        canvas.restore();

    }
}
