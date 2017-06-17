package com.cvlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.callanna.viewlibrary.R;

/**
 * Description
 * Created by Callanna on 2017/5/11.
 */
public class SwitchButton extends android.support.v7.widget.AppCompatTextView implements Checkable {
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private final StateListDrawable stateListDrawable;
    private boolean mChecked;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        stateListDrawable = (StateListDrawable) a.getDrawable(R.styleable.SwitchButton_statusSrc);
        mChecked = a.getBoolean(R.styleable.SwitchButton_statusChecked, false);
        setChecked(mChecked);
        a.recycle();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (stateListDrawable != null) {
            int[] myDrawableState = getDrawableState();
            stateListDrawable.setState(myDrawableState);
            Drawable drawable = stateListDrawable.getCurrent();
//            setImageDrawable(drawable);
            setBackground(drawable);
        }
    }
}