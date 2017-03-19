package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.R;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;

public class CheckerboardImageCreator extends BaseBitmapMoireeImageCreator<RescaledDrawable> {

    private static final String KEY_SQUARE_SIZE = "checkerboardImageImageCreator:squareSizeInPixels";

    private int squareSizeInPixels;


    public CheckerboardImageCreator() {
        this(1);
    }

    public CheckerboardImageCreator(Resources resources) {
        this(resources.getDimensionPixelSize(R.dimen.checkerboard_image_default_square_size));
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
    public void loadFromBundle(Bundle bundle, String prefix) {
        squareSizeInPixels = bundle.getInt(prefix + KEY_SQUARE_SIZE, squareSizeInPixels);
    }

    @Override
    public void storeToBundle(Bundle bundle, String prefix) {
        bundle.putInt(prefix + KEY_SQUARE_SIZE, squareSizeInPixels);
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
