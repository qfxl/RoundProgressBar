/*
 * Copyright (c) 2019 qfxl
 *
 * Anti 996 License Version 1.0 (Draft)
 *
 * Permission is hereby granted to any individual or legal entity
 * obtaining a copy of this licensed work (including the source code,
 * documentation and/or related items, hereinafter collectively referred
 * to as the "licensed work"), free of charge, to deal with the licensed
 * work for any purpose, including without limitation, the rights to use,
 * reproduce, modify, prepare derivative works of, distribute, publish
 * and sublicense the licensed work, subject to the following conditions:
 *
 * 1. The individual or the legal entity must conspicuously display,
 * without modification, this License and the notice on each redistributed
 * or derivative copy of the Licensed Work.
 *
 * 2. The individual or the legal entity must strictly comply with all
 * applicable laws, regulations, rules and standards of the jurisdiction
 * relating to labor and employment where the individual is physically
 * located or where the individual was born or naturalized; or where the
 * legal entity is registered or is operating (whichever is stricter). In
 * case that the jurisdiction has no such laws, regulations, rules and
 * standards or its laws, regulations, rules and standards are
 * unenforceable, the individual or the legal entity are required to
 * comply with Core International Labor Standards.
 *
 * 3. The individual or the legal entity shall not induce or force its
 * employee(s), whether full-time or part-time, or its independent
 * contractor(s), in any methods, to agree in oral or written form, to
 * directly or indirectly restrict, weaken or relinquish his or her
 * rights or remedies under such laws, regulations, rules and standards
 * relating to labor and employment as mentioned above, no matter whether
 * such written or oral agreement are enforceable under the laws of the
 * said jurisdiction, nor shall such individual or the legal entity
 * limit, in any methods, the rights of its employee(s) or independent
 * contractor(s) from reporting or complaining to the copyright holder or
 * relevant authorities monitoring the compliance of the license about
 * its violation(s) of the said license.
 *
 * THE LICENSED WORK IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN ANY WAY CONNECTION WITH THE
 * LICENSED WORK OR THE USE OR OTHER DEALINGS IN THE LICENSED WORK.
 */
