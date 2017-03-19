package de.lennartmeinhardt.android.moiree.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ChangeAlpha extends Transition {

    private static final String PROPNAME_ALPHA = "de.lennartmeinhardt.android.moiree:changeAlpha:alpha";

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureValues(@NonNull TransitionValues transitionValues) {
        float alpha = transitionValues.view.getAlpha();
        transitionValues.values.put(PROPNAME_ALPHA, alpha);
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        if(startValues == null || endValues == null)
            return null;

        float startAlpha = (float) startValues.values.get(PROPNAME_ALPHA);
        float endAlpha = (float) endValues.values.get(PROPNAME_ALPHA);

        if(startAlpha != endAlpha) {
            endValues.view.setAlpha(startAlpha);
            return ObjectAnimator.ofFloat(endValues.view, "alpha", startAlpha, endAlpha);
        } else
            return null;
    }
}
