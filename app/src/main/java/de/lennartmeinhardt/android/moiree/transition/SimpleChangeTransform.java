package de.lennartmeinhardt.android.moiree.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SimpleChangeTransform extends Transition {

    private static final String PROPNAME_TRANSLATION_X = "de.lennartmeinhardt.android.moiree:simpleChangeTransform:translationX";
    private static final String PROPNAME_TRANSLATION_Y = "de.lennartmeinhardt.android.moiree:simpleChangeTransform:translationY";
    private static final String PROPNAME_SCALING_X = "de.lennartmeinhardt.android.moiree:simpleChangeTransform:scalingX";
    private static final String PROPNAME_SCALING_Y = "de.lennartmeinhardt.android.moiree:simpleChangeTransform:scalingY";
    private static final String PROPNAME_ROTATION = "de.lennartmeinhardt.android.moiree:simpleChangeTransform:rotation";


    private static final Property<View, Float> ROTATION_PROPERTY = new Property<View, Float>(Float.class, "rotation") {
        @Override
        public Float get(View object) {
            return object.getRotation();
        }

        @Override
        public void set(View object, Float value) {
            object.setRotation(value);
        }
    };

    private static final Property<View, Float> SCALE_X_PROPERTY = new Property<View, Float>(Float.class, "scaleX") {
        @Override
        public Float get(View object) {
            return object.getScaleX();
        }

        @Override
        public void set(View object, Float value) {
            object.setScaleX(value);
        }
    };

    private static final Property<View, Float> SCALE_Y_PROPERTY = new Property<View, Float>(Float.class, "scaleY") {
        @Override
        public Float get(View object) {
            return object.getScaleY();
        }

        @Override
        public void set(View object, Float value) {
            object.setScaleY(value);
        }
    };

    private static final Property<View, Float> TRANSLATION_X_PROPERTY = new Property<View, Float>(Float.class, "translationX") {
        @Override
        public Float get(View object) {
            return object.getTranslationX();
        }

        @Override
        public void set(View object, Float value) {
            object.setTranslationX(value);
        }
    };

    private static final Property<View, Float> TRANSLATION_Y_PROPERTY = new Property<View, Float>(Float.class, "translationY") {
        @Override
        public Float get(View object) {
            return object.getTranslationY();
        }

        @Override
        public void set(View object, Float value) {
            object.setTranslationY(value);
        }
    };


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
        transitionValues.values.put(PROPNAME_ROTATION, view.getRotation());
        transitionValues.values.put(PROPNAME_SCALING_X, view.getScaleX());
        transitionValues.values.put(PROPNAME_SCALING_Y, view.getScaleY());
        transitionValues.values.put(PROPNAME_TRANSLATION_X, view.getTranslationX());
        transitionValues.values.put(PROPNAME_TRANSLATION_Y, view.getTranslationY());
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        if(startValues == null || endValues == null)
            return null;

        List<Animator> allAnimators = new ArrayList<>();

        addAnimatorIfNonNull(allAnimators,
                createFloatAnimatorIfPresent(PROPNAME_ROTATION, ROTATION_PROPERTY, startValues, endValues));
        addAnimatorIfNonNull(allAnimators,
                createFloatAnimatorIfPresent(PROPNAME_SCALING_X, SCALE_X_PROPERTY, startValues, endValues));
        addAnimatorIfNonNull(allAnimators,
                createFloatAnimatorIfPresent(PROPNAME_SCALING_Y, SCALE_Y_PROPERTY, startValues, endValues));
        addAnimatorIfNonNull(allAnimators,
                createFloatAnimatorIfPresent(PROPNAME_TRANSLATION_X, TRANSLATION_X_PROPERTY, startValues, endValues));
        addAnimatorIfNonNull(allAnimators,
                createFloatAnimatorIfPresent(PROPNAME_TRANSLATION_Y, TRANSLATION_Y_PROPERTY, startValues, endValues));

        if(! allAnimators.isEmpty()) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(allAnimators);
            return set;
        } else
            return null;
    }

    private static void addAnimatorIfNonNull(List<Animator> animators, Animator animator) {
        if(animator != null)
            animators.add(animator);
    }

    @Nullable
    private static Animator createFloatAnimatorIfPresent(@NonNull String transitionPropName, @NonNull final Property<View, Float> objectProperty, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
        if(startValues == null || endValues == null)
            return null;

        final float startValue = (float) startValues.values.get(transitionPropName);
        final float endValue = (float) endValues.values.get(transitionPropName);

        if(startValue != endValue) {
            final View view = endValues.view;
            objectProperty.set(view, startValue);
            return ObjectAnimator.ofFloat(view, objectProperty, startValue, endValue);
        } else
            return null;
    }
}
