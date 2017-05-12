package com.gentledevs.chatmodule.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Yura Stetsyc on 4/21/17.
 */

public class RoundTarget implements Target {

    private ImageView mImageView;
    private boolean mIsSelf;
    private float mRadius;

    public RoundTarget(ImageView imageView, boolean isSelf, float radius) {
        mImageView = imageView;
        mIsSelf = isSelf;
        mRadius = radius;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) config = Bitmap.Config.ARGB_8888;

        float ratio = getBitmapRatio(bitmap);

        int width = (int) (bitmap.getWidth() * ratio);
        int height = (int) (bitmap.getHeight() * ratio);

        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Bitmap bmp = Bitmap.createBitmap(width, height, config);

        Canvas canvas = new Canvas(bmp);
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRect(new RectF(mIsSelf ? mRadius : 0,
                mRadius, width - (mIsSelf ? 0 : mRadius),
                height), paint);
        canvas.drawRoundRect(rectF, mRadius, mRadius, paint);
        mImageView.setImageBitmap(bmp);
    }

    private float getBitmapRatio(Bitmap bitmap) {
        int widthPixels = mImageView.getContext().getResources().getDisplayMetrics().widthPixels;
        if (widthPixels >= bitmap.getWidth()) return 1f;
        else return (float) widthPixels / bitmap.getWidth();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
}
