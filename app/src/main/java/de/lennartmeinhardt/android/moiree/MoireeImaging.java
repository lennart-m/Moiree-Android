package de.lennartmeinhardt.android.moiree;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.util.ImageCreatorHolder;

public interface MoireeImaging extends ImageCreatorHolder {

    int getMoireeImageWidth();

    int getMoireeImageHeight();

    void onMoireeImageCreated(Drawable moireeImage);

    void onBeforeCreatingMoireeImage();

    void setImageCreatorAndRecreateImage(MoireeImageCreator imageCreator);

    void drawMoireeImageToCanvas(Canvas canvas);

}
