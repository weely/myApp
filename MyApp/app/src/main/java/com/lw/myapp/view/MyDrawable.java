package com.lw.myapp.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

/**
 * Created by Lw on 2016/12/22.
 */

public class MyDrawable extends Drawable {
    private Paint paint;
    private Bitmap bitmap;

    private int weight;
    private int height;

    public MyDrawable(Bitmap bitmap, int height, int weight) {
        this.bitmap = bitmap;
        this.height = height;
        this.weight = weight;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        paint = new Paint();
        /*int weight = bitmap.getWidth();
        int height = bitmap.getHeight();*/
        BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.MIRROR,
                TileMode.MIRROR);
        Paint paint1 = new Paint();
        paint1.setShader(bitmapShader);
        canvas.drawRect(0, 0, weight, height, paint1);
        //canvas.drawBitmap(bitmap, 0, 0, paint1);
        //canvas.drawBitmap(bitmap, 0, 0, null);
        //设置遮罩效果
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //设置渐变效果,设置着色器为遮罩着色
        LinearGradient shader = new LinearGradient(0, 0, weight, height, 0x55ffffff, 0x88ffffff, TileMode.CLAMP);
        paint.setShader(shader);

        canvas.drawRect(0, 0, weight, height, paint);
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
