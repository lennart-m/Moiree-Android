package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import java.util.Random;

import de.lennartmeinhardt.android.moiree.R;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;

public class RandomPixelsImageCreator extends BaseBitmapMoireeImageCreator <RescaledDrawable> {

	private static final String KEY_SQ_SIZE_IN_PX = "randomPixelsImageCreator:squareSizeInPixels";
	private static final String KEY_DENSITY = "randomPixelsImageCreator:density";

	private int squareSizeInPixels;
	private float density;


	public RandomPixelsImageCreator() {
		this(1, .5f);
	}

	public RandomPixelsImageCreator(Resources resources) {
		this(resources.getDimensionPixelSize(R.dimen.random_pixels_image_default_square_size),
				resources.getInteger(R.integer.random_pixels_image_default_density_percents) / 100f);
	}

	public RandomPixelsImageCreator(int squareSizeInPixels) {
		this(squareSizeInPixels, .5f);
	}

	public RandomPixelsImageCreator(float density) {
		this(1, density);
	}

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
	public void storeToBundle(Bundle bundle, String prefix) {
		bundle.putInt(prefix + KEY_SQ_SIZE_IN_PX, squareSizeInPixels);
		bundle.putFloat(prefix + KEY_DENSITY, density);
	}

	@Override
	public void loadFromBundle(Bundle bundle, String prefix) {
		this.squareSizeInPixels = bundle.getInt(prefix + KEY_SQ_SIZE_IN_PX, squareSizeInPixels);
		this.density = bundle.getFloat(prefix + KEY_DENSITY, density);
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

	private static void drawRandomPixelsToImage(Bitmap image, float density) {
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
