package ru.bmixsoft.jsontest.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Михаил on 12.12.2017.
 */

public class TestView extends View {
    Context context;
    int color;

    public TestView(Context context, int color) {
        super(context);
        this.context = context;
        this.color = color;
    }

    @Override
    public void onDraw (Canvas canvas) {
        super.onDraw(canvas);
        this.setBackgroundColor(Color.LTGRAY);
        Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawCircle(20, 20, 20, paint);
    }
}