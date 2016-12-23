package com.lw.myapp.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by Lw on 2016/12/21.
 */

public class MyImageDrawable extends Drawable {
    private Paint paint;
    private Bitmap rawBitmap;
    private int mWidth;
    private RectF rectF;
    private Matrix matrix;
    private int gap = 4;
    private int leftX = 40;

    public MyImageDrawable(Bitmap bitmap) {
        this.rawBitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;

        int width = rawBitmap.getWidth();
        int height = rawBitmap.getHeight();
        matrix = new Matrix();
        matrix.setScale(1, -1);
        Bitmap refrectionBitmap = Bitmap.createBitmap(rawBitmap, 0, height / 2, width, height / 2, matrix, false);
        Bitmap bitmap = Bitmap.createBitmap(width, height + height / 2, Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(rawBitmap, leftX, 0, null);
        Paint defaultPaint = new Paint();
        canvas.drawRect(leftX, height, width, height + gap, defaultPaint);
        canvas.drawBitmap(refrectionBitmap, leftX, height + gap, null);

        paint = new Paint();
        //设置遮罩效果
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //设置渐变效果,设置着色器为遮罩着色
        LinearGradient shader = new LinearGradient(0, height, 0, bitmap.getHeight(), 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawRect(leftX, height, width + leftX, bitmap.getHeight(), paint);
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
}
