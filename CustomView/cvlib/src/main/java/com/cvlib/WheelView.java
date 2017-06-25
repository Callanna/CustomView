package com.cvlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.callanna.viewlibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2017/1/31.
 */

public class WheelView extends View {
    /**
     * View the width
     */
    private float controlWidth;
    /**
     * View the Height
     */
    private float controlHeight;
    /**
     * isScrolling
     */
    private boolean isScrolling = false;
    /**
     * itemList
     */
    private ArrayList<ItemObject> itemList = new ArrayList<>();
    /**
     * data Lenght
     */
    private int dataLen = 0;
    /**
     * data List
     */
    private ArrayList<String> dataList = new ArrayList<>();
    /**
     * event action down y
     */
    private int downY;
    /**
     * event action down  time
     */
    private long downTime = 0;
    /**
     * move time
     */
    private long goonTime = 200;
    /**
     * move distance
     */
    private int goonDistance = 100;
    /**
     * line Paint
     */
    private Paint linePaint;
    /**
     * line Color
     */
    private int lineColor = 0xff000000;
    /**
     * line Height
     */
    private float lineHeight = 2f;
    /**
     * normal Font
     */
    private float normalFont = 14.0f;
    /**
     * selected Font
     */
    private float selectedFont = 22.0f;
    /**
     * unit Height
     */
    private int unitHeight = 50;
    /**
     * item Number
     */
    private int itemNumber = 7;
    /**
     * normal Color
     */
    private int normalColor = 0xff000000;
    /**
     * selected Color
     */
    private int selectedColor = 0xffff0000;
    /**
     * maskHeight
     */
    private float maskHeight = 48.0f;
    /**
     * onSelectListener
     */
    private OnSelectListener onSelectListener;
    /**
     * isEnable
     */
    private boolean isEnable = true;
    /**
     * final data to Refresh the interface
     */
    private static final int REFRESH_VIEW = 0x001;
    /**
     *final data to move distance
     */
    private static final int MOVE_NUMBER = 5;
    /**
     * final data to is Empty
     */
    private boolean noEmpty = true;

