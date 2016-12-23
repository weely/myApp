package com.lw.myapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.lw.myapp.model.LrcInfo;

import java.util.List;

/**
 * Created by Lw on 2016/12/16.
 */

public class LrcView extends TextView {
    private List<LrcInfo> lrcInfos;
    private Paint curPaint, noCurPaint;
    private float width, height;
    private float txtHeight = 40;
    private float txtSize = 35;
    private int index = 0;
    private float drawY;

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setLrcInfos(List<LrcInfo> lrcInfos) {
        this.lrcInfos = lrcInfos;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void init() {
        setFocusable(true);
        curPaint = producePaint(Color.argb(210, 251, 248, 29), txtHeight, Typeface.SERIF);
        noCurPaint = producePaint(Color.argb(140, 255, 255, 255), txtSize, Typeface.DEFAULT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;
        try {
            setText("");
            drawY = (index + 1) * txtHeight < height / 2 ? (index + 1) * txtHeight : height / 2;
            canvas.drawText(lrcInfos.get(index).getLrcContent(), width / 2, drawY, curPaint);
            for (int i = index - 1; i > 0; i--) {
                drawY -= txtHeight + txtSize / 2;
                canvas.drawText(lrcInfos.get(i).getLrcContent(), width / 2, drawY, noCurPaint);
            }
            drawY = (index + 1) * txtHeight < height / 2 ? (index + 1) * txtHeight : height / 2;
            for (int i = index + 1; i < lrcInfos.size(); i++) {
                drawY += txtHeight + txtSize / 2;
                canvas.drawText(lrcInfos.get(i).getLrcContent(), width / 2, drawY, noCurPaint);
            }
        } catch (Exception e) {
            setText("...木有歌词文件，赶紧去下载...");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float posY = event.getY();

            invalidate();
        }
        return true;
    }

    private static Paint producePaint(int color, float size, Typeface typeface) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setTypeface(typeface);
        return paint;
    }
}
