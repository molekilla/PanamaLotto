package com.ecyware.android.lottopanama.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.view.View;

import java.util.List;

public class LottoPyramidView extends View {
    List<String> pyramidValues;
    float scale;
    float absoluteWidth;
    float absoluteHeight;
    float gridX;
    float gridY;

    public LottoPyramidView(Context context, List<String> values) {
        super(context);
        pyramidValues = values;
        scale = 1;// getContext().getResources().getDisplayMetrics().density;
        absoluteHeight = getContext().getResources().getDisplayMetrics().heightPixels * scale;
        absoluteWidth = getContext().getResources().getDisplayMetrics().widthPixels * scale;
        gridX = absoluteWidth / 4;
        gridY = absoluteHeight / 4;
    }

    private Paint trianglePaintStyle()
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setARGB(255, 223, 98, 98);
        return paint;
    }

    private Paint textPaintStyle()
    {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setARGB(255, 223, 98, 98);
        paint.setTextSize(22);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        return paint;
    }

    // private float toDips(int pixels)
    // {
    // // Converting pixels to dips
    // float scale = getContext().getResources().getDisplayMetrics().density;
    // return pixels / scale;
    // }
    // private int toPixels(float dips)
    // {
    // // Converting dips to pixels float dips = 20.0f;
    // float scale = getContext().getResources().getDisplayMetrics().density;
    // return Math.round(dips * scale);
    // }

    public void onDraw(Canvas canvas) {

        canvas.drawColor(Color.WHITE);

        Path path = new Path();

        path.moveTo(gridX, gridY);
        path.lineTo(gridX * 3, gridY);
        path.lineTo(gridX * 2, (float) (gridY * 2.5));
        path.lineTo(gridX, gridY);
        path.lineTo(gridX * 3, gridY);

        Paint textPaintStyle = textPaintStyle();
        canvas.drawPath(path, trianglePaintStyle());
        canvas.drawText(pyramidValues.get(0), (float) (gridX * 1.3), (float) (gridY * 1.2),
                textPaintStyle);

        canvas.drawText(pyramidValues.get(1), (float) (gridX * 2.5), (float) (gridY * 1.2),
                textPaintStyle);

        canvas.drawText(pyramidValues.get(2), (float) (gridX * 1.925), (float) (gridY * 2.2),
                textPaintStyle);


    }
}
