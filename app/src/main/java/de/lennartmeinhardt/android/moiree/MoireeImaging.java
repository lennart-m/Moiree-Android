package de.lennartmeinhardt.android.moiree;

import android.graphics.drawable.Drawable;

import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.util.ImageCreatorHolder;

public interface MoireeImaging extends ImageCreatorHolder {

    int getMoireeImageWidth();

    int getMoireeImageHeight();

    void onPreCreateMoireeImage();

    void onMoireeImageCreated(Drawable moireeImage);

    void setImageCreatorAndRecreateImage(MoireeImageCreator imageCreator);

}
