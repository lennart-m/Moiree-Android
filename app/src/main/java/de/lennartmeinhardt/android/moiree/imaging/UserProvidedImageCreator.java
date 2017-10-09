package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class UserProvidedImageCreator extends BaseBitmapMoireeImageCreator implements BundleIO, PreferenceIO {

    private static final String KEY_KEEP_ASPECT_RATIO = "userProvidedImageCreator:keepAspectRatio";

    private Bitmap userProvidedImage;

    public final ObservableBoolean keepAspectRatio = new ObservableBoolean();


    public UserProvidedImageCreator() {
        this(true);
    }

    public UserProvidedImageCreator(boolean keepAspectRatio) {
        this.keepAspectRatio.set(keepAspectRatio);
    }


    public void setUserProvidedImage(Bitmap userProvidedImage, boolean recycleOld) {
        if(this.userProvidedImage != userProvidedImage) {
            if(recycleOld && this.userProvidedImage != null)
                this.userProvidedImage.recycle();

            this.userProvidedImage = userProvidedImage;
        }
    }

    public Bitmap getUserProvidedImage() {
        return userProvidedImage;
    }

    @Override
    public void loadFromPreferences(SharedPreferences preferences) {
        keepAspectRatio.set(preferences.getBoolean(KEY_KEEP_ASPECT_RATIO, keepAspectRatio.get()));
    }

    @Override
    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putBoolean(KEY_KEEP_ASPECT_RATIO, keepAspectRatio.get());
    }

    @Override
    public void loadFromBundle(Bundle bundle) {
        keepAspectRatio.set(bundle.getBoolean(KEY_KEEP_ASPECT_RATIO, keepAspectRatio.get()));
    }

    @Override
    public void storeToBundle(Bundle bundle) {
        bundle.putBoolean(KEY_KEEP_ASPECT_RATIO, keepAspectRatio.get());
    }

    @Override
    public void drawImageToBitmap(Bitmap bitmap) {
        drawUserProvidedImageToBitmap(bitmap, userProvidedImage, keepAspectRatio.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProvidedImageCreator that = (UserProvidedImageCreator) o;

        return keepAspectRatio.get() == that.keepAspectRatio.get();
    }

    @Override
    public int hashCode() {
        return keepAspectRatio.hashCode();
    }

    private static void drawUserProvidedImageToBitmap(Bitmap image, Bitmap userProvidedImage, boolean keepAspectRatio) {
        if(userProvidedImage == null || userProvidedImage.getWidth() <= 0 || userProvidedImage.getHeight() <= 0)
            return;

        int destinationWidth = image.getWidth();
        int destinationHeight = image.getHeight();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        drawUserProvidedImageToCanvas(canvas, destinationWidth, destinationHeight, userProvidedImage, keepAspectRatio, paint);
    }

    private static void drawUserProvidedImageToCanvas(Canvas canvas, int destinationWidth, int destinationHeight, Bitmap userProvidedImage, boolean keepAspectRatio, Paint paint) {
        int originalWidth = userProvidedImage.getWidth();
        int originalHeight = userProvidedImage.getHeight();

        float scalingX, scalingY;

        if(keepAspectRatio) {
            float requiredScalingX = ((float) destinationWidth) / originalWidth;
            float requiredScalingY = ((float) destinationHeight) / originalHeight;
            scalingX = scalingY = Math.max(requiredScalingX, requiredScalingY);
        } else {
            scalingX = ((float) destinationWidth) / originalWidth;
            scalingY = ((float) destinationHeight) / originalHeight;
        }

        float drawingWidth = scalingX * originalWidth,
                drawingHeight = scalingY * originalHeight;

        float x = (destinationWidth - drawingWidth) / 2f,
                y = (destinationWidth - drawingHeight) / 2f;

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        canvas.save();
        canvas.translate(x, y);
        canvas.scale(scalingX, scalingY);
        canvas.drawBitmap(userProvidedImage, 0, 0, paint);
        canvas.restore();
    }
}
