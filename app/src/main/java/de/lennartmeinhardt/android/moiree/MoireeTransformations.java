package de.lennartmeinhardt.android.moiree;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

public class MoireeTransformations {
	
	public static void copyTransformationValues(MoireeTransformation source, MoireeTransformation destination) {
		destination.setUseCommonScaling(source.isUseCommonScaling());
		destination.setCommonScaling(source.getCommonScaling());
		destination.setScalingX(source.getScalingX());
		destination.setScalingY(source.getScalingY());
		
		destination.setRotation(source.getRotation());
		
		destination.setTranslationX(source.getTranslationX());
		destination.setTranslationY(source.getTranslationY());
	}
	
	public static void copyTransformationValuesAnimated(MoireeTransformation source, final MoireeTransformation destination) {
		ArrayList<Animator> animators = new ArrayList<>();

		animators.add(ObjectAnimator.ofFloat(destination, "rotation", source.getRotation()));
		
		animators.add(ObjectAnimator.ofFloat(destination, "translationX", source.getTranslationX()));
		animators.add(ObjectAnimator.ofFloat(destination, "translationY", source.getTranslationY()));

		animators.add(ObjectAnimator.ofFloat(destination, "commonScaling", source.getCommonScaling()));
		animators.add(ObjectAnimator.ofFloat(destination, "scalingX", source.getScalingX()));
		animators.add(ObjectAnimator.ofFloat(destination, "scalingY", source.getScalingY()));

		final boolean targetUseCommonScaling = source.isUseCommonScaling();

		final AnimatorSet transformationAnimator = new AnimatorSet();
		transformationAnimator.playTogether(animators);
		transformationAnimator.addListener(new AnimatorListenerAdapter() {
			private boolean canceled = false;

			@Override
			public void onAnimationCancel(Animator animation) {
				canceled = true;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if(! canceled)
					destination.setUseCommonScaling(targetUseCommonScaling);
			}
		});
		transformationAnimator.start();
	}
}
