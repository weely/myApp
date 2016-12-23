package com.lw.myapp.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by Lw on 2016/12/22.
 */

public class MyDrawable extends Drawable {
    private Paint paint;
    private Bitmap bitmap;

    public MyDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        paint = new Paint();
        int weight = bitmap.getWidth();
        int height = bitmap.getHeight();
        canvas.drawBitmap(bitmap, 0, 0, null);
        //设置遮罩效果
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //设置渐变效果,设置着色器为遮罩着色
        LinearGradient shader = new LinearGradient(0, 0, weight, height, 0x50ffffff, 0x80ffffff, Shader.TileMode.CLAMP);
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
