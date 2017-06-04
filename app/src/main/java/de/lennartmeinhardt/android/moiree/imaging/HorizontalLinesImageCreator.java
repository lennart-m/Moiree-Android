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

public class HorizontalLinesImageCreator extends BaseBitmapMoireeImageCreator<BitmapDrawable> implements BundleIO, PreferenceIO {

	private static final String KEY_LINE_THICKNESS_IN_PX = "horizontalLinesImageCreator:lineThicknessInPixels";

	public final ObservableInt lineThicknessInPixels = new ObservableInt();


	public HorizontalLinesImageCreator() {
	}

	public HorizontalLinesImageCreator(int lineThicknessInPixels) {
		this.lineThicknessInPixels.set(lineThicknessInPixels);
	}


	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels.get());
	}

	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		this.lineThicknessInPixels.set(preferences.getInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels.get()));
	}

	@Override
	public void storeToBundle(Bundle bundle) {
		bundle.putInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels.get());
	}

	@Override
	public void loadFromBundle(Bundle bundle) {
		this.lineThicknessInPixels.set(bundle.getInt(KEY_LINE_THICKNESS_IN_PX, lineThicknessInPixels.get()));
	}

	@Override
	public Bitmap createBitmapForDimensions(int width, int height) {
		Bitmap bitmap = createEmptyBitmap(width, height);
		drawHorizontalLinesToImage(bitmap, lineThicknessInPixels.get());
		return bitmap;
	}

	private static void drawHorizontalLinesToImage(Bitmap bitmap, int lineThicknessInPixels) {
		int w = bitmap.getWidth(), h = bitmap.getHeight();
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		Paint fillBlack = new Paint();

		final int segmentsPerImageHalf = (int) Math.ceil((h / 2f) / lineThicknessInPixels);
		for(int j = - segmentsPerImageHalf; j < segmentsPerImageHalf; j++) {
			if(j % 2 == 0) {
				float y = h / 2 + j * lineThicknessInPixels;
				canvas.drawRect(0, y, w, y + lineThicknessInPixels, fillBlack);
			}
		}
	}

	/*
	 * extremely slow when compared to the canvas method
	 */
	private static void drawHorizontalLinesToImage(Bitmap bitmap) {
		int w = bitmap.getWidth(), h = bitmap.getHeight();
		for(int y = 0; y < h; y++) {
			int argb = y % 2 == 0 ? Color.BLACK : Color.TRANSPARENT;
			for(int x = 0; x < w; x++)
				bitmap.setPixel(x, y, argb);
		}
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

		HorizontalLinesImageCreator that = (HorizontalLinesImageCreator) o;

		return lineThicknessInPixels.get() == that.lineThicknessInPixels.get();

	}

	@Override
	public int hashCode() {
		return lineThicknessInPixels.get();
	}
}
