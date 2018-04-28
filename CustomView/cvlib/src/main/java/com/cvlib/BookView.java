package com.cvlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * Created by Callanna on 2018/1/3.
 */

public class BookView extends View {
    private final static int STYLE_LEFT = 1;
    private final static int STYLE_RIGHT = 2;
    private final static int STYLE_TOP = 3;
    private final static int STYLE_BOTTOM = 4;
    private final static int STYLE_MIDDLE = 5;

    private int default_height, default_width;
    private int mWidth, mHeight;

    private Paint pathAPaint, pathBPaint, pathCPaint, pathShadowPaint;
    private TextPaint textPaint;

    private Path pathA, pathB, pathC, pathShadow;
    private Bitmap contentABitmap, contentBbitmap, contentCbitmap;
    private PointF a, b, c, d, e, f, g, h, i, j, k;
    private String mTextFirst = "Systrace是分析Android性能问题的神器，Google IO 2017上更是对其各种强推；由于TraceView过于严重的运行时开销，我怀疑这个方向是不是压根儿就是错误的。个人预计Google会放弃TraceView转向全力支持Systrace；不过这个工具并不像TraceView那样简单直观，使用起来也不太方便，而且没有一个详尽的文档介绍如何使用和分析；本文和后续旨在弥补这一块的缺失，尽可能地完整介绍Systrace的方方面面。";
    private String mTextSecond = "在介绍使用之前，先简单说明一下Systrace的原理：它的思想很朴素，在系统的一些关键链路（比如System Service，虚拟机，Binder驱动）插入一些信息（我这里称之为Label），通过Label的开始和结束来确定某个核心过程的执行时间，然后把这些Label信息收集起来得到系统关键路径的运行时间信息，进而得到整个系统的运行性能信息。Android Framework里面一些重要的模块都插入了Label信息（Java层的通过android.os.Trace类完成，native层通过ATrace宏完成），用户App中可以添加自定义的Label，这样就组成了一个完成的性能分析系统。";
    private int mCurrentStyle = STYLE_BOTTOM;
    private Scroller mScroller;
    private Matrix mMatrix;
    private int colorbg = Color.parseColor("#21B548");
    private float[] mMatrixArray;
    private Paint pointPaint;
    private int shadowColor = Color.parseColor("#883c3c3c");
    private int mashWidth = 20, mashHeight = 20;
    private int POINT_COUNT = (mashHeight + 1) * (mashWidth + 1);
    private float[] orig = new float[POINT_COUNT * 2];//乘以２是因为x,y值是一对的。
    private float[] verts = new float[POINT_COUNT * 2];

    public BookView(Context context) {
        this(context, null);
    }

    public BookView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        default_height = 600;
        default_width = 400;
        pathA = new Path();
        pathAPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathAPaint.setColor(colorbg);

        pathB = new Path();
        pathBPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathBPaint.setColor(colorbg);

        pathC = new Path();
        pathCPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathCPaint.setStyle(Paint.Style.FILL);
        pathCPaint.setColor((int) (colorbg));
        pathCPaint.setAlpha(200);
        //pathCPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID));

        pathShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathShadowPaint.setStyle(Paint.Style.STROKE);
        pathShadowPaint.setStrokeWidth(10);
        pathShadowPaint.setColor(Color.TRANSPARENT);


