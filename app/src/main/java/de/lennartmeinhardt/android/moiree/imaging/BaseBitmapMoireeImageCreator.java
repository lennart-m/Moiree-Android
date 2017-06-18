package de.lennartmeinhardt.android.moiree.imaging;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public abstract class BaseBitmapMoireeImageCreator implements MoireeImageCreator {

    public Bitmap createBitmapForDimensions(int width, int height) {
        Bitmap bitmap = createEmptyBitmap(width, height);
        drawImageToBitmap(bitmap);
        return bitmap;
    }

    public BitmapDrawable createDrawableFromBitmap(Resources resources, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
        drawable.setAntiAlias(false);
        drawable.setFilterBitmap(false);
        drawable.setDither(false);
        return drawable;
    }

    public abstract void drawImageToBitmap(Bitmap bitmap);

    protected Bitmap createEmptyBitmap(int width, int height) {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    public BitmapDrawable createMoireeImageForDimensions(Resources resources, int width, int height) {
        Bitmap bitmap = createBitmapForDimensions(width, height);
        return createDrawableFromBitmap(resources, bitmap);
    }
}