    /**
     * Is modifying the data，Avoid ConcurrentModificationException Exception
     */
    private boolean isClearing = false;

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        initData();
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        initData();
    }

    public WheelView(Context context) {
        super(context);
        initData();
    }

    /**
     * Initialization, access to set of properties
     *
     * @param context context
     * @param attrs AttributeSet
     */
    private void init(Context context, AttributeSet attrs) {

        TypedArray attribute = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        unitHeight = (int) attribute.getDimension(R.styleable.WheelView_unitHeight, unitHeight);
        itemNumber = attribute.getInt(R.styleable.WheelView_itemNumber, itemNumber);

        normalFont = attribute.getDimension(R.styleable.WheelView_normalTextSize, normalFont);
        selectedFont = attribute.getDimension(R.styleable.WheelView_selectedTextSize, selectedFont);
        normalColor = attribute.getColor(R.styleable.WheelView_normalTextColor, normalColor);
        selectedColor = attribute.getColor(R.styleable.WheelView_selectedTextColor, selectedColor);

        lineColor = attribute.getColor(R.styleable.WheelView_lineColor, lineColor);
        lineHeight = attribute.getDimension(R.styleable.WheelView_lineHeight, lineHeight);

        maskHeight = attribute.getDimension(R.styleable.WheelView_maskHeight, maskHeight);
        noEmpty = attribute.getBoolean(R.styleable.WheelView_noEmpty, true);
        isEnable = attribute.getBoolean(R.styleable.WheelView_isEnable, true);

        attribute.recycle();

        controlHeight = itemNumber * unitHeight;
    }

    public void setUnitHeight(int unitHeight) {
        this.unitHeight = unitHeight;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public void setSelectedFont(float selectedFont) {
        this.selectedFont = selectedFont;
    }

    public void setNormalFont(float normalFont) {
        this.normalFont = normalFont;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void setMaskHeight(float maskHeight) {
        this.maskHeight = maskHeight;
    }



    /**
     * init Data
     */
    private void initData() {
        isClearing = true;
        itemList.clear();
        dataLen = dataList.size();
        for (int i = 0; i < dataList.size(); i++) {
            ItemObject itemObject = new ItemObject();
            itemObject.id = i;
            itemObject.itemText = dataList.get(i);
            itemObject.x = 0;
            itemObject.y = i * unitHeight;
            itemList.add(itemObject);
        }
        isClearing = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        controlWidth = getMeasuredWidth();
        controlHeight = itemNumber * unitHeight;
        if (controlWidth != 0) {
            setMeasuredDimension(getMeasuredWidth(), itemNumber * unitHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawLine(canvas);
        drawList(canvas);
        drawMask(canvas);
    }

    /**
     * draw Line
     *
     * @param canvas  Canvas
     */
    private void drawLine(Canvas canvas) {

        if (linePaint == null) {
            linePaint = new Paint();
            linePaint.setColor(lineColor);
            linePaint.setAntiAlias(true);
            linePaint.setStrokeWidth(lineHeight);
        }

        canvas.drawLine(0, controlHeight / 2 - unitHeight / 2 + lineHeight,
                controlWidth, controlHeight / 2 - unitHeight / 2 + lineHeight, linePaint);
        canvas.drawLine(0, controlHeight / 2 + unitHeight / 2 - lineHeight,
                controlWidth, controlHeight / 2 + unitHeight / 2 - lineHeight, linePaint);
    }

    private synchronized void drawList(Canvas canvas) {
        if (isClearing)
            return;
        try {
            for (ItemObject itemObject : itemList) {
                itemObject.drawSelf(canvas, getMeasuredWidth());
            }
        } catch (Exception e) {
        }
    }

    /**
     * Draw the cover plate
     *
     * @param canvas Canvas
     */
    private void drawMask(Canvas canvas) {
        LinearGradient lg = new LinearGradient(0, 0, 0, maskHeight, 0x00f2f2f2,
                0x00f2f2f2, Shader.TileMode.MIRROR);
        Paint paint = new Paint();
        paint.setShader(lg);
        canvas.drawRect(0, 0, controlWidth, maskHeight, paint);

        LinearGradient lg2 = new LinearGradient(0, controlHeight - maskHeight,
                0, controlHeight, 0x00f2f2f2, 0x00f2f2f2, Shader.TileMode.MIRROR);
        Paint paint2 = new Paint();
        paint2.setShader(lg2);
        canvas.drawRect(0, controlHeight - maskHeight, controlWidth,
                controlHeight, paint2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnable)
            return true;
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isScrolling = true;
                downY = (int) event.getY();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(y - downY);
                onSelectListener();
                break;
            case MotionEvent.ACTION_UP:
                int move = Math.abs(y - downY);
                // This paragraph of time moving distance
                if (System.currentTimeMillis() - downTime < goonTime && move > goonDistance) {
                    goonMove(y - downY);
                } else {
                    actionUp(y - downY);
                    noEmpty();
                    isScrolling = false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Continue to move a distance
     */
    private synchronized void goonMove(final int move) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                int distance = 0;
                while (distance < unitHeight * MOVE_NUMBER) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    actionThreadMove(move > 0 ? distance : distance * (-1));
                    distance += 10;

                }
                actionUp(move > 0 ? distance - 10 : distance * (-1) + 10);
                noEmpty();
            }
        }).start();
    }

    /**
     * Can't be empty, there must be options
     */
    private void noEmpty() {
        if (!noEmpty)
            return;
        if(itemList.size()<=0)
            return;
        for (ItemObject item : itemList) {
            if (item.isSelected())
                return;
        }
        int move = (int) itemList.get(0).moveToSelected();
        if (move < 0) {
            defaultMove(move);
        } else {
            defaultMove((int) itemList.get(itemList.size() - 1)
                    .moveToSelected());
        }
        for (ItemObject item : itemList) {
            if (item.isSelected()) {
                if (onSelectListener != null)
                    onSelectListener.endSelect(item.id, item.itemText);
                break;
            }
        }
    }

    /**
     * When the Move
     *
     * @param move
     */
    private void actionMove(int move) {
        for (ItemObject item : itemList) {
            item.move(move);
            Log.d("duanyl","y: "+item.y);
        }
        invalidate();
    }

    /**
     * start Thread   when Move
     *
     * @param move
     */
    private void actionThreadMove(int move) {
        for (ItemObject item : itemList) {
            item.move(move);
        }
        Message rMessage = new Message();
        rMessage.what = REFRESH_VIEW;
        handler.sendMessage(rMessage);
    }

    /**
     * When Loosen
     *
     * @param move
     */
    private void actionUp(int move) {
        int newMove = 0;
        if (move > 0) {
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).isSelected()) {
                    newMove = (int) itemList.get(i).moveToSelected();
                    if (onSelectListener != null)
                        onSelectListener.endSelect(itemList.get(i).id,
                                itemList.get(i).itemText);
                    break;
                }
            }
        } else {
            for (int i = itemList.size() - 1; i >= 0; i--) {
                if (itemList.get(i).isSelected()) {
                    newMove = (int) itemList.get(i).moveToSelected();
                    if (onSelectListener != null)
                        onSelectListener.endSelect(itemList.get(i).id,
                                itemList.get(i).itemText);
                    break;
                }
            }
        }
        for (ItemObject item : itemList) {
            item.newY(move + 0);
        }
        slowMove(newMove);
        Message rMessage = new Message();
        rMessage.what = REFRESH_VIEW;
        handler.sendMessage(rMessage);

    }

    /**
     * snail
     *
     * @param move index
     */
    private synchronized void slowMove(final int move) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Determine the positive and negative
                int m = move > 0 ? move : move * (-1);
                int i = move > 0 ? 1 : (-1);
                // movement speed
                int speed = 1;
                while (true) {
                    m = m - speed;
                    if (m <= 0) {
                        for (ItemObject item : itemList) {
                            item.newY(m * i);
                        }
                        Message rMessage = new Message();
                        rMessage.what = REFRESH_VIEW;
                        handler.sendMessage(rMessage);
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    for (ItemObject item : itemList) {
                        item.newY(speed * i);
                    }
                    Message rMessage = new Message();
                    rMessage.what = REFRESH_VIEW;
                    handler.sendMessage(rMessage);
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (ItemObject item : itemList) {
                    if (item.isSelected()) {
                        if (onSelectListener != null)
                            onSelectListener.endSelect(item.id, item.itemText);
                        break;
                    }
                }

            }
        }).start();
    }

    /**
     * Move to the default location
     *
     * @param move
     */
    private synchronized void defaultMove(int move) {
        for (ItemObject item : itemList) {
            item.newY(move);
        }
        Message rMessage = new Message();
        rMessage.what = REFRESH_VIEW;
        handler.sendMessage(rMessage);
    }

    /**
     * Sliding to monitor
     */
    private void onSelectListener() {
        if (onSelectListener == null)
            return;
        for (ItemObject item : itemList) {
            if (item.isSelected()) {
                onSelectListener.selecting(item.id, item.itemText);
            }
        }
    }

    /**
     * Set the data (the first)
     *
     * @param data data
     */
    public void setData(List<String> data) {
        this.dataList.clear();
        this.dataList.addAll(data);
        initData();
    }

    public void clearData(){
        this.dataList.clear();
        initData();
        postInvalidate();
    }

    /**
     * Reset the data
     *
     * @param data data
     */
    public void refreshData(ArrayList<String> data) {
        setData(data);
        invalidate();
    }

    /**
     * Get return items
     *
     * @return select item
     */
    public int getSelected() {
        for (ItemObject item : itemList) {
            if (item.isSelected())
                return item.id;
        }
        return -1;
    }

    /**
     * Access to the content
     *
     * @return
     */
    public String getSelectedText() {
        for (ItemObject item : itemList) {
            if (item.isSelected())
                return item.itemText;
        }
        return "";
    }

    /**
     * Whether is sliding
     *
     * @return
     */
    public boolean isScrolling() {
        return isScrolling;
    }

    /**
     * is Enable
     *
     * @return
     */
    public boolean isEnable() {
        return isEnable;
    }

    /**
     * Settings is Enable
     *
     * @param isEnable
     */
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * Set the default option
     *
     * @param index index
     */
    public void setDefault(int index) {
        if (index > itemList.size() - 1)
            return;
        float move = itemList.get(index).moveToSelected();
        defaultMove((int) move);
    }

    /**
     * Set the default option
     *
     * @param text text
     */
    public void setDefault(String text) {
        int index = 0;
        if(itemList.size() <= 0){
            return;
        }
        if(!text.equals("")) {
            for (int i = 0; i < dataLen; i++) {
                if (text.contains(itemList.get(i).itemText)) {
                    index = i;
                    break;
                }
            }
        }
        float move = itemList.get(index).moveToSelected();
        defaultMove((int) move);
    }

    /**
     * Access list size
     *
     * @return
     */
    public int getListSize() {
        if (itemList == null)
            return 0;
        return itemList.size();
    }

    /**
     * Get a content
     *
     * @param index index
     * @return String
     */
    public String getItemText(int index) {
        if (itemList == null)
            return "";
        return itemList.get(index).itemText;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_VIEW:
                    invalidate();
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * Item Object
     *
     * @author JiangPing
     */
    private class ItemObject {
        /**
         * id
         */
        public int id = 0;
        /**
         * content
         */
        public String itemText = "";
        /**
         * x
         */
        public int x = 0;
        /**
         * y
         */
        public int y = 0;
        /**
         * displacement distance
         */
        public int move = 0;
        /**
         * Brush fonts
         */
        private TextPaint textPaint;
        /**
         * Font range rectangle
         */
        private Rect textRect;

        public ItemObject() {
            super();
        }

        /**
         * Draw their own
         *
         * @param canvas         canvas
         * @param containerWidth container Width
         */
        public void drawSelf(Canvas canvas, int containerWidth) {

            if (textPaint == null) {
                textPaint = new TextPaint();
                textPaint.setAntiAlias(true);
            }

            if (textRect == null)
                textRect = new Rect();

            //  if selected
            if (isSelected()) {
                textPaint.setColor(selectedColor);
                // Distance from the center of the standard for distance
                float moveToSelect = moveToSelected();
                moveToSelect = moveToSelect > 0 ? moveToSelect : moveToSelect * (-1);
                // Calculate the current font size
                float textSize = normalFont
                        + ((selectedFont - normalFont) * (1.0f - moveToSelect / (float) unitHeight));
                textPaint.setTextSize(textSize);
            } else {
                textPaint.setColor(normalColor);
                textPaint.setTextSize(normalFont);
            }

            // The smallest surrounded the entire string a the Rect area
            String itemtext = (String) TextUtils.ellipsize(itemText, textPaint, containerWidth, TextUtils.TruncateAt.END);
            textPaint.getTextBounds(itemtext, 0, itemtext.length(), textRect);
            // Judge whether the visual
            if (!isInView())
                return;

            // Draw the content
            canvas.drawText(itemtext, x + controlWidth / 2 - textRect.width() / 2,
                    y + move + unitHeight / 2 + textRect.height() / 2, textPaint);
            itemtext = null;
        }

        /**
         * Whether in the visual interface
         *
         * @return
         */
        public boolean isInView() {
            if (y + move > controlHeight || (y + move + unitHeight / 2 + textRect.height() / 2) < 0)
                return false;
            return true;
        }

        /**
         * displacement distance
         *
         * @param _move
         */
        public void move(int _move) {
            this.move = _move;
        }

        /**
         * Set the new coordinates

         *
         * @param _move
         */
        public void newY(int _move) {
            this.move = 0;
            this.y = y + _move;
        }

        /**
         * Determine whether within the selected area
         *
         * @return
         */
        public boolean isSelected() {
            if ((y + move) >= controlHeight / 2 - unitHeight / 2 + lineHeight
                    && (y + move) <= controlHeight / 2 + unitHeight / 2 - lineHeight) {
                return true;
            }
            if ((y + move + unitHeight) >= controlHeight / 2 - unitHeight / 2 + lineHeight
                    && (y + move + unitHeight) <= controlHeight / 2 + unitHeight / 2 - lineHeight) {
                return true;
            }
            if ((y + move) <= controlHeight / 2 - unitHeight / 2 + lineHeight
                    && (y + move + unitHeight) >= controlHeight / 2 + unitHeight / 2 - lineHeight) {
                return true;
            }
            return false;
        }

        /**
         * The move to the standard position need distance
         */
        public float moveToSelected() {
            return (controlHeight / 2 - unitHeight / 2) - (y + move);
        }
    }

    /**
     * Choose listening

     *
     * @author JiangPing
     */
    public interface OnSelectListener {
        /**
         * End of choice
         *
         * @param id
         * @param text
         */
        void endSelect(int id, String text);

        /**
         * The content of the selected
         *
         * @param id
         * @param text
         */
        void selecting(int id, String text);

    }
}
