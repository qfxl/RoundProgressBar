/*
 * MIT License
 *
 * Copyright (c) 2018 Frank Xu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.qfxl.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * roundProgressBar
 *
 * @author qfxl
 */

public class RoundProgressBar extends View {
    /**
     * arcPaint
     */
    private Paint arcPaint;
    /**
     * arcRect
     */
    private RectF arcRect;
    /**
     * textPaint
     */
    private Paint textPaint;
    /**
     * arc StrokeWidth
     */
    private int strokeWidth;
    /**
     * countDown Arc StrokeColor
     */
    private int strokeColor;
    /**
     * progress
     */
    private int progress;
    /**
     * countDown millis default is 3000ms
     */
    private int countDownTimeMillis;
    /**
     * center background paint
     */
    private Paint centerBgPaint;
    /**
     * center background
     */
    private int centerBackground;
    /**
     * center text
     */
    private String centerText;
    /**
     * placeHolder if there is none text
     */
    private String emptyText = "100%";
    /**
     * center textColor
     */
    private int centerTextColor;
    /**
     * center textSize
     */
    private float centerTextSize;
    /**
     * measure text bounds
     */
    private Rect textBounds;
    /**
     * arc start angle default is -90
     */
    private int startAngle;
    /**
     * if is auto start,default is true
     */
    private boolean isAutoStart;
    /**
     * progress change listener
     */
    private ProgressChangeListener mProgressChangeListener;
    /**
     * arc sweep direction default is forward
     */
    private Direction mDirection;
    /**
     * direction index
     */
    private int directionIndex;
    private final Direction[] mDirections = {
            Direction.FORWARD,
            Direction.REVERSE
    };

    public enum Direction {
        FORWARD(0),
        REVERSE(1);

        Direction(int ni) {
            nativeInt = ni;
        }

        final int nativeInt;
    }

    /**
     * value animator
     */
    private ValueAnimator animator;
    /**
     * if true draw outsideWrapper false otherwise
     */
    private boolean shouldDrawOutsideWrapper;
    /**
     * outsideWrapper color
     */
    private int outsideWrapperColor;
    /**
     * default space between view to bound
     */
    private int defaultSpace;
    /**
     * isSupport end to start, if true progress = progress - 360.
     */
    private boolean isSupportEts;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        strokeWidth = a.getDimensionPixelSize(R.styleable.RoundProgressBar_rpb_sweepStrokeWidth, 5);
        strokeColor = a.getColor(R.styleable.RoundProgressBar_rpb_sweepStrokeColor, Color.BLACK);
        startAngle = a.getInteger(R.styleable.RoundProgressBar_rpb_sweepStartAngle, -90);
        centerText = a.getString(R.styleable.RoundProgressBar_rpb_centerText);
        centerTextSize = a.getDimension(R.styleable.RoundProgressBar_rpb_centerTextSize, sp2px(12));
        centerTextColor = a.getColor(R.styleable.RoundProgressBar_rpb_centerTextColor, Color.BLACK);
        centerBackground = a.getColor(R.styleable.RoundProgressBar_rpb_centerBackgroundColor, Color.GRAY);
        countDownTimeMillis = a.getInteger(R.styleable.RoundProgressBar_rpb_countDownTimeInMillis, 3 * 1000);
        directionIndex = a.getInt(R.styleable.RoundProgressBar_rpb_progressDirection, 0);
        isAutoStart = a.getBoolean(R.styleable.RoundProgressBar_rpb_autoStart, true);
        shouldDrawOutsideWrapper = a.getBoolean(R.styleable.RoundProgressBar_rpb_drawOutsideWrapper, false);
        outsideWrapperColor = a.getColor(R.styleable.RoundProgressBar_rpb_outsideWrapperColor, Color.GRAY);
        isSupportEts = a.getBoolean(R.styleable.RoundProgressBar_rpb_supportEndToStart, false);
        a.recycle();
        defaultSpace = strokeWidth;
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(centerTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        centerBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        centerBgPaint.setStyle(Paint.Style.FILL);

        arcRect = new RectF();
        textBounds = new Rect();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int minWidth = getMinWidth(widthMode, widthSize);
        int minHeight = getMinHeight(heightMode, heightSize);
        if (minWidth != minHeight) {
            minWidth = Math.max(minWidth, minHeight);
        }
        setMeasuredDimension(minWidth, minWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        arcRect.left = defaultSpace;
        arcRect.top = defaultSpace;
        arcRect.right = w - defaultSpace;
        arcRect.bottom = h - defaultSpace;
    }

    /**
     * getMinWidth textHeight + paddingLeft + paddingRight + arcStrokeWidth * 2
     *
     * @param mode         mode
     * @param measuredSize measuredSize
     * @return minWidth
     */
    private int getMinWidth(int mode, int measuredSize) {
        int suggestSize = 0;
        switch (mode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if (TextUtils.isEmpty(centerText)) {
                    textPaint.getTextBounds(emptyText, 0, emptyText.length(), textBounds);
                } else {
                    textPaint.getTextBounds(centerText, 0, centerText.length(), textBounds);
                }
                suggestSize = getPaddingLeft() + getPaddingRight() + textBounds.width() + defaultSpace;
                break;
            case MeasureSpec.EXACTLY:
                suggestSize = measuredSize;
                break;
            default:
        }
        return suggestSize;
    }

    /**
     * getMinHeight similar to {@link #getMinWidth(int, int)}.
     *
     * @param mode         mode
     * @param measuredSize measuredSize
     * @return minHeight
     */
    private int getMinHeight(int mode, int measuredSize) {
        int suggestSize = 0;
        switch (mode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                if (TextUtils.isEmpty(centerText)) {
                    textPaint.getTextBounds(emptyText, 0, emptyText.length(), textBounds);
                } else {
                    textPaint.getTextBounds(centerText, 0, centerText.length(), textBounds);
                }
                suggestSize = getPaddingTop() + getPaddingBottom() + textBounds.height() + defaultSpace;
                break;
            case MeasureSpec.EXACTLY:
                suggestSize = measuredSize;
                break;
            default:
        }
        return suggestSize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //draw center background circle
        centerBgPaint.setColor(centerBackground);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() - defaultSpace * 2) / 2, centerBgPaint);

