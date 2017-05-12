package com.gentledevs.chatmodule.drawable;


import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by Yura Stetsyc on 4/18/17.
 */
public class ChatDrawable extends Drawable {

    private static final int TRIANGLE_SIZE = 22;

    private static final int SHADOW_WIDTH = 32;
    private static final int SHADOW_OFFSET = 8;

    private static final int CORNER_RADIUS = 10;

    private static final int SHADOW_COLOR = 0xffDDDDDD;

    private Paint mContentAccent = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Path mTrianglePath = new Path();
    private Path mTriangleShadowPath = new Path();

    private RectF mContentRect = new RectF();
    private RectF mShadowRect = new RectF();

    private boolean mIsSelf = true;

    public ChatDrawable() {
        mShadowPaint.setColor(SHADOW_COLOR);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPath(mTriangleShadowPath, mShadowPaint);
        canvas.drawRoundRect(mShadowRect, CORNER_RADIUS, CORNER_RADIUS, mShadowPaint);

        canvas.drawPath(mTrianglePath, mContentAccent);
        canvas.drawRoundRect(mContentRect, CORNER_RADIUS, CORNER_RADIUS, mContentAccent);
    }

    @Override
    public void setAlpha(int alpha) {
        mContentAccent.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mContentAccent.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if (mIsSelf) {
            prepareSelf(bounds);
        } else {
            prepareOther(bounds);
        }
    }

    private void prepareSelf(Rect bounds) {
        Rect rect = new Rect(bounds);
        rect.bottom = rect.bottom - SHADOW_OFFSET;

        mContentRect.set(rect);
        mContentRect.right = mContentRect.right - TRIANGLE_SIZE;

        mTrianglePath.reset();
        mTrianglePath.moveTo(rect.right - TRIANGLE_SIZE, rect.bottom - TRIANGLE_SIZE);
        mTrianglePath.lineTo(rect.right, rect.bottom);
        mTrianglePath.lineTo(rect.right - TRIANGLE_SIZE - CORNER_RADIUS, rect.bottom);
        mTrianglePath.close();

        mShadowRect.set(bounds);
        mShadowRect.right = mShadowRect.right - TRIANGLE_SIZE;
        mShadowRect.top = mShadowRect.bottom - SHADOW_WIDTH;

        mTriangleShadowPath.reset();
        mTriangleShadowPath.moveTo(mShadowRect.right, mShadowRect.bottom - TRIANGLE_SIZE);
        mTriangleShadowPath.lineTo(bounds.right, mShadowRect.bottom);
        mTriangleShadowPath.lineTo(mShadowRect.right - CORNER_RADIUS, mShadowRect.bottom);
        mTriangleShadowPath.close();
    }

    private void prepareOther(Rect bounds) {
        Rect rect = new Rect(bounds);
        rect.bottom = rect.bottom - SHADOW_OFFSET;

        mContentRect.set(rect);
        mContentRect.left = mContentRect.left + TRIANGLE_SIZE;

        mTrianglePath.reset();
        mTrianglePath.moveTo(mContentRect.left + CORNER_RADIUS, mContentRect.bottom - TRIANGLE_SIZE);
        mTrianglePath.lineTo(mContentRect.left + CORNER_RADIUS, rect.bottom);
        mTrianglePath.lineTo(rect.left, rect.bottom);
        mTrianglePath.close();

        mShadowRect.set(bounds);
        mShadowRect.left = mShadowRect.left + TRIANGLE_SIZE;
        mShadowRect.top = mShadowRect.bottom - SHADOW_WIDTH;

        mTriangleShadowPath.reset();
        mTriangleShadowPath.moveTo(mShadowRect.left + CORNER_RADIUS, mShadowRect.bottom - TRIANGLE_SIZE);
        mTriangleShadowPath.lineTo(mShadowRect.left + CORNER_RADIUS, mShadowRect.bottom);
        mTriangleShadowPath.lineTo(bounds.left, mShadowRect.bottom);
        mTriangleShadowPath.close();
    }

    public boolean isSelf() {
        return mIsSelf;
    }

    public void setSelf(boolean self) {
        mIsSelf = self;
    }
}

