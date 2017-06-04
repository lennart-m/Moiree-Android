package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class TrianglesImageCreator extends BaseBitmapMoireeImageCreator<BitmapDrawable> implements PreferenceIO, BundleIO {

    private static final String KEY_TRIANGLE_SIZE = "trianglesImageCreator:triangleSizeInPixels";

    public final ObservableInt triangleSizeInPixels = new ObservableInt();


    public TrianglesImageCreator() {
        this(1);
    }

    public TrianglesImageCreator(int triangleSizeInPixels) {
        this.triangleSizeInPixels.set(triangleSizeInPixels);
    }


    @Override
    public void loadFromPreferences(SharedPreferences preferences) {
        triangleSizeInPixels.set(preferences.getInt(KEY_TRIANGLE_SIZE, triangleSizeInPixels.get()));
    }

    @Override
    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putInt(KEY_TRIANGLE_SIZE, triangleSizeInPixels.get());
    }

    @Override
    public void loadFromBundle(Bundle bundle) {
        triangleSizeInPixels.set(bundle.getInt(KEY_TRIANGLE_SIZE, triangleSizeInPixels.get()));
    }

    @Override
    public void storeToBundle(Bundle bundle) {
        bundle.putInt(KEY_TRIANGLE_SIZE, triangleSizeInPixels.get());
    }

    @Override
    public Bitmap createBitmapForDimensions(int width, int height) {
        Bitmap bitmap = createEmptyBitmap(width, height);
        drawTrianglesToImage(bitmap, triangleSizeInPixels.get());
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

    public static void drawTrianglesToImage(Bitmap image, float triangleWidth) {
        int width = image.getWidth();
        int height = image.getHeight();

        float triangleHeight = (float) (triangleWidth * Math.sqrt(3) / 2);

        // fill triangles in black
        Paint fillBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillBlack.setStyle(Paint.Style.FILL);
        fillBlack.setColor(Color.BLACK);

        // create the triangle path
        Path triangle = new Path();
        triangle.moveTo(0, 0);
        triangle.lineTo(triangleWidth, 0);
        triangle.lineTo(triangleWidth / 2, triangleHeight);
        triangle.close();

        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        final int numRowsInEachImageHalf = (int) Math.ceil((height / 2f) / triangleHeight);

        for(int j = - numRowsInEachImageHalf; j < numRowsInEachImageHalf; j++) {
            final float y = width / 2 + j * triangleHeight;
            final float startX;
            if(j % 2 == 0)
                startX = width / 2f - triangleWidth * numTrianglesHittingRowAligned(width / 2f, triangleWidth);
            else
                startX = width / 2f - triangleWidth * numTrianglesHittingRowAligned(width / 2f - triangleWidth / 2f, triangleWidth) - triangleWidth / 2;

            for(float x = startX; x < width + triangleWidth; x += triangleWidth) {
                canvas.save();
                canvas.translate(x, y);
                canvas.drawPath(triangle, fillBlack);
                canvas.restore();
            }
        }
    }

    /**
     * Get the number of triangles filling a row such that the left triangle is aligned on the left.
     * If triangle width is 1.5, and row width is 2, two triangles are needed to fill the row.
     * Due to alignment of triangles, a row of width 3 is also filled by 2 triangles of width 2.5.
     * Without alignment three triangles might be needed (if they were aligned in the center, for example).
     */
    private static int numTrianglesHittingRowAligned(float rowWidth, float triangleWidth) {
        return (int) Math.ceil(rowWidth / triangleWidth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrianglesImageCreator that = (TrianglesImageCreator) o;

        return triangleSizeInPixels.get() == that.triangleSizeInPixels.get();
    }

    @Override
    public int hashCode() {
        return triangleSizeInPixels.get();
    }

}