        //draw outside arc wrapper
        if (shouldDrawOutsideWrapper) {
            arcPaint.setColor(outsideWrapperColor);
            canvas.drawArc(arcRect, 0, 360, false, arcPaint);
        }

        //draw outside arc
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setColor(strokeColor);
        canvas.drawArc(arcRect, startAngle, isSupportEts ? progress - 360 : progress, false, arcPaint);

        //draw text
        textPaint.setColor(centerTextColor);
        if (TextUtils.isEmpty(centerText)) {
            canvas.drawText(Math.abs((int) (progress / 3.6)) + "%", arcRect.centerX(), arcRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
        } else {
            canvas.drawText(centerText, arcRect.centerX(), arcRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2, textPaint);
        }
    }

    /**
     * start
     */
    public void start() {
        initAnimator(countDownTimeMillis, mDirection);
    }

    /**
     * stop
     */
    public void stop() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setDirection(mDirections[directionIndex]);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    /**
     * init Animator
     *
     * @param duration  duration
     * @param direction sweep direction
     */
    private void initAnimator(int duration, Direction direction) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator.start();
        } else {
            int start = 0;
            int end = 360;
            if (direction == Direction.REVERSE) {
                start = 360;
                end = 0;
            }
            animator = ValueAnimator.ofInt(start, end).setDuration(duration);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (int) animation.getAnimatedValue();
                    if (mProgressChangeListener != null) {
                        mProgressChangeListener.onProgressChanged((int) (progress / 3.6));
                    }
                    invalidate();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mProgressChangeListener != null) {
                        mProgressChangeListener.onFinish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }
    }

    /**
     * set sweep arc strokeWidth
     *
     * @param strokeWidth strokeWidth
     */
    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth > 0) {
            this.strokeWidth = strokeWidth;
        }
    }

    /**
     * set sweep arc strokeColor
     *
     * @param strokeColor strokeColor
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * set countDown millis
     *
     * @param countDownTimeMillis countDownTimeMillis
     */
    public void setCountDownTimeMillis(int countDownTimeMillis) {
        this.countDownTimeMillis = countDownTimeMillis;
    }

    /**
     * set center background (color)
     *
     * @param centerBackground centerBackground
     */
    public void setCenterBackground(int centerBackground) {
        this.centerBackground = centerBackground;
    }

    /**
     * set center text
     * if is none , then start progress text countDown(100% ~ 0% | 0 % ~ 100%)
     *
     * @param centerText centerText
     */
    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    /**
     * set center textColor
     *
     * @param centerTextColor centerTextColor
     */
    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
    }

    /**
     * set center textSize
     *
     * @param centerTextSize centerTextSize
     */
    public void setCenterTextSize(float centerTextSize) {
        this.centerTextSize = centerTextSize;
    }

    /**
     * set sweep start angle
     *
     * @param startAngle start angle default is -90
     */
    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * set if is auto start
     *
     * @param isAutoStart if is auto start
     */
    public void setAutoStart(boolean isAutoStart) {
        this.isAutoStart = isAutoStart;
    }

    /**
     * set progressChange listener
     *
     * @param progressChangeListener progressChangeListener
     */
    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        mProgressChangeListener = progressChangeListener;
    }

    /**
     * set direction
     *
     * @param direction sweep direction
     */
    public void setDirection(Direction direction) {
        if (direction == null) {
            throw new RuntimeException("Direction is null");
        }
        mDirection = direction;
        switch (direction) {
            default:
            case FORWARD:
                progress = 0;
                break;
            case REVERSE:
                progress = 360;
                break;
        }
        if (isAutoStart) {
            start();
        }
    }

    /**
     * set progress by your self
     *
     * @param progress progress
     */
    public void setProgress(int progress) {
        if (progress > 100) {
            progress = 100;
        } else if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;
        invalidate();
    }

    public void setShouldDrawOutsideWrapper(boolean shouldDrawOutsideWrapper) {
        this.shouldDrawOutsideWrapper = shouldDrawOutsideWrapper;
    }

    public void setOutsideWrapperColor(int outsideWrapperColor) {
        this.outsideWrapperColor = outsideWrapperColor;
    }

    public boolean isSupportEts() {
        return isSupportEts;
    }

    public void setSupportEts(boolean supportEts) {
        isSupportEts = supportEts;
    }

    private float sp2px(float inParam) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, inParam, getContext().getResources().getDisplayMetrics());
    }

    public interface ProgressChangeListener {
        /**
         * onFinish
         */
        void onFinish();

        /**
         * onProgressChanged
         *
         * @param progress
         */
        void onProgressChanged(int progress);
    }


}
