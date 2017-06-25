package com.cvlib.progress;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Callanna on 2017/6/25.
 */

public class PSeekBarBuilder  {
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
    @PSeekBar.PSeekBarMode
    private int mPSMode;

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
    private int mArcFullDegree;
    private boolean isToChangeColor;
    
    private PSeekBar pSeekBar;
    
    public PSeekBarBuilder(PSeekBar pSeekBar){
        this.pSeekBar = pSeekBar;
    }
    
    public void build(){
        this.pSeekBar.config(this);
    }

    public int getmTrackHeight() {
        return mTrackHeight;
    }

    public PSeekBarBuilder setmTrackHeight(int mTrackHeight) {
        this.mTrackHeight = dpTopx( mTrackHeight);
        return this;
    }

    public int getmTrackColor() {
        return mTrackColor;
    }

    public PSeekBarBuilder setmTrackColor(int mTrackColor) {
        this.mTrackColor = mTrackColor;
        return this;
    }

    public int getmProgressHeight() {
        return mProgressHeight;
    }

    public PSeekBarBuilder setmProgressHeight(int mProgressHeight) {
        this.mProgressHeight = dpTopx( mProgressHeight);
        return this;
    }

    public int getmProgressColor() {
        return mProgressColor;
    }

    public PSeekBarBuilder setmProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
        return this;
    }

    public int getmThumbSize() {
        return mThumbSize;
    }

    public PSeekBarBuilder setmThumbSize(int mThumbSize) {
        this.mThumbSize = dpTopx( mThumbSize);
        return this;
    }

    public int getmThumbColor() {
        return mThumbColor;
    }

    public PSeekBarBuilder setmThumbColor(int mThumbColor) {
        this.mThumbColor = mThumbColor;
        return this;
    }

    public int getmThumbDotSize() {
        return mThumbDotSize;
    }

    public PSeekBarBuilder setmThumbDotSize(int mThumbDotSize) {
        this.mThumbDotSize = dpTopx( mThumbDotSize);
        return this;
    }

    public int getmThumbDotColor() {
        return mThumbDotColor;
    }

    public PSeekBarBuilder setmThumbDotColor(int mThumbDotColor) {
        this.mThumbDotColor = mThumbDotColor;
        return this;
    }

    public int getmMax() {
        return mMax;
    }

    public PSeekBarBuilder setmMax(int mMax) {
        this.mMax = mMax;
        return this;
    }

    public int getmMin() {
        return mMin;
    }

    public PSeekBarBuilder setmMin(int mMin) {
        this.mMin = mMin;
        return this;
    }


    public @PSeekBar.PSeekBarMode int getmPSMode() {
        return mPSMode;
    }

    public PSeekBarBuilder setmPSMode(@PSeekBar.PSeekBarMode int mPSMode) {
        this.mPSMode = mPSMode;
        return this;
    }

    public boolean isRound() {
        return isRound;
    }

    public PSeekBarBuilder setRound(boolean round) {
        isRound = round;
        return this;
    }

    public int getRadiusSize() {
        return radiusSize;
    }

    public PSeekBarBuilder setRadiusSize(int radiusSize) {
        this.radiusSize = radiusSize;
        return this;
    }

    public boolean isHasBubble() {
        return isHasBubble;
    }

    public PSeekBarBuilder setHasBubble(boolean hasBubble) {
        isHasBubble = hasBubble;
        return this;
    }

    public int getBubbleTextSize() {
        return bubbleTextSize;
    }

    public PSeekBarBuilder setBubbleTextSize(int bubbleTextSize) {
        this.bubbleTextSize = spTopx( bubbleTextSize);
        return this;
    }

    public int getBubbleTextColor() {
        return bubbleTextColor;
    }

    public PSeekBarBuilder setBubbleTextColor(int bubbleTextColor) {
        this.bubbleTextColor = bubbleTextColor;
        return this;
    }

    public int getmBubbleColor() {
        return mBubbleColor;
    }

    public PSeekBarBuilder setmBubbleColor(int mBubbleColor) {
        this.mBubbleColor = mBubbleColor;
        return this;
    }

    public int getmBubbleDistance() {
        return mBubbleDistance;
    }

    public PSeekBarBuilder setmBubbleDistance(int mBubbleDistance) {
        this.mBubbleDistance = mBubbleDistance;
        return this;
    }

    public boolean isClickToDrag() {
        return isClickToDrag;
    }

    public PSeekBarBuilder setClickToDrag(boolean clickToDrag) {
        isClickToDrag = clickToDrag;
        return this;
    }

    public boolean isLineTrack() {
        return isLineTrack;
    }

    public PSeekBarBuilder setLineTrack(boolean lineTrack) {
        isLineTrack = lineTrack;
        return this;
    }

    public int getmLineTrackSize() {
        return mLineTrackSize;
    }

    public PSeekBarBuilder setmLineTrackSize(int mLineTrackSize) {
        this.mLineTrackSize = spTopx( mLineTrackSize);
        return this;
    }

    public boolean isColorGradient() {
        return isColorGradient;
    }

    public PSeekBarBuilder setColorGradient(boolean colorGradient) {
        isColorGradient = colorGradient;
        return this;
    }

    public int getmStartColor() {
        return mStartColor;
    }

    public PSeekBarBuilder setmStartColor(int mStartColor) {
        this.mStartColor = mStartColor;
        return this;
    }

    public int getmCenterColor() {
        return mCenterColor;
    }

    public PSeekBarBuilder setmCenterColor(int mCenterColor) {
        this.mCenterColor = mCenterColor;
        return this;
    }

    public int getmEndColor() {
        return mEndColor;
    }

    public PSeekBarBuilder setmEndColor(int mEndColor) {
        this.mEndColor = mEndColor;
        return this;
    }

    public boolean isHasTextEnd() {
        return isHasTextEnd;
    }

    public PSeekBarBuilder setHasTextEnd(boolean hasTextEnd) {
        isHasTextEnd = hasTextEnd;
        return this;
    }

    public int getmArcFullDegree() {
        return mArcFullDegree;
    }

    public PSeekBarBuilder setmArcFullDegree(int mArcFullDegree) {
        this.mArcFullDegree = mArcFullDegree;
        return this;
    }

    public boolean isToChangeColor() {
        return isToChangeColor;
    }

    public PSeekBarBuilder setToChangeColor(boolean toChangeColor) {
        isToChangeColor = toChangeColor;
        return this;
    }

    public static int dpTopx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, Resources.getSystem().getDisplayMetrics());
    }
    public static int spTopx(int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp, Resources.getSystem().getDisplayMetrics());
    }
}
