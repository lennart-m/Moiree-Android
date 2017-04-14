package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;

public class CheckerboardImageCreator extends BaseBitmapMoireeImageCreator<RescaledDrawable> implements BundleIO, PreferenceIO {

    private static final String KEY_SQUARE_SIZE = "checkerboardImageCreator:squareSizeInPixels";

    private int squareSizeInPixels;


    public CheckerboardImageCreator() {
        this(1);
    }

    public CheckerboardImageCreator(int squareSizeInPixels) {
        this.squareSizeInPixels = squareSizeInPixels;
    }


    @Override
    public void loadFromPreferences(SharedPreferences preferences) {
        squareSizeInPixels = preferences.getInt(KEY_SQUARE_SIZE, squareSizeInPixels);
    }

    @Override
    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putInt(KEY_SQUARE_SIZE, squareSizeInPixels);
    }

    @Override
    public void loadFromBundle(Bundle bundle) {
        squareSizeInPixels = bundle.getInt(KEY_SQUARE_SIZE, squareSizeInPixels);
    }

    @Override
    public void storeToBundle(Bundle bundle) {
        bundle.putInt(KEY_SQUARE_SIZE, squareSizeInPixels);
    }

    public int getSquareSizeInPixels() {
        return squareSizeInPixels;
    }
    public void setSquareSizeInPixels(int squareSizeInPixels) {
        this.squareSizeInPixels = squareSizeInPixels;
    }

    @Override
    public Bitmap createBitmapForDimensions(int width, int height) {
        int imageWidth = (width + squareSizeInPixels - 1) / squareSizeInPixels;
        int imageHeight = (height + squareSizeInPixels - 1) / squareSizeInPixels;
        Bitmap bitmap = createEmptyBitmap(imageWidth, imageHeight);
        drawCheckerboardToImage(bitmap);
        return bitmap;
    }

    @Override
    public RescaledDrawable createDrawableFromBitmap(Resources resources, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
        drawable.setAntiAlias(false);
        drawable.setDither(false);
        drawable.setFilterBitmap(false);
        return new RescaledDrawable(drawable, squareSizeInPixels);
    }

    @Override
    public Bitmap getBitmapFromDrawable(RescaledDrawable drawable) {
        return ((BitmapDrawable) drawable.getWrappedDrawable()).getBitmap();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckerboardImageCreator that = (CheckerboardImageCreator) o;

        return squareSizeInPixels == that.squareSizeInPixels;
    }

    @Override
    public int hashCode() {
        return squareSizeInPixels;
    }

    private static void drawCheckerboardToImage(Bitmap image) {
        double width = image.getWidth();
        double height = image.getHeight();

        int argb;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((x + y) % 2 == 0)
                    argb = BLACK;
                else
                    argb = TRANSPARENT;

                image.setPixel(x, y, argb);
            }
        }
    }
}
