package com.example.user.isotuapp.utils;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.squareup.picasso.Transformation;


/**
 * Created by Hari Nugroho on 18/09/2017.
 */

public class BubleTransformation implements Transformation {
    private static final int outerMargin = 30;
    private final int margin;

    // margin is the board in dp
    public BubleTransformation(final int margin) {
        this.margin = margin;
    }

    /**
     *create a rounded, triangular form for a profile picture
     * @param source
     * @return
     */
    @Override
    public Bitmap transform(final Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;


        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        // the float array passed to this function defines the x/y values of the corners
        // it starts top-left and works clockwise
        // so top-left-x, top-left-y, top-right-x etc
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);

        Paint paintBorder = new Paint();
        paintBorder.setColor(Color.WHITE);
        paintBorder.setStrokeWidth(margin);

        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;

        Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setStrokeWidth(2);
        trianglePaint.setColor(Color.WHITE);
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setAntiAlias(true);

        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(outerMargin, source.getHeight() / 2 + 50);
        triangle.lineTo(source.getWidth()/2,source.getHeight());
        triangle.lineTo(source.getWidth()-outerMargin,source.getHeight()/2 +50);
        triangle.close();


        canvas.drawCircle(r,r,r-20,paintBorder);
        canvas.drawPath(triangle, trianglePaint);
        canvas.drawCircle(r, r, r-30, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "rounded";
    }
}