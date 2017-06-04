package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class CheckerboardImageCreator extends BaseBitmapMoireeImageCreator<BitmapDrawable> implements BundleIO, PreferenceIO {

    private static final String KEY_SQUARE_SIZE = "checkerboardImageCreator:squareSizeInPixels";

    public final ObservableInt squareSizeInPixels = new ObservableInt();


    public CheckerboardImageCreator() {
        this(1);
    }

    public CheckerboardImageCreator(int squareSizeInPixels) {
        this.squareSizeInPixels.set(squareSizeInPixels);
    }


    @Override
    public void loadFromPreferences(SharedPreferences preferences) {
        squareSizeInPixels.set(preferences.getInt(KEY_SQUARE_SIZE, squareSizeInPixels.get()));
    }

    @Override
    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putInt(KEY_SQUARE_SIZE, squareSizeInPixels.get());
    }

    @Override
    public void loadFromBundle(Bundle bundle) {
        squareSizeInPixels.set(bundle.getInt(KEY_SQUARE_SIZE, squareSizeInPixels.get()));
    }

    @Override
    public void storeToBundle(Bundle bundle) {
        bundle.putInt(KEY_SQUARE_SIZE, squareSizeInPixels.get());
    }

    @Override
    public Bitmap createBitmapForDimensions(int width, int height) {
        Bitmap bitmap = createEmptyBitmap(width, height);
        drawCheckerboardToImage(bitmap, squareSizeInPixels.get());
        return bitmap;
    }

    @Override
    public BitmapDrawable createDrawableFromBitmap(Resources resources, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
        drawable.setAntiAlias(false);
        drawable.setDither(false);
        drawable.setFilterBitmap(false);
        return drawable;
    }

    @Override
    public Bitmap getBitmapFromDrawable(BitmapDrawable drawable) {
        return drawable.getBitmap();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheckerboardImageCreator that = (CheckerboardImageCreator) o;

        return squareSizeInPixels.get() == that.squareSizeInPixels.get();
    }

    @Override
    public int hashCode() {
        return squareSizeInPixels.get();
    }

    private static void drawCheckerboardToImage(Bitmap image, int squareSizeInPixels) {
        int width = image.getWidth();
        int height = image.getHeight();

        final int segmentsPerImageHalfX = (int) Math.ceil((width / 2f) / squareSizeInPixels);
        final int segmentsPerImageHalfY = (int) Math.ceil((height / 2f) / squareSizeInPixels);

        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Paint fillBlack = new Paint();

        for (int i = - segmentsPerImageHalfX; i < segmentsPerImageHalfX; i++) {
            final float x = width / 2f + i * squareSizeInPixels;
            for (int j = -segmentsPerImageHalfY; j < segmentsPerImageHalfY; j++) {
                final float y = height / 2f + j * squareSizeInPixels;
                if ((i + j) % 2 == 0)
                    canvas.drawRect(x, y, x + squareSizeInPixels, y + squareSizeInPixels, fillBlack);
            }
        }
    }
}
