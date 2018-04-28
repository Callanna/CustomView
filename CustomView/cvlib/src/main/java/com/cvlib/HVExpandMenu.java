package com.cvlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * Created by Callanna on 18/4/11.
 */

public class HVExpandMenu extends RelativeLayout {
    public static final int ExpandHorizon = 0;

    public static final int ExpandVertical = 1;

    public static final  int ExpandButtonLeft = 0;

    public static final  int ExpandButtonRight = 1;

    public static final  int ExpandButtonTop = 2;

    public static final  int ExpandButtonBottom = 3;

    private float defaultWidth,defaultHeight;

    private float viewWidth,viewHeight;

    private float viewLeft,viewRight;

    private float backPathWidth;//绘制子View区域宽度

    private float maxBackPathWidth;//绘制子View区域最大宽度

    private int expandButtonIconSize;

    private int expandButtonRotateDegrees;

    private int expandButtonIconColor;

    private int expandButtonIconStorke;

    private int expandButtonStyle,viewOrientation;

    private int viewBackgroudColor;

    private int viewBolderWidth,viewBolderColor,viewBolderCornerRadius;

    private Point rightButtonIconCenter,leftButtonIconCenter;

    private float rightButtonIconLeft,rightButtonIconRight,leftButtonIconLeft,leftButtonIconRight;
    private int buttonRadius;//按钮矩形区域内圆半径
    private float buttonTop;//按钮矩形区域top值
    private float buttonBottom;//按钮矩形区域bottom值

    private boolean isFirstLayout;

    private boolean isExpand;

    private boolean isAnimEnd;

    private View childView;

    private float downX = -1;

    private float downY = -1;

    private int expandAnimTime;//展开收起菜单的动画时间

    private ExpandMenuAnimation expandMenuAnimtion;

    private Path path;

    private Paint paint;


    public HVExpandMenu(Context context) {
        this(context,null);
    }

