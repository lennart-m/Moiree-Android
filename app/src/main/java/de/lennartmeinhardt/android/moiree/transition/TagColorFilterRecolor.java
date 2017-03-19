package de.lennartmeinhardt.android.moiree.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TagColorFilterRecolor extends Transition {

	private static final String PROPNAME_COLORFILTER_COLOR = "de.lennartmeinhardt.android:colorFilterRecolor:color";

	private final Property<ImageView, Integer> colorFilterPropety = new Property<ImageView, Integer>(int.class, "color") {
		@Override
		public Integer get(ImageView object) {
			return ColorFilterHelper.getColorTag(object);
		}

		@Override
		public void set(ImageView object, Integer value) {
			ColorFilterHelper.setColorFilterAndTag(object, value, modeToSet);
		}
	};

	private final PorterDuff.Mode modeToSet;

	public TagColorFilterRecolor(PorterDuff.Mode modeToSet) {
		this.modeToSet = modeToSet;
	}

	@Override
	public void captureStartValues(@NonNull TransitionValues transitionValues) {
		captureValues(transitionValues);
	}

	@Override
	public void captureEndValues(@NonNull TransitionValues transitionValues) {
		captureValues(transitionValues);
	}

	private void captureValues(@NonNull TransitionValues transitionValues) {
		View view = transitionValues.view;
		if(view instanceof ImageView && ColorFilterHelper.hasColorTag(view)) {
			int color = ColorFilterHelper.getColorTag(view);
			transitionValues.values.put(PROPNAME_COLORFILTER_COLOR, color);
		}
	}

	@Nullable
	@Override
	public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
		if (startValues == null || endValues == null) {
			return null;
		}

		final View endView = endValues.view;
		if (endView instanceof ImageView) {
			int startColor = (int) startValues.values.get(PROPNAME_COLORFILTER_COLOR);
			final int endColor = (int) endValues.values.get(PROPNAME_COLORFILTER_COLOR);

			if (startColor != endColor) {
				ColorFilterHelper.setColorFilterAndTag((ImageView) endView, startColor, modeToSet);

				Animator animator = ObjectAnimator.ofObject((ImageView) endView, colorFilterPropety, new ArgbEvaluator(), startColor, endColor);
				animator.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationCancel(Animator animation) {
						ColorFilterHelper.setColorFilterAndTag((ImageView) endView, endColor, modeToSet);
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						ColorFilterHelper.setColorFilterAndTag((ImageView) endView, endColor, modeToSet);
					}
				});
				return animator;
			}
		}
		return null;
	}
}
