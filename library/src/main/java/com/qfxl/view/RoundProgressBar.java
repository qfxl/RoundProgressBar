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
 * 清风徐来
 * 圆形倒计时控件(提供百分比计时,固定文本计时)
 */

public class RoundProgressBar extends View {
    /**
     * 绘制圆弧的画笔
     */
    private Paint arcPaint;

    /**
     * 圆弧
     */
    private RectF arcRect;

    /**
     * 绘制文本的画笔
     */
    private Paint textPaint;

    /**
     * 倒计时圆弧的宽度
     */
    private int strokeWidth;

    /**
     * 倒计时圆弧的颜色
     */
    private int strokeColor;

    /**
     * 进度
     */
    private int progress;

    /**
     * 倒计时时间 默认3秒
     */
    private int countDownTimeMillis;

    /**
     * 中间区域绘制画笔
     */
    private Paint centerBgPaint;

    /**
     * 中间区域的颜色
     */
    private int centerBackground;

    /**
     * 中间的文字
     */
    private String centerText;

    /**
     * 没有文字时候的占位字符
     */
    private String emptyText = "100%";

    /**
     * 中间文字的颜色
     */
    private int centerTextColor;

    /**
     * 中间文本的大小
     */
    private float centerTextSize;

    /**
     * 测量文字的Rect
     */
    private Rect textBounds;

    /**
     * 弧度开始的位置,默认顶部中间
     */
    private int startAngle;

    /**
     * 是否自动开启计时(默认true)
     */
    private boolean isAutoStart;

    /**
     * 监听progress
     */
    private ProgressChangeListener mProgressChangeListener;

    /**
     * 倒计时圆弧的方向
     */
    private Direction mDirection;
    /**
     * attr获取的direction的枚举值
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
     * 属性动画
     */
    private ValueAnimator animator;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        strokeWidth = a.getDimensionPixelSize(R.styleable.RoundProgressBar_sweep_stroke_width, 5);
        strokeColor = a.getColor(R.styleable.RoundProgressBar_sweep_stroke_color, Color.BLACK);
        startAngle = a.getInteger(R.styleable.RoundProgressBar_sweep_start_angle, -90);
        centerText = a.getString(R.styleable.RoundProgressBar_center_text);
        centerTextSize = a.getDimension(R.styleable.RoundProgressBar_center_text_size, sp2px(12));
        centerTextColor = a.getColor(R.styleable.RoundProgressBar_center_text_color, Color.BLACK);
        centerBackground = a.getColor(R.styleable.RoundProgressBar_center_background, Color.GRAY);
        countDownTimeMillis = a.getInteger(R.styleable.RoundProgressBar_count_down_time_millis, 3 * 1000);
        directionIndex = a.getInt(R.styleable.RoundProgressBar_progress_direction, 0);
        isAutoStart = a.getBoolean(R.styleable.RoundProgressBar_auto_start, true);
        a.recycle();

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setColor(strokeColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(centerTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(centerTextColor);

        centerBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerBgPaint.setStyle(Paint.Style.FILL);
        centerBgPaint.setColor(centerBackground);

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
        arcRect.left = strokeWidth / 2;
        arcRect.top = strokeWidth / 2;
        arcRect.right = w - strokeWidth / 2;
        arcRect.bottom = h - strokeWidth / 2;
    }

    /**
     * 获取最小的高度 文字高度 + paddingLeft + paddingRight + 外弧的宽度 * 2
     *
     * @param mode
     * @param measuredSize
     * @return
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
                suggestSize = getPaddingLeft() + getPaddingRight() + textBounds.width() + strokeWidth * 2;
                break;
            case MeasureSpec.EXACTLY:
                suggestSize = measuredSize;
                break;
        }
        return suggestSize;
    }

    /**
     * 获取最小的高度 文字高度 + paddingBottom + paddingTop + 外弧的宽度 * 2
     *
     * @param mode
     * @param measuredSize
     * @return
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
                suggestSize = getPaddingTop() + getPaddingBottom() + textBounds.height() + strokeWidth * 2;
                break;
            case MeasureSpec.EXACTLY:
                suggestSize = measuredSize;
                break;
        }
        return suggestSize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        centerBgPaint.setColor(centerBackground);
        //绘制中间文字区域的圆
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() - strokeWidth * 2) / 2, centerBgPaint);
        //绘制外圈弧线
        canvas.drawArc(arcRect, startAngle, (float) (3.6 * progress), false, arcPaint);
        Paint.FontMetricsInt mFontMetrics = textPaint.getFontMetricsInt();
        int baseLine = (int) (arcRect.centerY() - (mFontMetrics.bottom - mFontMetrics.top) / 2 - mFontMetrics.top);
        //绘制文字
        if (TextUtils.isEmpty(centerText)) {
            canvas.drawText(progress + "%", arcRect.centerX(), baseLine, textPaint);
        } else {
            canvas.drawText(centerText, arcRect.centerX(), baseLine, textPaint);
        }
    }

    /**
     * 开始
     */
    public void start() {
        initAnimator(countDownTimeMillis, mDirection);
    }

    /**
     * 停止
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
     * 初始化Animator
     *
     * @param duration  持续时间
     * @param direction 进度条方向
     */
    private void initAnimator(int duration, Direction direction) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator.start();
        } else {
            int start = 0;
            int end = 100;
            if (direction == Direction.REVERSE) {
                start = 100;
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
                        mProgressChangeListener.onProgressChanged(progress);
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
     * 设置外弧的宽度
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * 设置外弧的颜色
     *
     * @param strokeColor
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * 设置倒计时的时间
     *
     * @param countDownTimeMillis
     */
    public void setCountDownTimeMillis(int countDownTimeMillis) {
        this.countDownTimeMillis = countDownTimeMillis;
    }

    /**
     * 设置中间文字区域的背景色
     *
     * @param centerBackground
     */
    public void setCenterBackground(int centerBackground) {
        this.centerBackground = centerBackground;
    }

    /**
     * 设置中间的文本
     *
     * @param centerText
     */
    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    /**
     * 设置中间的文本颜色
     *
     * @param centerTextColor
     */
    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
    }

    /**
     * 设置中间的文字大小
     *
     * @param centerTextSize
     */
    public void setCenterTextSize(float centerTextSize) {
        this.centerTextSize = centerTextSize;
    }

    /**
     * 设置圆弧的起始位置
     *
     * @param startAngle
     */
    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * 设置是否自动开启计时
     *
     * @param isAutoStart
     */
    public void setAutoStart(boolean isAutoStart) {
        this.isAutoStart = isAutoStart;
    }

    /**
     * 设置倒计时的监听
     *
     * @param progressChangeListener
     */
    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        mProgressChangeListener = progressChangeListener;
    }

    /**
     * 设置方向
     *
     * @param direction
     */
    public void setDirection(Direction direction) {
        if (direction == null) {
            throw new RuntimeException("Direction is null");
        }
        mDirection = direction;
        switch (direction) {
            case FORWARD:
                progress = 0;
                break;
            case REVERSE:
                progress = 100;
                break;
        }
        if (isAutoStart) {
            start();
        }
    }


    private float sp2px(float inParam) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, inParam, getContext().getResources().getDisplayMetrics());
    }

    public interface ProgressChangeListener {
        void onFinish();

        void onProgressChanged(int progress);
    }


}
