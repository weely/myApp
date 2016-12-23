package com.lw.myapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

/**
 * Created by Lw on 2016/12/20.
 */

public class RoundImageDrawable extends Drawable {

    private Paint paint;
    private Bitmap bitmap;
    private float round = 30;
    private int mWidth;
    private RectF rectF;
    private RoundType type = RoundType.TYPE_CIRCLE;

    public RoundImageDrawable(Bitmap bitmap, float round) {
        this.bitmap = bitmap;
        this.round = round;
        type = RoundType.TYPE_ROUND;
        init();
    }

    public RoundImageDrawable(Bitmap bitmap, RoundType type) {
        this.bitmap = bitmap;
        this.type = type;
        init();
    }

    public RoundImageDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
        init();
    }

    private void init() {
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP,
                TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        if (type == RoundType.TYPE_CIRCLE) {
            mWidth = Math.min(bitmap.getWidth(), bitmap.getHeight());
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (type == RoundType.TYPE_ROUND) {
            rectF = new RectF(left, top, right, bottom);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        if (type == RoundType.TYPE_ROUND) {
            canvas.drawRoundRect(rectF, round, round, paint);
        }
        if (type == RoundType.TYPE_CIRCLE) {
            canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, paint);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        if (type == RoundType.TYPE_ROUND) {
            return bitmap.getWidth();
        }
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        if (type == RoundType.TYPE_ROUND) {
            return bitmap.getHeight();
        }
        return mWidth;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public enum RoundType {
        /**
         * 0：画圆角
         * 1：画圆
         */
        TYPE_CIRCLE   (0),

        TYPE_ROUND  (1);

        RoundType(int nativeInt) {
            this.nativeInt = nativeInt;
        }
        final int nativeInt;
    }
}
