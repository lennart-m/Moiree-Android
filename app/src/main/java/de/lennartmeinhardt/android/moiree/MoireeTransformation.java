package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableFloat;
import android.os.Bundle;

import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MoireeTransformation extends BaseObservable implements PreferenceIO {

	private static final String KEY_ROTATION = "moireeTransformation:rotation";
	private static final String KEY_TRANSLATION_X = "moireeTransformation:translationX";
	private static final String KEY_TRANSLATION_Y = "moireeTransformation:translationY";
	private static final String KEY_COMMON_SCALING = "moireeTransformation:commonScaling";
	private static final String KEY_SCALING_X = "moireeTransformation:scalingX";
	private static final String KEY_SCALING_Y = "moireeTransformation:scalingY";
	private static final String KEY_USE_COMMON_SCALING = "moireeTransformation:useCommonScaling";

	private static final float ID_ROTATION = 0;
	private static final float ID_TRANSLATION_X = 0;
	private static final float ID_TRANSLATION_Y = 0;
	private static final float ID_SCALING_X = 1;
	private static final float ID_SCALING_Y = 1;
	private static final float ID_COMMON_SCALING = 1;
	private static final boolean ID_USE_COMMON_SCALING = true;

	public final ObservableFloat rotation = new ObservableFloat();
	public final ObservableFloat commonScaling = new ObservableFloat();
	public final ObservableFloat scalingX = new ObservableFloat();
	public final ObservableFloat scalingY = new ObservableFloat();
	public final ObservableFloat translationX = new ObservableFloat();
	public final ObservableFloat translationY = new ObservableFloat();
	public final ObservableBoolean useCommonScaling = new ObservableBoolean();


	{
		Observable.OnPropertyChangedCallback effectiveScalingUpdater = new Observable.OnPropertyChangedCallback() {
			@Override
			public void onPropertyChanged(Observable observable, int i) {
				notifyPropertyChanged(BR.effectiveScalingX);
				notifyPropertyChanged(BR.effectiveScalingY);
			}
		};
		commonScaling.addOnPropertyChangedCallback(effectiveScalingUpdater);
		scalingX.addOnPropertyChangedCallback(effectiveScalingUpdater);
		scalingY.addOnPropertyChangedCallback(effectiveScalingUpdater);
		useCommonScaling.addOnPropertyChangedCallback(effectiveScalingUpdater);
	}


	@Bindable
	public float getEffectiveScalingX() {
		return useCommonScaling.get() ? commonScaling.get() : scalingX.get();
	}

	@Bindable
	public float getEffectiveScalingY() {
		return useCommonScaling.get() ? commonScaling.get() : scalingY.get();
	}


	public void setToIdentity() {
		setCommonScalingToIdentity();
		setScalingXToIdentity();
		setScalingYToIdentity();
//		setUseCommonScalingToIdentity();
		setRotationToIdentity();
		setTranslationXToIdentity();
		setTranslationYToIdentity();
	}

	public void setRotationToIdentity() {
		rotation.set(ID_ROTATION);
	}
	public void setScalingXToIdentity() {
		scalingX.set(ID_SCALING_X);
	}
	public void setScalingYToIdentity() {
		scalingY.set(ID_SCALING_Y);
	}
	public void setCommonScalingToIdentity() {
		commonScaling.set(ID_COMMON_SCALING);
	}
	public void setUseCommonScalingToIdentity() {
		useCommonScaling.set(ID_USE_COMMON_SCALING);
	}
	public void setTranslationXToIdentity() {
		translationX.set(ID_TRANSLATION_X);
	}
	public void setTranslationYToIdentity() {
		translationY.set(ID_TRANSLATION_Y);
	}


	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		rotation.set(preferences.getFloat(KEY_ROTATION, rotation.get()));
		commonScaling.set(preferences.getFloat(KEY_COMMON_SCALING, commonScaling.get()));
		scalingX.set(preferences.getFloat(KEY_SCALING_X, scalingX.get()));
		scalingY.set(preferences.getFloat(KEY_SCALING_Y, scalingY.get()));
		translationX.set(preferences.getFloat(KEY_TRANSLATION_X, translationX.get()));
		translationY.set(preferences.getFloat(KEY_TRANSLATION_Y, translationY.get()));
		useCommonScaling.set(preferences.getBoolean(KEY_USE_COMMON_SCALING, useCommonScaling.get()));
	}

	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putFloat(KEY_ROTATION, rotation.get());
		preferencesEditor.putFloat(KEY_TRANSLATION_X, translationX.get());
		preferencesEditor.putFloat(KEY_TRANSLATION_Y, translationY.get());
		preferencesEditor.putFloat(KEY_COMMON_SCALING, commonScaling.get());
		preferencesEditor.putFloat(KEY_SCALING_X, scalingX.get());
		preferencesEditor.putFloat(KEY_SCALING_Y, scalingY.get());
		preferencesEditor.putBoolean(KEY_USE_COMMON_SCALING, useCommonScaling.get());
	}


	public void loadDefaultValuesFromResources(Resources resources) {
		float defaultRotation = 1f * resources.getInteger(R.integer.default_transformation_rotation_degrees);
		rotation.set(defaultRotation);

		int defaultCommonScalingPercents = resources.getInteger(R.integer.default_transformation_common_scaling_percents);
		int defaultScalingXPercents = resources.getInteger(R.integer.default_transformation_scaling_x_percents);
		int defaultScalingYPercents = resources.getInteger(R.integer.default_transformation_scaling_y_percents);
		float defaultCommonScaling = defaultCommonScalingPercents / 100f;
		float defaultScalingX = defaultScalingXPercents / 100f;
		float defaultScalingY = defaultScalingYPercents / 100f;
		commonScaling.set(defaultCommonScaling);
		scalingX.set(defaultScalingX);
		scalingY.set(defaultScalingY);

		float defaultTranslationX = resources.getInteger(R.integer.default_transformation_translation_x_pixels);
		float defaultTranslationY = resources.getInteger(R.integer.default_transformation_translation_y_pixels);
		translationX.set(defaultTranslationX);
		translationY.set(defaultTranslationY);

		boolean defaultUseCommonScaling = resources.getBoolean(R.bool.default_transformation_use_common_scaling);
		useCommonScaling.set(defaultUseCommonScaling);
	}

	public void storeToBundle(Bundle outBundle) {
		outBundle.putFloat(KEY_ROTATION, rotation.get());
		outBundle.putFloat(KEY_TRANSLATION_X, translationX.get());
		outBundle.putFloat(KEY_TRANSLATION_Y, translationY.get());
		outBundle.putFloat(KEY_COMMON_SCALING, commonScaling.get());
		outBundle.putFloat(KEY_SCALING_X, scalingX.get());
		outBundle.putFloat(KEY_SCALING_Y, scalingY.get());
		outBundle.putBoolean(KEY_USE_COMMON_SCALING, useCommonScaling.get());
	}

	// TODO in bibliothek auslagern
	public void readFromBundle(Bundle srcBundle) {
		rotation.set(srcBundle.getFloat(KEY_ROTATION, rotation.get()));
		commonScaling.set(srcBundle.getFloat(KEY_COMMON_SCALING, commonScaling.get()));
		scalingX.set(srcBundle.getFloat(KEY_SCALING_X, scalingX.get()));
		scalingY.set(srcBundle.getFloat(KEY_SCALING_Y, scalingY.get()));
		translationX.set(srcBundle.getFloat(KEY_TRANSLATION_X, translationX.get()));
		translationY.set(srcBundle.getFloat(KEY_TRANSLATION_Y, translationY.get()));
		useCommonScaling.set(srcBundle.getBoolean(KEY_USE_COMMON_SCALING, useCommonScaling.get()));
	}


	public static MoireeTransformation createIdentity() {
		MoireeTransformation moireeTransformation = new MoireeTransformation();
		moireeTransformation.setToIdentity();
		return moireeTransformation;
	}
}