    public HVExpandMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HVExpandMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.HVExpandMenu);
        viewBackgroudColor = typedArray.getColor(R.styleable.HVExpandMenu_menuBackgroudColor, Color.WHITE);
        viewBolderColor = typedArray.getColor(R.styleable.HVExpandMenu_menuBolderColor, Color.GRAY);
        viewBolderCornerRadius = (int) typedArray.getDimension(R.styleable.HVExpandMenu_menuCornerRadius,10);
        viewBolderWidth = (int) typedArray.getDimension(R.styleable.HVExpandMenu_menuBolderWidth,1);
        viewOrientation = typedArray.getInteger(R.styleable.HVExpandMenu_menuOrientation,0);
        expandAnimTime = typedArray.getInteger(R.styleable.HVExpandMenu_menuExpandAminTime,500);
        expandButtonStyle = typedArray.getInteger(R.styleable.HVExpandMenu_menuButtonStyle,0);
        expandButtonIconSize = (int) typedArray.getDimension(R.styleable.HVExpandMenu_menuExpandButtonSize,10);
        expandButtonIconStorke = (int) typedArray.getDimension(R.styleable.HVExpandMenu_menuExpandButtonStroke,2);
        expandButtonIconColor = typedArray.getColor(R.styleable.HVExpandMenu_menuExpandButtonColor, Color.RED);
        typedArray.recycle();
        expandButtonRotateDegrees = 0;
        isAnimEnd = true;
        isExpand = false;
        isFirstLayout = true;

        if(viewOrientation == ExpandHorizon) {
            defaultHeight = dp2px(50);
            defaultWidth = dp2px(200);
        }else{
            defaultHeight = dp2px(250);
            defaultWidth = dp2px(50);
        }
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(expandButtonIconStorke);
        paint.setColor(expandButtonIconColor);

        rightButtonIconCenter  = new Point();
        leftButtonIconCenter = new Point();

        expandMenuAnimtion = new ExpandMenuAnimation();
        expandMenuAnimtion.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                 isAnimEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureSize((int) defaultWidth,widthMeasureSpec);
        int height = measureSize((int) defaultHeight,heightMeasureSpec);
        viewWidth = width;
        viewHeight = height;
        viewBolderCornerRadius = Math.min(width,height) / 2;
        buttonRadius = viewBolderCornerRadius;
        layoutRootButton();
        setMeasuredDimension(width,height);
        maxBackPathWidth = width - buttonRadius * 2;
        backPathWidth = maxBackPathWidth;
        if(getBackground() == null) setMenuBackground();
    }
    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }
    /**
     * 测量按钮中点和矩形位置
     */
    private void layoutRootButton(){
        buttonTop = 0;
        buttonBottom = viewHeight;

        rightButtonIconCenter.x = (int) (viewWidth- buttonRadius);
        rightButtonIconCenter.y = (int) (viewHeight/2);
        rightButtonIconLeft = rightButtonIconCenter.x- buttonRadius;
        rightButtonIconRight = rightButtonIconCenter.x+ buttonRadius;

        leftButtonIconCenter.x = buttonRadius;
        leftButtonIconCenter.y = (int) (viewHeight/2);
        leftButtonIconLeft = leftButtonIconCenter.x- buttonRadius;
        leftButtonIconRight = leftButtonIconCenter.x+ buttonRadius;
    }
    /**
     * 设置菜单背景，如果要显示阴影，需在onLayout之前调用
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setMenuBackground(){
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(viewBackgroudColor);
        gd.setStroke((int)viewBolderWidth, viewBolderColor);
        gd.setCornerRadius(viewBolderCornerRadius);
        setBackground(gd);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(isFirstLayout){
            viewLeft = getLeft();
            viewRight = getRight();
            isFirstLayout = false;
        }
        if(getChildCount() == 1){
            childView = getChildAt(0);
            if(isExpand){
                switch (expandButtonStyle){
                    case ExpandButtonLeft:
                        childView.layout(leftButtonIconCenter.x,(int)buttonTop,(int)rightButtonIconRight,(int)buttonBottom);
                        break;
                    case ExpandButtonRight:
                        childView.layout(rightButtonIconCenter.x,(int)buttonTop,(int)leftButtonIconLeft,(int)buttonBottom);
                        break;
                }
                //限制子View在菜单内，LayoutParam类型和当前ViewGroup一致
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)viewWidth,(int)viewHeight);
                layoutParams.setMargins(0,0,buttonRadius *3,0);
                childView.setLayoutParams(layoutParams);
            }else{
                childView.setVisibility(GONE);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;//当menu的宽度改变时，重新给viewWidth赋值
        if(isAnimEnd){//防止出现动画结束后菜单栏位置大小测量错误的bug
            if(expandButtonStyle == ExpandButtonRight){
                if(!isExpand){
//                    layout((int)(menuRight - buttonRadius *2-backPathWidth),getTop(), menuRight,getBottom());
                    layout(((int)viewRight - buttonRadius *2),getTop(), (int)viewRight,getBottom());
                }
            }else {
                if(!isExpand){
//                    layout(menuLeft,getTop(),(int)(menuLeft + buttonRadius *2+backPathWidth),getBottom());
                    layout((int)viewLeft,getTop(),((int)viewLeft + buttonRadius *2),getBottom());
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        layoutRootButton();
        if(expandButtonStyle == ExpandButtonRight){
            drawRightIcon(canvas);
        }else {
            drawLeftIcon(canvas);
        }

        super.onDraw(canvas);//注意父方法在最后调用，以免icon被遮盖
    }

    private void drawLeftIcon(Canvas canvas) {
        path.reset();
        path.moveTo(leftButtonIconCenter.x- expandButtonIconSize, leftButtonIconCenter.y);
        path.lineTo(leftButtonIconCenter.x+ expandButtonIconSize, leftButtonIconCenter.y);
        canvas.drawPath(path, paint);//划横线

        canvas.save();
        canvas.rotate(expandButtonRotateDegrees, leftButtonIconCenter.x, leftButtonIconCenter.y);//旋转画布，让竖线可以随角度旋转
        path.reset();
        path.moveTo(leftButtonIconCenter.x, leftButtonIconCenter.y- expandButtonIconSize);
        path.lineTo(leftButtonIconCenter.x, leftButtonIconCenter.y+ expandButtonIconSize);
        canvas.drawPath(path, paint);//画竖线
        canvas.restore();
    }

    private void drawRightIcon(Canvas canvas) {
        path.reset();
        path.moveTo(rightButtonIconCenter.x- expandButtonIconSize, rightButtonIconCenter.y);
        path.lineTo(rightButtonIconCenter.x+ expandButtonIconSize, rightButtonIconCenter.y);
        canvas.drawPath(path, paint);//划横线

        canvas.save();
        canvas.rotate(expandButtonRotateDegrees, rightButtonIconCenter.x, rightButtonIconCenter.y);//旋转画布，让竖线可以随角度旋转
        path.reset();
        path.moveTo(rightButtonIconCenter.x, rightButtonIconCenter.y- expandButtonIconSize);
        path.lineTo(rightButtonIconCenter.x, rightButtonIconCenter.y+ expandButtonIconSize);
        canvas.drawPath(path, paint);//画竖线
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if(backPathWidth==maxBackPathWidth || backPathWidth==0){//动画结束时按钮才生效
                    switch (expandButtonStyle){
                        case ExpandButtonRight:
                            if(x==downX&&y==downY&&y>=buttonTop&&y<=buttonBottom&&x>=rightButtonIconLeft&&x<=rightButtonIconRight){
                                expandMenu(expandAnimTime);
                            }
                            break;
                        case ExpandButtonLeft:
                            if(x==downX&&y==downY&&y>=buttonTop&&y<=buttonBottom&&x>=leftButtonIconLeft&&x<=leftButtonIconRight){
                                expandMenu(expandAnimTime);
                            }
                            break;
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 展开收起菜单
     * @param time 动画时间
     */
    private void expandMenu(int time){
        expandMenuAnimtion.setDuration(time);
        isExpand = isExpand ?false:true;
        this.startAnimation(expandMenuAnimtion);
        isAnimEnd = false;
    }
    class ExpandMenuAnimation extends Animation {
       @Override
       protected void applyTransformation(float interpolatedTime, Transformation t) {
           super.applyTransformation(interpolatedTime, t);

           float left = viewRight - buttonRadius *2;//按钮在右边，菜单收起时按钮区域left值
           float right = viewLeft + buttonRadius *2;//按钮在左边，菜单收起时按钮区域right值
           if(childView!=null) {
               childView.setVisibility(GONE);
           }
           if(isExpand){//打开菜单
               backPathWidth = maxBackPathWidth * interpolatedTime;
               expandButtonRotateDegrees = (int) (90 * interpolatedTime);

               if(backPathWidth==maxBackPathWidth){
                   if(childView!=null) {
                       childView.setVisibility(VISIBLE);
                   }
               }
           }else {//关闭菜单
               backPathWidth = maxBackPathWidth - maxBackPathWidth * interpolatedTime;
               expandButtonRotateDegrees = (int) (90 - 90 * interpolatedTime);
           }
           if(expandButtonStyle == ExpandButtonRight){
               layout((int)(left-backPathWidth),getTop(), (int)viewRight,getBottom());//会调用onLayout重新测量子View位置
           }else {
               layout((int)viewLeft,getTop(),(int)(right+backPathWidth),getBottom());
           }
           postInvalidate();
       }
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
