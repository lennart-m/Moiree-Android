package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class HorizontalLinesImageCreator extends BaseBitmapMoireeImageCreator <RescaledDrawable> implements BundleIO, PreferenceIO {

	private static final String KEY_LINE_THICKNESS_IN_PX = "horizontalLinesImageCreator:lineThicknessInPixels";

	private int lineThicknessInPixels;


	public HorizontalLinesImageCreator(int lineThicknessInPixels) {
		this.lineThicknessInPixels = lineThicknessInPixels;
	}

	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels);
	}

	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		this.lineThicknessInPixels = preferences.getInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels);
	}

	@Override
	public void storeToBundle(Bundle bundle) {
		bundle.putInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels);
	}

	@Override
	public void loadFromBundle(Bundle bundle) {
		this.lineThicknessInPixels = bundle.getInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels);
	}

	@Override
	public Bitmap createBitmapForDimensions(int width, int height) {
		int imageWidth = (width + lineThicknessInPixels - 1) / lineThicknessInPixels;
		int imageHeight = (height + lineThicknessInPixels - 1) / lineThicknessInPixels;
		Bitmap bitmap = createEmptyBitmap(imageWidth, imageHeight);
		drawHorizontalLinesToImage(bitmap);
		return bitmap;
	}

	private static void drawHorizontalLinesToImage(Bitmap bitmap) {
		int w = bitmap.getWidth(), h = bitmap.getHeight();
		for(int y = 0; y < h; y++) {
			int argb = y % 2 == 0 ? Color.BLACK : Color.TRANSPARENT;
			for(int x = 0; x < w; x++)
				bitmap.setPixel(x, y, argb);
		}
	}

	@Override
	public RescaledDrawable createDrawableFromBitmap(Resources resources, Bitmap bitmap) {
		BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
		drawable.setAntiAlias(false);
		drawable.setFilterBitmap(false);
		drawable.setDither(false);
		return new RescaledDrawable(drawable, lineThicknessInPixels);
	}

	@Override
	public Bitmap getBitmapFromDrawable(RescaledDrawable drawable) {
		return ((BitmapDrawable) drawable.getWrappedDrawable()).getBitmap();
	}

	public int getLineThicknessInPixels() {
		return lineThicknessInPixels;
	}
	public void setLineThicknessInPixels(int lineThicknessInPixels) {
		this.lineThicknessInPixels = lineThicknessInPixels;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HorizontalLinesImageCreator that = (HorizontalLinesImageCreator) o;

		return lineThicknessInPixels == that.lineThicknessInPixels;

	}

	@Override
	public int hashCode() {
		return lineThicknessInPixels;
	}
}
