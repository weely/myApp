package com.lw.myapp.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.lw.myapp.R;

/**
 * Created by Lw on 2016/12/21.
 */

public class RoundProgressBar extends View {
    private Paint paint;
    private Paint paintProgress;
    private Paint pointPaint;
    private int roundColor;
    private int progressColor;
    private int progressPointColor;
    private float roundWidth;
    private float progressWidth;
    private int progress;
    private int max;
    private boolean textIsDisplayable;
    private int style = STROKE;

    public static final int STROKE = 0;
    public static final int FILL = 1;
    private static final double DOUBLE_PI = 2 * 3.1415;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);
        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        progressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        progressPointColor = mTypedArray.getColor(R.styleable.RoundProgressBar_progressPointColor, Color.GREEN);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 10);
        progressWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_progressWidth, 2);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        /*textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);*/
        mTypedArray.recycle();

        init();
    }

    private void init() {
        paint = new Paint();
        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        paint.setColor(roundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);

        paintProgress.setColor(progressColor);
        paintProgress.setStrokeWidth(progressWidth);
        paintProgress.setAntiAlias(true);

        pointPaint.setColor(progressPointColor);
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = (int) ((centerX <= centerY ? centerX : centerY) - roundWidth / 2);
        canvas.drawCircle(centerX, centerY, radius, paint);

        int progressRadius = radius;
        RectF oval = new RectF(centerX - progressRadius, centerY - progressRadius,
                centerX + progressRadius, centerY + progressRadius);
        switch (style) {
            case STROKE:
                paintProgress.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * progress / max, false, paintProgress);
                break;
            case FILL:
                paintProgress.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, 0, 360 * progress / max, true, paintProgress);
                break;
        }
        int pointRadios = 10;
        pointPaint.setStrokeWidth(pointRadios);
        float x = (float) (centerX + progressRadius * Math.cos(DOUBLE_PI * progress / max));
        float y = (float) (centerY + progressRadius * Math.sin(DOUBLE_PI * progress / max));
        canvas.drawCircle(x, y, pointRadios, pointPaint);
    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        /*if (max <= 0) {
            throw new IllegalArgumentException("max not less than 0");
        } */
        this.max = max <= 0 ? 1 : max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }
}
