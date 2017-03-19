package de.lennartmeinhardt.android.moiree.transition;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

public class BackgroundRecolor extends Transition {

	private static final String PROPNAME_BACKGROUND_COLOR = "de.lennartmeinhardt.android:backgroundRecolor:color";

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
		captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
		captureValues(transitionValues);
    }

    private void captureValues(@NonNull TransitionValues transitionValues) {
		Drawable backgroundDrawable = transitionValues.view.getBackground();
		if(backgroundDrawable instanceof ColorDrawable)
			transitionValues.values.put(PROPNAME_BACKGROUND_COLOR, ((ColorDrawable) backgroundDrawable).getColor());
	}

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		if (startValues == null || endValues == null)
			return null;

		final View endView = endValues.view;
		int startColor = (int) startValues.values.get(PROPNAME_BACKGROUND_COLOR);
		int endColor = (int) endValues.values.get(PROPNAME_BACKGROUND_COLOR);
		if (startColor != endColor) {
			endView.setBackgroundColor(startColor);
			ObjectAnimator animator = ObjectAnimator.ofInt(endView, "backgroundColor", startColor, endColor);
			animator.setEvaluator(new ArgbEvaluator());
			return animator;
		}
		return null;
    }
}
