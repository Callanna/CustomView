package com.cvlib;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.callanna.viewlibrary.R;

/**
 * Created by Callanna on 2017/9/19.
 */

public class CardView extends RelativeLayout {

    private Bitmap mCacheBitmap;
    private Drawable drawablebg;

    private Path mCornerShadowPath;
    private Paint mCornerShadowPaint;
    private Paint mEdgeShadowPaint;

    private RectF mBoundsF = new RectF();

    private int mInsetShadow;
    private float mMaxShadowSize;
    private float mRawMaxShadowSize;
    private float mShadowSize;
    private float mRawShadowSize;

    private int radius;
    private Rect mBgBounds = new Rect();
    private Rect mBounds = new Rect();
    float mCornerRadius;

    private int mShadowStartColor;
    private int mShadowEndColor;
    private int ref_img;


    public CardView(Context context) {
        this(context, null);
    }

    public CardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, context.getResources(), defStyleAttr);
    }
    public void init(Context context, AttributeSet attrs, Resources resources, int defStyleAttr){

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaterialImageView,
                defStyleAttr, 0);
        int shadowSize = a.getInt(R.styleable.MaterialImageView_shadow_size, 8);
        radius = a.getInt(R.styleable.MaterialImageView_radius_size, 5);//radius size
        drawablebg =  a.getDrawable(R.styleable.MaterialImageView_src );
        a.recycle();
        setWillNotDraw(false);
         if(drawablebg == null){
             drawablebg = getBackground();
         }
        mShadowStartColor = resources.getColor(R.color.shadow_start_color);
        mShadowEndColor = resources.getColor(R.color.shadow_end_color);

        mInsetShadow = resources.getDimensionPixelSize(R.dimen.cardview_compat_inset_shadow);
        mCornerRadius = ((int) (radius + 0.5F));

        mCornerShadowPaint = new Paint(Paint.DITHER_FLAG | Paint.HINTING_ON);
        mCornerShadowPaint.setStyle(Paint.Style.FILL);
        mCornerShadowPaint.setAntiAlias(true);
        mEdgeShadowPaint = new Paint(mCornerShadowPaint);
        mEdgeShadowPaint.setAntiAlias(true);
        setShadowSize(shadowSize, 20);
    }

    void setShadowSize(float shadowSize, float maxShadowSize) {
        if((shadowSize < 0.0f) || (maxShadowSize < 0.0f)){
            throw new IllegalArgumentException("invalid shadow size");
        }

        if(shadowSize > maxShadowSize){
            shadowSize = maxShadowSize;
        }

        mRawShadowSize = shadowSize;
        mRawMaxShadowSize = maxShadowSize;
        mShadowSize = ((int) (shadowSize * 1.5F + mInsetShadow + 0.5F));
        mMaxShadowSize = (maxShadowSize + mInsetShadow);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("duanyl", "onMeasure: ");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0 ;
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        } else
        {
            if(drawablebg != null)
              width = drawablebg.getIntrinsicWidth() ;
        }

        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        } else{
            if(drawablebg != null)
              height = drawablebg.getIntrinsicHeight();
        }

        setMeasuredDimension(width +(int)mRawMaxShadowSize *2, height+(int)mRawMaxShadowSize *3);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("duanyl", "onSizeChanged: "+w+","+h );
        mBounds.set(0, 0, w, h);
        mBoundsF.set(0, 0, w, h);
        buildShadow(0, 0,w, h);
        mCacheBitmap = drawableToBitmap(drawablebg,w,h);
        super.onSizeChanged(w  , h  , oldw, oldh);
    }
    private void buildShadow(int left, int top, int right, int bottom){
        float verticalOffset = mRawMaxShadowSize * 1.5F;
        mBgBounds.set(left + (int)mRawMaxShadowSize, top +(int)verticalOffset,
                right - (int)mRawMaxShadowSize, bottom - (int)verticalOffset);
        buildShadowCorners();
    }

    private void buildShadowCorners() {
        RectF innerBounds = new RectF(-mCornerRadius, -mCornerRadius, mCornerRadius, mCornerRadius);
        RectF outerBounds = new RectF(innerBounds);
        outerBounds.inset(-mShadowSize, -mShadowSize);

        if (mCornerShadowPath == null)
            mCornerShadowPath = new Path();
        else {
            mCornerShadowPath.reset();
        }

        mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        mCornerShadowPath.moveTo(-mCornerRadius, 0.0F);
        mCornerShadowPath.rLineTo(-mShadowSize, 0.0F);
        mCornerShadowPath.arcTo(outerBounds, 180.0F, 90.0F, false);
        mCornerShadowPath.arcTo(innerBounds, 270.0F, -90.0F, false);
        mCornerShadowPath.close();

        float startRatio = mCornerRadius / (mCornerRadius + mShadowSize);
        mCornerShadowPaint.setShader(
                new RadialGradient(0.0F, 0.0F, mCornerRadius + mShadowSize,//阴影半径
                        new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
                        new float[]{0.0F, startRatio, 1.0F},
                        Shader.TileMode.CLAMP));

        mEdgeShadowPaint.setShader(
                new LinearGradient(
                        0.0F, -mCornerRadius  + mShadowSize, 0.0F, -mCornerRadius - mShadowSize,
                        new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
                        new float[]{0.0F, 0.5F, 1.0F}, Shader.TileMode.CLAMP));

        mEdgeShadowPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0.0F, mRawShadowSize / 2.0F);
        drawShadow(canvas);
        canvas.translate(0.0F, -mRawShadowSize / 2.0F);
        canvas.drawBitmap(createRoundImage(mCacheBitmap),0,0,null);
        canvas.translate(0.0F,  0F);
        super.onDraw(canvas);
    }
    public  Bitmap drawableToBitmap(Drawable drawable,int w,int h) {
        // 取 drawable 的颜色格式
        Bitmap.Config config =  Bitmap.Config.ARGB_8888 ;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);

        return bitmap;
    }
    /**
     * 创建圆角图片
     * @param bitmap 源图片
     * @return 圆角图片
     */
    private Bitmap createRoundImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height= bitmap.getHeight();
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿画笔
        Bitmap target = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);//创建一个bigmap
        Canvas canvas=new Canvas(target);//创建一个画布
        RectF rectF=new RectF(mBgBounds);//矩形
        //绘制圆角矩形
        canvas.drawRoundRect(rectF,mCornerRadius,mCornerRadius,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//画笔模式
        canvas.drawBitmap(bitmap,0,0, paint);//将画笔
        return target;
    }
    private void drawShadow(Canvas canvas) {
        float edgeShadowTop = -mCornerRadius - mShadowSize;
        float inset = mCornerRadius + mInsetShadow + mRawShadowSize / 2.0F;
        boolean drawHorizontalEdges = mBgBounds.width() - 2.0F * inset > 0.0F;
        boolean drawVerticalEdges = mBgBounds.height() - 2.0F * inset > 0.0F;

        int saved;

        saved = canvas.save();
        canvas.translate(mBgBounds.left + inset, mBgBounds.top + inset);
        if (drawHorizontalEdges) {
            canvas.drawRect(0.0F, edgeShadowTop, mBgBounds.width() - 2.0F * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);//和下面不同  这句放在这里 才不会出现旋转后阴影缺角
        canvas.restoreToCount(saved);

        saved = canvas.save();
        canvas.translate(mBgBounds.left + inset, mBgBounds.bottom - inset);
        canvas.rotate(270.0F);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0.0F, edgeShadowTop, mBgBounds.height() - 2.0F * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);

        saved = canvas.save();
        canvas.translate(mBgBounds.right - inset, mBgBounds.top + inset);
        canvas.rotate(90.0F);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0.0F, edgeShadowTop, mBgBounds.height() - 2.0F * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);

        saved = canvas.save();
        canvas.translate(mBgBounds.right - inset, mBgBounds.bottom - inset);
        canvas.rotate(180.0F);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0.0F, edgeShadowTop, mBgBounds.width() - 2.0F * inset, -mCornerRadius + mShadowSize, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
    }

}
