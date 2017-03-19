package de.lennartmeinhardt.android.moiree.imaging;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public interface MoireeImageCreator extends BundleIO {

    Drawable createMoireeImageForDimensions(Resources resources, int width, int height);

    void loadFromPreferences(SharedPreferences preferences);

    void storeToPreferences(SharedPreferences.Editor preferencesEditor);

}
