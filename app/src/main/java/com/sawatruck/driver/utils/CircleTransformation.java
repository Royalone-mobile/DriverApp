package com.sawatruck.driver.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * Created by royalone on 2017-03-03.
 */

public class CircleTransformation implements com.squareup.picasso.Transformation {
    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int radius = 0;
        if(source.getWidth() >= source.getHeight())
            radius = source.getHeight()/2;
        else
            radius = source.getWidth()/2;

        Logger.error("Radius = " + radius);
        canvas.drawCircle(source.getWidth() - radius,source.getHeight() -radius, radius, paint);
        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}