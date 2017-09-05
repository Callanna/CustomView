package com.cvlib.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.callanna.viewlibrary.R;

/**
 * Created by Callanna on 2017/8/23.
 */

public class PercentRelativeLayout extends RelativeLayout {
    public PercentRelativeLayout(Context context) {
        super(context);
    }

    public PercentRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PercentRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 重写测量方法
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 先拿到父控件的宽高
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {// 循环迭代子控件
            View child = this.getChildAt(i);// 取出每一个子控件
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            float widthPercent = 0;
            float hightPercent = 0;
            if (lp instanceof PercentRelativeLayout.LayoutParams) {// 支持百分比布局
                widthPercent = ((PercentRelativeLayout.LayoutParams) lp).widthPercent;
                hightPercent = ((PercentRelativeLayout.LayoutParams) lp).heightPercent;
            }
            if (widthPercent != 0) {
                // 父容器的宽*宽的百分比
                lp.width = (int) (width * widthPercent);
            }

            if (hightPercent != 0) {
                // 父容器的高*高的百分比
                lp.height = (int) (height * hightPercent);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 重写对子控件布局方法
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 重写对子控件布局属性进行获取解析
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
//      return super.generateLayoutParams(attrs);// 这里必须返回下面自定的LayoutParams
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams{

        private float widthPercent;
        private float heightPercent;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs,R.styleable.PrecentRelativeLayout);
            widthPercent = a.getFloat(R.styleable.PrecentRelativeLayout_layout_widthPrecent, widthPercent);
            heightPercent = a.getFloat(R.styleable.PrecentRelativeLayout_layout_heightPrecent, heightPercent);
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(android.widget.RelativeLayout.LayoutParams source) {
            super(source);
        }

    }
}
