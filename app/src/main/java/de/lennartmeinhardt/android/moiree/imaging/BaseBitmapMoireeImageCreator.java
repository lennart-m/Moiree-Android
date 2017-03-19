package de.lennartmeinhardt.android.moiree.imaging;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public abstract class BaseBitmapMoireeImageCreator <D extends Drawable> implements MoireeImageCreator {

    public abstract Bitmap createBitmapForDimensions(int width, int height);

    public abstract D createDrawableFromBitmap(Resources resources, Bitmap bitmap);

    public abstract Bitmap getBitmapFromDrawable(D drawable);

    protected Bitmap createEmptyBitmap(int width, int height) {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    public D createMoireeImageForDimensions(Resources resources, int width, int height) {
        Bitmap bitmap = createBitmapForDimensions(width, height);
        return createDrawableFromBitmap(resources, bitmap);
    }
}
