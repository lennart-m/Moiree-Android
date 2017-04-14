package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import java.util.Random;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;

public class RandomPixelsImageCreator extends BaseBitmapMoireeImageCreator <RescaledDrawable> implements BundleIO, PreferenceIO {

	private static final String KEY_SQ_SIZE_IN_PX = "randomPixelsImageCreator:squareSizeInPixels";
	private static final String KEY_DENSITY = "randomPixelsImageCreator:density";

	private int squareSizeInPixels;
	private float density;


	public RandomPixelsImageCreator(int squareSizeInPixels, float density) {
		this.squareSizeInPixels = squareSizeInPixels;
		this.density = density;
	}


	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels);
		preferencesEditor.putFloat(KEY_DENSITY, density);
	}

	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		this.squareSizeInPixels = preferences.getInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels);
		this.density = preferences.getFloat(KEY_DENSITY, density);
	}

	@Override
	public void storeToBundle(Bundle bundle) {
		bundle.putInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels);
		bundle.putFloat(KEY_DENSITY, density);
	}

	@Override
	public void loadFromBundle(Bundle bundle) {
		this.squareSizeInPixels = bundle.getInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels);
		this.density = bundle.getFloat(KEY_DENSITY, density);
	}

	@Override
	public Bitmap createBitmapForDimensions(int width, int height) {
		int imageWidth = (width + squareSizeInPixels - 1) / squareSizeInPixels;
		int imageHeight = (height + squareSizeInPixels - 1) / squareSizeInPixels;
		Bitmap bitmap = createEmptyBitmap(imageWidth, imageHeight);
		drawRandomPixelsToImage(bitmap, density);
		return bitmap;
	}

	@Override
	public RescaledDrawable createDrawableFromBitmap(Resources resources, Bitmap bitmap) {
		BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
		drawable.setAntiAlias(false);
		drawable.setFilterBitmap(false);
		drawable.setDither(false);
		return new RescaledDrawable(drawable, squareSizeInPixels);
	}

	@Override
	public Bitmap getBitmapFromDrawable(RescaledDrawable drawable) {
		return ((BitmapDrawable) drawable.getWrappedDrawable()).getBitmap();
	}

	public float getDensity() {
		return density;
	}
	public void setDensity(float density) {
		this.density = density;
	}

	public int getSquareSizeInPixels() {
		return squareSizeInPixels;
	}
	public void setSquareSizeInPixels(int squareSizeInPixels) {
		this.squareSizeInPixels = squareSizeInPixels;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RandomPixelsImageCreator that = (RandomPixelsImageCreator) o;

		return squareSizeInPixels == that.squareSizeInPixels && Float.compare(that.density, density) == 0;
	}

	@Override
	public int hashCode() {
		int result = squareSizeInPixels;
		result = 31 * result + (density != +0.0f ? Float.floatToIntBits(density) : 0);
		return result;
	}

	public static void drawRandomPixelsToImage(Bitmap image, float density) {
		int width = image.getWidth();
		int height = image.getHeight();

		Random random = new Random();
		int argb;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (random.nextDouble() < density)
					argb = BLACK;
				else
					argb = TRANSPARENT;

				image.setPixel(x, y, argb);
			}
		}
	}
}
