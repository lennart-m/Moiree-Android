package de.lennartmeinhardt.android.moiree.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ChangeScroll extends Transition {

    private static final String PROPNAME_SCROLL_X = "de.lennartmeinhardt.android.moiree:changeScroll:scrollX";
    private static final String PROPNAME_SCROLL_Y = "de.lennartmeinhardt.android.moiree:changeScroll:scrollY";

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureValues(@NonNull TransitionValues transitionValues) {
        int scrollX = transitionValues.view.getScrollX();
        int scrollY = transitionValues.view.getScrollY();
        transitionValues.values.put(PROPNAME_SCROLL_X, scrollX);
        transitionValues.values.put(PROPNAME_SCROLL_Y, scrollY);
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        if(startValues == null || endValues == null)
            return null;

        int startScrollX = (int) startValues.values.get(PROPNAME_SCROLL_X);
        int endScrollX = (int) endValues.values.get(PROPNAME_SCROLL_X);
        int startScrollY = (int) startValues.values.get(PROPNAME_SCROLL_Y);
        int endScrollY = (int) endValues.values.get(PROPNAME_SCROLL_Y);

        List<Animator> animators = new ArrayList<>();
        if(startScrollX != endScrollX) {
            endValues.view.setScrollX(startScrollX);
            animators.add(ObjectAnimator.ofInt(endValues.view, "scrollX", startScrollX, endScrollX));
        };
        if(startScrollY != endScrollY) {
            endValues.view.setScrollY(startScrollY);
            animators.add(ObjectAnimator.ofInt(endValues.view, "scrollY", startScrollY, endScrollY));
        };

        if(! animators.isEmpty()) {
            AnimatorSet bothScrollAnimators = new AnimatorSet();
            bothScrollAnimators.playTogether(animators);
            return bothScrollAnimators;
        } else
            return null;
    }
}
