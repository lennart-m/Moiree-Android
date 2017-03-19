package de.lennartmeinhardt.android.moiree.imaging;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.drawable.DrawableWrapper;

public class RescaledDrawable extends DrawableWrapper {

    private float scaleX, scaleY;


    public RescaledDrawable(Drawable drawable) {
        this(drawable, 1);
    }

    public RescaledDrawable(Drawable drawable, float scale) {
        this(drawable, scale, scale);
    }

    public RescaledDrawable(Drawable drawable, float scaleX, float scaleY) {
        super(drawable);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }


    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.save();
        // scale around the center of this drawable's bounds
        canvas.scale(scaleX, scaleY, bounds.width() / 2f, bounds.height() / 2f);
        getWrappedDrawable().draw(canvas);
        canvas.restore();
    }

    public float getScaleX() {
        return scaleX;
    }
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        invalidateSelf();
    }

    public float getScaleY() {
        return scaleY;
    }
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        invalidateSelf();
    }
}