package com.github.view;

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
import android.util.DisplayMetrics;
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
    private final RectF arcRect;
    /**
     * textPaint
     */
    private final Paint textPaint;
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
    private final Paint centerBgPaint;
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
    private final String emptyText = "100%";
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
    private final Rect textBounds;
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
    private final int directionIndex;

    private final Direction[] mDirections = {
            Direction.FORWARD,
            Direction.REVERSE
    };

    public enum Direction {
        /**
         * forward
         */
        FORWARD(0),
        /**
         * reverse
         */
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
    private final int defaultSpace;
    /**
     * isSupport end to start, if true progress = progress - 360.
     */
    private boolean isSupportEts;

    private long currentTime;

    private boolean isFinished = false;
    /**
     * animateEnd is not reliable
     */
    private final Runnable animateEndRunnable = new Runnable() {
        @Override
        public void run() {
            isFinished = true;
            if (animator != null && mProgressChangeListener != null) {
                mProgressChangeListener.onFinish();
            }
        }
    };

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        strokeWidth = a.getDimensionPixelSize(R.styleable.RoundProgressBar_rpb_sweepStrokeWidth, (int) dp2px(2));
        strokeColor = a.getColor(R.styleable.RoundProgressBar_rpb_sweepStrokeColor, Color.BLACK);
        startAngle = a.getInteger(R.styleable.RoundProgressBar_rpb_sweepStartAngle, -90);
        centerText = a.getString(R.styleable.RoundProgressBar_rpb_centerText);
        centerTextSize = a.getDimension(R.styleable.RoundProgressBar_rpb_centerTextSize, sp2px(12));
        centerTextColor = a.getColor(R.styleable.RoundProgressBar_rpb_centerTextColor, Color.WHITE);
        centerBackground = a.getColor(R.styleable.RoundProgressBar_rpb_centerBackgroundColor, Color.parseColor("#808080"));
        countDownTimeMillis = a.getInteger(R.styleable.RoundProgressBar_rpb_countDownTimeInMillis, 3 * 1000);
        directionIndex = a.getInt(R.styleable.RoundProgressBar_rpb_progressDirection, 0);
        isAutoStart = a.getBoolean(R.styleable.RoundProgressBar_rpb_autoStart, true);
        shouldDrawOutsideWrapper = a.getBoolean(R.styleable.RoundProgressBar_rpb_drawOutsideWrapper, false);
        outsideWrapperColor = a.getColor(R.styleable.RoundProgressBar_rpb_outsideWrapperColor, Color.parseColor("#E8E8E8"));
        isSupportEts = a.getBoolean(R.styleable.RoundProgressBar_rpb_supportEndToStart, false);
        a.recycle();
        defaultSpace = strokeWidth * 2;
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
            int suggestedSize = Math.max(minWidth, minHeight);
            minWidth = suggestedSize;
            minHeight = suggestedSize;
        }
        setMeasuredDimension(minWidth, minHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        arcRect.left = defaultSpace >> 1;
        arcRect.top = defaultSpace >> 1;
        arcRect.right = w - (defaultSpace >> 1);
        arcRect.bottom = h - (defaultSpace >> 1);
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
        drawCenterBackground(canvas);
        if (shouldDrawOutsideWrapper) {
            drawOutsideWrapper(canvas);
        }
        drawArc(canvas);
        drawCenterText(canvas);
    }

    /**
     * draw center background circle
     *
     * @param canvas
     */
    private void drawCenterBackground(Canvas canvas) {
        centerBgPaint.setColor(centerBackground);
        canvas.drawCircle(arcRect.centerX(), arcRect.centerY(), (arcRect.width() - (defaultSpace >> 2)) / 2, centerBgPaint);
    }

    /**
     * draw outside arc wrapper if needed
     *
     * @param canvas
     */
    private void drawOutsideWrapper(Canvas canvas) {
        arcPaint.setColor(outsideWrapperColor);
        canvas.drawArc(arcRect, 0, 360, false, arcPaint);
    }

    /**
     * draw sweep arc
     * core
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setColor(strokeColor);
        canvas.drawArc(arcRect, startAngle, isSupportEts ? progress - 360 : progress, false, arcPaint);
    }

    /**
     * draw centerText
     *
     * @param canvas
     */
    private void drawCenterText(Canvas canvas) {
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
        if (animator != null) {
            if (animator.isStarted()) {
                animator.cancel();
            }
            animator.start();
        }
    }

    /**
     * stop
     */
    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
        isFinished = true;
        removeCallbacks(animateEndRunnable);
    }

    /**
     * pause
     */
    public void pause() {
        if (isStarted()) {
            currentTime = animator.getCurrentPlayTime();
            animator.pause();
            removeCallbacks(animateEndRunnable);
        }
    }

    /**
     * resume
     */
    public void resume() {
        if (isPaused()) {
            animator.resume();
            long animatorLeftTime = countDownTimeMillis - currentTime;
            if (animatorLeftTime > 0) {
                postDelayed(animateEndRunnable, animatorLeftTime);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
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
        int start = 0;
        int end = 360;
        if (direction == Direction.REVERSE) {
            start = 360;
            end = 0;
        }
        animator = ValueAnimator.ofInt(start, end).setDuration(duration);
        animator.setRepeatCount(0);
        animator.setInterpolator(new LinearInterpolator());
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

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        postDelayed(animateEndRunnable, duration);
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
        initAnimator(countDownTimeMillis, mDirection);
        if (animator != null && animator.isRunning()) {
            stop();
        }
        if (isAutoStart) {
            start();
        }
    }

    /**
     * set progress by your self
     *
     * @param progress progress 0-360
     */
    public void setProgress(int progress) {
        if (progress > 360) {
            progress = 360;
        } else if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;
        invalidate();
    }

    /**
     * set progress percent
     *
     * @param progressPercent 0-100
     */
    public void setProgressPercent(int progressPercent) {
        if (progressPercent > 100) {
            progressPercent = 100;
        } else if (progressPercent < 0) {
            progressPercent = 0;
        }
        this.progress = (int) (progressPercent * 3.6);
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

    public boolean isRunning() {
        return animator != null && animator.isRunning();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isStarted() {
        return animator != null && animator.isStarted();
    }

    public boolean isPaused() {
        return animator != null && animator.isPaused();
    }

    private float sp2px(float inParam) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, inParam, getContext().getResources().getDisplayMetrics());
    }

    private float dp2px(float dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return dp < 0 ? dp : Math.round(dp * displayMetrics.density);
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
