package com.gentledevs.chatmodule.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by Yura Stetsyc on 4/25/17.
 */

public class RoundTransformation implements Transformation {

    private boolean mIsSelf;
    private float mRadius;
    private int mDisplayWidth;

    public RoundTransformation(boolean isSelf, float radius, int displayWidth) {
        mIsSelf = isSelf;
        mRadius = radius;
        mDisplayWidth = displayWidth;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap.Config config = source.getConfig();
        if (config == null) config = Bitmap.Config.ARGB_8888;

        float ratio = getBitmapRatio(source);

        int width = (int) (source.getWidth() * ratio);
        int height = (int) (source.getHeight() * ratio);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, width, height, false);
        Bitmap bmp = Bitmap.createBitmap(width, height, config);

        Canvas canvas = new Canvas(bmp);
        Shader shader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRect(new RectF(mIsSelf ? mRadius : 0,
                mRadius, width - (mIsSelf ? 0 : mRadius),
                height), paint);
        canvas.drawRoundRect(rectF, mRadius, mRadius, paint);

        source.recycle();
        return bmp;
    }

    private float getBitmapRatio(Bitmap bitmap) {
        if (mDisplayWidth >= bitmap.getWidth()) return 1f;
        else return (float) mDisplayWidth / bitmap.getWidth();
    }

    @Override
    public String key() {
        return "round_transformation";
    }
}
