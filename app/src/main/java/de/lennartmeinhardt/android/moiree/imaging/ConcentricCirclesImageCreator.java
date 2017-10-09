package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class ConcentricCirclesImageCreator extends BaseBitmapMoireeImageCreator implements BundleIO, PreferenceIO {

	private static final String KEY_LINE_THICKNESS_IN_PX = "concentricCirclesLinesImageCreator:lineThicknessInPixels";

	public final ObservableInt lineThicknessInPixels = new ObservableInt();


	public ConcentricCirclesImageCreator() {
	}

	public ConcentricCirclesImageCreator(int lineThicknessInPixels) {
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
	public void drawImageToBitmap(Bitmap bitmap) {
		drawHorizontalLinesToImage(bitmap, lineThicknessInPixels.get());
	}

	private static void drawHorizontalLinesToImage(Bitmap bitmap, int lineThicknessInPixels) {
		int w = bitmap.getWidth(), h = bitmap.getHeight();
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		Paint strokeBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
		strokeBlack.setStyle(Paint.Style.STROKE);
		strokeBlack.setStrokeWidth(lineThicknessInPixels);

		final double diagonalSize = Math.sqrt(w * w + h * h);
		final int totalCirclesCount = (int) Math.ceil(diagonalSize / (2 * lineThicknessInPixels));
		for(int i = 0; i < totalCirclesCount; i++) {
			// draw only every second circle
			if(i % 2 == 0) {
				float radius = i * lineThicknessInPixels;
				canvas.drawCircle(w / 2f, h / 2f, radius, strokeBlack);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConcentricCirclesImageCreator that = (ConcentricCirclesImageCreator) o;

		return lineThicknessInPixels.get() == that.lineThicknessInPixels.get();

	}

	@Override
	public int hashCode() {
		return lineThicknessInPixels.get();
	}
}