        pathShadow = new Path();

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.RED);
        a = new PointF(-1, -1);
        b = new PointF();
        c = new PointF();
        d = new PointF();
        e = new PointF();
        f = new PointF();
        g = new PointF();
        h = new PointF();
        i = new PointF();
        j = new PointF();
        k = new PointF();
        mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
        mMatrix = new Matrix();
        mMatrixArray = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 1.0f};
    }

    private void initData() {
        float baseBitmapWidth = getWidth();
        float baseBitmapHeight = getHeight();
        int index = 0;
        //通过遍历所有的划分后得到的像素块，得到原图中每个交叉点的坐标，并把它们保存在orig数组中
        for (int i = 0; i <= mashHeight; i++) {//因为这个数组是采取行优先原则储存点的坐标，所以最外层为纵向的格子数,然后一行一行的遍历
            float fy = baseBitmapHeight * i / mashHeight;//得到每行中每个交叉点的y坐标,同一行的y坐标一样
            for (int j = 0; j <= mashWidth; j++) {
                float fx = baseBitmapWidth * j / mashWidth;//得到每行中的每个交叉点的x坐标,同一列的x坐标一样
                orig[index * 2 + 0] = verts[index * 2 + 0] = fx;//存储每行中每个交叉点的x坐标，为什么是index*2+0作为数组的序号呢？？
                //因为我们之前也说过这个数组既存储x坐标也存储y坐标，所以每个点就占有２个单位数组空间
                orig[index * 2 + 1] = verts[index * 2 + 1] = fy;//存储每行中每个交叉点的y坐标.

                index++;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureSize(default_width, widthMeasureSpec);
        int height = measureSize(default_height, heightMeasureSpec);
        setMeasuredDimension(width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        createContentBitmap();
        initData();
    }

    private void createContentBitmap() {
        contentABitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(contentABitmap);
        canvas.drawPath(getDefaultPath(), pathAPaint);
        textPaint.setAlpha(255);
        StaticLayout sl = new StaticLayout(mTextFirst, textPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(500, 0);
        sl.draw(canvas);

        contentBbitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(contentBbitmap);
        canvas2.drawPath(getDefaultPath(), pathAPaint);
        textPaint.setAlpha(250);
        StaticLayout sl3 = new StaticLayout(mTextSecond, textPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas2.translate(500, 0);
        sl3.draw(canvas2);

        contentCbitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas3 = new Canvas(contentCbitmap);
        canvas3.drawPath(getDefaultPath(), pathCPaint);
        textPaint.setAlpha(150);
        StaticLayout sl2 = new StaticLayout(mTextFirst, textPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas3.translate(500, 0);
        sl2.draw(canvas3);
    }

    private Path getDefaultPath() {
        pathA.reset();
        pathA.moveTo(0, 0);
        pathA.lineTo(mWidth, 0);
        pathA.lineTo(mWidth, mHeight);
        pathA.lineTo(0, mHeight);
        pathA.close();
        return pathA;
    }

    private int measureSize(int default_width, int widthMeasureSpec) {
        int val = default_width;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                val = default_width;
            case MeasureSpec.EXACTLY:
                val = size;
        }
        return val;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colorbg);
        if (a.x == -1 && a.y == -1) {
            getDefaultPath();
            canvas.save();
            canvas.clipPath(pathA, Region.Op.INTERSECT);
            canvas.drawBitmap(contentABitmap, 0, 0, pathAPaint);
            canvas.restore();
        } else {
            drawAContent(canvas);
            drawBContent(canvas);
            drawCContent(canvas);
            drawShadower(canvas);
        }
        //绘制各标识点
        //drawTagPoint(canvas);
    }

    private void drawShadower(Canvas canvas) {
        canvas.save();
        pathShadow.reset();
        pathShadow.moveTo(c.x, c.y);//移动到c点
        pathShadow.quadTo(e.x, e.y, b.x, b.y);//从c到b画贝塞尔曲线，控制点为e
        pathShadow.lineTo(a.x, a.y);//移动到a点
        pathShadow.lineTo(k.x, k.y);//移动到k点
        pathShadow.quadTo(h.x, h.y, j.x, j.y);//从k到j画贝塞尔曲线，控制点为h
        pathShadowPaint.setShadowLayer(30, 0, 0, shadowColor);
        canvas.drawPath(pathShadow, pathShadowPaint);
        canvas.restore();

        canvas.save();
        canvas.translate((e.x - c.x) / 2, 0);
        pathShadowPaint.setStrokeWidth(15);
        pathShadowPaint.setShadowLayer(50, 0, 0, shadowColor);

        canvas.drawLine(c.x, c.y, j.x, j.y, pathShadowPaint);
        canvas.restore();
    }

    private void drawTagPoint(Canvas canvas) {
        canvas.drawText("a", a.x, a.y, pointPaint);
        canvas.drawText("f", f.x, f.y, pointPaint);
        canvas.drawText("g", g.x, g.y, pointPaint);

        canvas.drawText("e", e.x, e.y, pointPaint);
        canvas.drawText("h", h.x, h.y, pointPaint);

        canvas.drawText("c", c.x, c.y, pointPaint);
        canvas.drawText("j", j.x, j.y, pointPaint);

        canvas.drawText("b", b.x, b.y, pointPaint);
        canvas.drawText("k", k.x, k.y, pointPaint);

        canvas.drawText("d", d.x, d.y, pointPaint);
        canvas.drawText("i", i.x, i.y, pointPaint);

        canvas.drawPath(pathC, pointPaint);
    }

    private void drawCContent(Canvas canvas) {
        canvas.save();
        canvas.clipPath(getPathA());
        canvas.clipPath(getPathC(), Region.Op.REVERSE_DIFFERENCE);
        float eh = (float) Math.hypot(f.x - e.x, h.y - f.y);
        float sin0 = (f.x - e.x) / eh;
        float cos0 = (h.y - f.y) / eh;
        float degree = (int) Math.asin(sin0);
        //设置翻转和旋转矩阵
        mMatrixArray[0] = -(1 - 2 * sin0 * sin0);
        mMatrixArray[1] = 2 * sin0 * cos0;
        mMatrixArray[3] = 2 * sin0 * cos0;
        mMatrixArray[4] = 1 - 2 * sin0 * sin0;
        mMatrix.reset();
        mMatrix.setValues(mMatrixArray);//翻转和旋转

        mMatrix.preTranslate(-e.x, -e.y);//沿当前XY轴负方向位移得到 矩形A₃B₃C₃D₃
        mMatrix.postTranslate(e.x, e.y);//沿原XY轴方向位移得到 矩形A4 B4 C4 D4

        canvas.drawPath(pathC, pathCPaint);

        canvas.drawBitmap(contentCbitmap, mMatrix, pathCPaint);
        canvas.restore();

    }

    private void drawBContent(Canvas canvas) {
        canvas.save();
        canvas.clipPath(getPathA());
        canvas.clipPath(getPathC(), Region.Op.UNION);
        canvas.clipPath(getPathB(), Region.Op.REVERSE_DIFFERENCE);
        canvas.drawBitmap(contentBbitmap, 0, 0, pathBPaint);
        canvas.restore();
    }

    private void drawAContent(Canvas canvas) {
        canvas.save();
        canvas.clipPath(getPathA(), Region.Op.INTERSECT);
        //canvas.drawBitmap(contentABitmap,0,0,pathAPaint);
        canvas.drawBitmapMesh(contentABitmap, mashWidth, mashHeight, verts, 0, null, 0, null);
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x < mWidth / 3) {
                    mCurrentStyle = STYLE_LEFT;
                } else if (x > mWidth * 2 / 3 && y < mHeight / 3) {
                    mCurrentStyle = STYLE_TOP;
                } else if (x > mWidth * 2 / 3 && y > mHeight * 2 / 3) {
                    mCurrentStyle = STYLE_BOTTOM;
                } else {
                    mCurrentStyle = STYLE_MIDDLE;
                }
                setTouchPoint(x, y, mCurrentStyle);
                break;
            case MotionEvent.ACTION_MOVE:
                setTouchPoint(x, y, mCurrentStyle);
                break;
            case MotionEvent.ACTION_UP:
                startFilpAnim();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currentx = mScroller.getCurrX();
            int currenty = mScroller.getCurrY();
            setTouchPoint(currentx, currenty, mCurrentStyle);
            if (mScroller.getFinalX() == currentx && mScroller.getFinalY() == currenty) {
                setDefaultPoint();
            }
        }
    }

    private void setDefaultPoint() {
        a.x = -1;
        a.y = -1;
        postInvalidate();
    }

    private void startFilpAnim() {
        int dx, dy;
        //让a滑动到f点所在位置，留出1像素是为了防止当a和f重叠时出现View闪烁的情况
        if (mCurrentStyle == STYLE_TOP) {
            dy = a.x > mWidth * 2 / 3 ? (int) (1 - a.y) : (int) (mHeight - 1 - a.y);
        } else {
            dy = a.x < mWidth * 2 / 3 ? (int) (1 - a.y) : (int) (mHeight - 1 - a.y);
        }
        dx = a.x > mWidth * 2 / 3 ? (int) (mWidth - 1 - a.x) : (int) (1 - a.x);

        mScroller.startScroll((int) a.x, (int) a.y, dx, dy, 400);
    }

    private void setTouchPoint(float x, float y, int mCurrentStyle) {
        a.x = x;
        a.y = y;
        switch (mCurrentStyle) {
            case STYLE_LEFT:
            case STYLE_RIGHT:
                a.y = mHeight - 1;
                f.y = mHeight;
                f.x = mWidth;
                break;
            case STYLE_TOP:
                f.y = 0;
                f.x = mWidth;

                break;
            case STYLE_BOTTOM:
                f.y = mHeight;
                f.x = mWidth;
                break;
        }
        calculatePathPoint();
        if (c.x < 0) {
            resetPointA();
            calculatePathPoint();
        }
        postInvalidate();
    }

    private void resetPointA() {
        float w0 = mWidth - c.x;
        float w1 = Math.abs(f.x - a.x);
        float w2 = mWidth * w1 / w0;
        a.x = Math.abs(f.x - w2);
        float h1 = Math.abs(f.y - a.y);
        float h2 = w2 * h1 / w1;
        a.y = Math.abs(f.y - h2);
    }

    private void calculatePathPoint() {
        g.x = (a.x + f.x) / 2;
        g.y = (a.y + f.y) / 2;

        e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x);
        e.y = f.y;

        h.x = f.x;
        h.y = g.y - (f.x - g.x) * (f.x - g.x) / (f.y - g.y);

        c.x = e.x - (f.x - e.x) / 2;
        c.y = f.y;

        j.x = f.x;
        j.y = h.y - (f.y - h.y) / 2;

        b = getIntersectionPoint(a, e, c, j);
        k = getIntersectionPoint(a, h, c, j);

        d.x = (c.x + 2 * e.x + b.x) / 4;
        d.y = (2 * e.y + c.y + b.y) / 4;

        i.x = (j.x + 2 * h.x + k.x) / 4;
        i.y = (2 * h.y + j.y + k.y) / 4;
        warp();
    }

    private void warp() {
        for (int p = 0; p <= POINT_COUNT; p += 2) {
            float ox = orig[p + 0];
            float oy = orig[p+ 1];

                float disx = (float) Math.sqrt((ox - d.x) * (ox - d.x) + (oy - d.y) * (oy - d.y));
                if (disx < 50) {
                    verts[p + 0] = ox + 10 * ((d.x - ox) / disx);
                    verts[p + 1] = oy + 10 * ((d.y - oy) / disx);
                }


                float disy = (float) Math.sqrt((ox - i.x) * (ox - i.x) + (oy - i.y) * (oy - i.y));
                if (disy < 50) {
                    verts[p + 0] = ox + 10 * ((i.x - ox) / disy);
                    verts[p + 1] = oy + 10 * ((i.y - oy) / disy);
                }

        }
    }

    private Path getPathA() {
        pathA.reset();
        if (mCurrentStyle == STYLE_BOTTOM) {
            pathA.lineTo(0, mHeight);//移动到左下角
        }
        pathA.lineTo(c.x, c.y);//移动到c点
        pathA.quadTo(e.x, e.y, b.x, b.y);//从c到b画贝塞尔曲线，控制点为e
        pathA.lineTo(a.x, a.y);//移动到a点
        pathA.lineTo(k.x, k.y);//移动到k点
        pathA.quadTo(h.x, h.y, j.x, j.y);//从k到j画贝塞尔曲线，控制点为h
        if (mCurrentStyle == STYLE_BOTTOM) {
            pathA.lineTo(mWidth, 0);//移动到右上角
        } else {
            pathA.lineTo(mWidth, mHeight);//移动到右下角
            pathA.lineTo(0, mHeight);//移动到左下角
        }
        pathA.close();
        return pathA;
    }

    private Path getPathB() {
        pathB.reset();
        pathB.lineTo(0, mHeight);//移动到左下角
        pathB.lineTo(mWidth, mHeight);//移动到右下角
        pathB.lineTo(mWidth, 0);//移动到右上角
        pathB.close();//闭合区域

        return pathB;
    }

    public Path getPathC() {
        pathC.reset();
        pathC.moveTo(i.x, i.y);//移动到i点
        pathC.lineTo(d.x, d.y);//移动到d点
        pathC.lineTo(b.x, b.y);//移动到b点
        pathC.lineTo(a.x, a.y);//移动到a点
        pathC.lineTo(k.x, k.y);//移动到k点
        pathC.close();
        return pathC;
    }

    /**
     * 计算两线段相交点坐标
     *
     * @param lineOne_My_pointOne
     * @param lineOne_My_pointTwo
     * @param lineTwo_My_pointOne
     * @param lineTwo_My_pointTwo
     * @return 返回该点
     */
    private PointF getIntersectionPoint(PointF lineOne_My_pointOne, PointF lineOne_My_pointTwo, PointF lineTwo_My_pointOne, PointF lineTwo_My_pointTwo) {
        float x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = lineOne_My_pointOne.x;
        y1 = lineOne_My_pointOne.y;
        x2 = lineOne_My_pointTwo.x;
        y2 = lineOne_My_pointTwo.y;
        x3 = lineTwo_My_pointOne.x;
        y3 = lineTwo_My_pointOne.y;
        x4 = lineTwo_My_pointTwo.x;
        y4 = lineTwo_My_pointTwo.y;

        float pointX = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        float pointY = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));

        return new PointF(pointX, pointY);
    }

}
