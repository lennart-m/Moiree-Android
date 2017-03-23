package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.transition.BackgroundRecolor;
import de.lennartmeinhardt.android.moiree.transition.SimpleChangeTransform;
import de.lennartmeinhardt.android.moiree.transition.TagColorFilterRecolor;

public class MoireeTransitionStarter {

    private static final String KEY_COLOR_ENABLED = "moireeTransitionStarter:colorTransitionEnabled";
    private static final String KEY_COLOR_DURATION = "moireeTransitionStarter:colorTransitionDuration";
    private static final String KEY_TRANSFORMATION_ENABLED = "moireeTransitionStarter:transformationTransitionEnabled";
    private static final String KEY_TRANSFORMATION_DURATION = "moireeTransitionStarter:transformationTransitionDuration";

    private boolean colorTransitionEnabled;
    private long colorTransitionDuration;

    private boolean transformationTransitionEnabled;
    private long transformationTransitionDuration;

    private final ViewGroup moireeViewGroup;


    public MoireeTransitionStarter(ViewGroup moireeViewGroup) {
        this.moireeViewGroup = moireeViewGroup;
    }

    public void beginColorTransitionIfWanted() {
        if(colorTransitionEnabled) {
            Transition foregroundRecolorTransition = new TagColorFilterRecolor(PorterDuff.Mode.SRC_ATOP)
                    .addTarget(R.id.moiree_image_fixed)
                    .addTarget(R.id.moiree_image_transformed);
            Transition backgroundRecolorTransition = new BackgroundRecolor().addTarget(R.id.moiree_background).setDuration(colorTransitionDuration);
            Transition transition = new TransitionSet()
                    .addTransition(backgroundRecolorTransition)
                    .addTransition(foregroundRecolorTransition)
                    .setOrdering(TransitionSet.ORDERING_TOGETHER)
                    .setDuration(colorTransitionDuration);

            TransitionManager.beginDelayedTransition(moireeViewGroup, transition);
        }
    }

    public void beginTransformationTransitionIfWanted() {
        beginTransformationTransitionIfWanted(createTransformationTransition());
    }

    public void beginTransformationTransitionIfWanted(Transition transition) {
        if(transformationTransitionEnabled)
            TransitionManager.beginDelayedTransition(moireeViewGroup, transition);
    }

    public Transition createTransformationTransition() {
        return new SimpleChangeTransform().addTarget(R.id.moiree_image_transformed).setDuration(transformationTransitionDuration);
    }

    public boolean isColorTransitionEnabled() {
        return colorTransitionEnabled;
    }
    public void setColorTransitionEnabled(boolean colorTransitionEnabled) {
        this.colorTransitionEnabled = colorTransitionEnabled;
    }

    public long getColorTransitionDuration() {
        return colorTransitionDuration;
    }
    public void setColorTransitionDuration(long colorTransitionDuration) {
        this.colorTransitionDuration = colorTransitionDuration;
    }

    public boolean isTransformationTransitionEnabled() {
        return transformationTransitionEnabled;
    }
    public void setTransformationTransitionEnabled(boolean transformationTransitionEnabled) {
        this.transformationTransitionEnabled = transformationTransitionEnabled;
    }

    public long getTransformationTransitionDuration() {
        return transformationTransitionDuration;
    }
    public void setTransformationTransitionDuration(long transformationTransitionDuration) {
        this.transformationTransitionDuration = transformationTransitionDuration;
    }


    public void loadFromPreferences(SharedPreferences preferences) {
        colorTransitionEnabled = preferences.getBoolean(KEY_COLOR_ENABLED, colorTransitionEnabled);
        colorTransitionDuration = preferences.getLong(KEY_COLOR_DURATION, colorTransitionDuration);

        transformationTransitionEnabled = preferences.getBoolean(KEY_TRANSFORMATION_ENABLED, transformationTransitionEnabled);
        transformationTransitionDuration = preferences.getLong(KEY_TRANSFORMATION_DURATION, transformationTransitionDuration);
    }

    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putBoolean(KEY_COLOR_ENABLED, colorTransitionEnabled);
        preferencesEditor.putLong(KEY_COLOR_DURATION, colorTransitionDuration);

        preferencesEditor.putBoolean(KEY_TRANSFORMATION_ENABLED, transformationTransitionEnabled);
        preferencesEditor.putLong(KEY_TRANSFORMATION_DURATION, transformationTransitionDuration);
    }
}
