package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.ObservableFloat;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import java.util.Random;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class RandomPixelsImageCreator extends BaseBitmapMoireeImageCreator<BitmapDrawable> implements BundleIO, PreferenceIO {

	private static final String KEY_SQ_SIZE_IN_PX = "randomPixelsImageCreator:squareSizeInPixels";
	private static final String KEY_DENSITY = "randomPixelsImageCreator:density";

	public final ObservableInt squareSizeInPixels = new ObservableInt();
	public final ObservableFloat density = new ObservableFloat();


	public RandomPixelsImageCreator() {
	}

	public RandomPixelsImageCreator(int squareSizeInPixels, float density) {
		this.squareSizeInPixels.set(squareSizeInPixels);
		this.density.set(density);
	}


	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels.get());
		preferencesEditor.putFloat(KEY_DENSITY, density.get());
	}

	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		this.squareSizeInPixels.set(preferences.getInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels.get()));
		this.density.set(preferences.getFloat(KEY_DENSITY, density.get()));
	}

	@Override
	public void storeToBundle(Bundle bundle) {
		bundle.putInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels.get());
		bundle.putFloat(KEY_DENSITY, density.get());
	}

	@Override
	public void loadFromBundle(Bundle bundle) {
		this.squareSizeInPixels.set(bundle.getInt(KEY_SQ_SIZE_IN_PX, squareSizeInPixels.get()));
		this.density.set(bundle.getFloat(KEY_DENSITY, density.get()));
	}

	@Override
	public Bitmap createBitmapForDimensions(int width, int height) {
		Bitmap bitmap = createEmptyBitmap(width, height);
		drawRandomPixelsToImage(bitmap, squareSizeInPixels.get(), density.get());
		return bitmap;
	}

	@Override
	public BitmapDrawable createDrawableFromBitmap(Resources resources, Bitmap bitmap) {
		BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
		drawable.setAntiAlias(false);
		drawable.setFilterBitmap(false);
		drawable.setDither(false);
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

		RandomPixelsImageCreator that = (RandomPixelsImageCreator) o;

		return squareSizeInPixels.get() == that.squareSizeInPixels.get() && Float.compare(that.density.get(), density.get()) == 0;
	}

	@Override
	public int hashCode() {
		int result = squareSizeInPixels.get();
		result = 31 * result + (density.get() != +0.0f ? Float.floatToIntBits(density.get()) : 0);
		return result;
	}

	public static void drawRandomPixelsToImage(Bitmap image, int squareSizeInPixels, float density) {
		int width = image.getWidth();
		int height = image.getHeight();

		final int segmentsPerImageHalfX = (int) Math.ceil((width / 2f) / squareSizeInPixels);
		final int segmentsPerImageHalfY = (int) Math.ceil((height / 2f) / squareSizeInPixels);

		Canvas canvas = new Canvas(image);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		Paint fillBlack = new Paint();

		Random random = new Random();
		for (int i = - segmentsPerImageHalfX; i < segmentsPerImageHalfX; i++) {
			final float x = width / 2f + i * squareSizeInPixels;
			for (int j = -segmentsPerImageHalfY; j < segmentsPerImageHalfY; j++) {
				final float y = height / 2f + j * squareSizeInPixels;
				if (random.nextDouble() < density)
					canvas.drawRect(x, y, x + squareSizeInPixels, y + squareSizeInPixels, fillBlack);
			}
		}
	}
}
