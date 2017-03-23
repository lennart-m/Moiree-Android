package de.lennartmeinhardt.android.moiree.imaging;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public interface MoireeImageCreator {

    Drawable createMoireeImageForDimensions(Resources resources, int width, int height);
}
