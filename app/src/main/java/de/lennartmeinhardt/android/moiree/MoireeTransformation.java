package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MoireeTransformation implements PreferenceIO {

	private static final String KEY_ROTATION = "moireeTransformation:rotation";
	private static final String KEY_TRANSLATION_X = "moireeTransformation:translationX";
	private static final String KEY_TRANSLATION_Y = "moireeTransformation:translationY";
	private static final String KEY_COMMON_SCALING = "moireeTransformation:commonScaling";
	private static final String KEY_SCALING_X = "moireeTransformation:scalingX";
	private static final String KEY_SCALING_Y = "moireeTransformation:scalingY";
	private static final String KEY_USE_COMMON_SCALING = "moireeTransformation:useCommonScaling";

	public static final float ID_ROTATION = 0;
	public static final float ID_TRANSLATION_X = 0;
	public static final float ID_TRANSLATION_Y = 0;
	public static final float ID_SCALING_X = 1;
	public static final float ID_SCALING_Y = 1;
	public static final float ID_COMMON_SCALING = 1;
	public static final boolean ID_USE_COMMON_SCALING = true;

	private float rotation;
	private float translationX, translationY;
	private float scalingX, scalingY, commonScaling;
	private boolean useCommonScaling;

	private final List<OnTransformationChangedListener> onTransformationChangedListeners = new ArrayList<>();

	
	public float getRotation() {
		return rotation;
	}
	public void setRotation(float rotation) {
		if(this.rotation != rotation) {
			this.rotation = rotation;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onRotationChanged(rotation);
		}
	}

	public float getTranslationX() {
		return translationX;
	}
	public void setTranslationX(float translationX) {
		if(this.translationX != translationX) {
			this.translationX = translationX;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onTranslationXChanged(translationX);
		}
	}

	public float getTranslationY() {
		return translationY;
	}
	public void setTranslationY(float translationY) {
		if(this.translationY != translationY) {
			this.translationY = translationY;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onTranslationYChanged(translationY);
		}
	}

	public float getScalingX() {
		return scalingX;
	}
	public void setScalingX(float scalingX) {
		if(this.scalingX != scalingX) {
			this.scalingX = scalingX;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onScalingXChanged(scalingX);
		}
	}

	public float getScalingY() {
		return scalingY;
	}
	public void setScalingY(float scalingY) {
		if(this.scalingY != scalingY) {
			this.scalingY = scalingY;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onScalingYChanged(scalingY);
		}
	}

	public float getCommonScaling() {
		return commonScaling;
	}
	public void setCommonScaling(float commonScaling) {
		if(this.commonScaling != commonScaling) {
			this.commonScaling = commonScaling;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onCommonScalingChanged(commonScaling);
		}
	}

	public boolean isUseCommonScaling() {
		return useCommonScaling;
	}
	public void setUseCommonScaling(boolean useCommonScaling) {
		if(this.useCommonScaling != useCommonScaling) {
			this.useCommonScaling = useCommonScaling;
			for (OnTransformationChangedListener listener : onTransformationChangedListeners)
				listener.onUseCommonScalingChanged(useCommonScaling);
		}
	}
	
	public float getEffectiveScalingX() {
		return isUseCommonScaling() ? commonScaling : scalingX;
	}
	
	public float getEffectiveScalingY() {
		return isUseCommonScaling() ? commonScaling : scalingY;
	}


	public void addOnTransformationChangedListener(OnTransformationChangedListener listener) {
		onTransformationChangedListeners.add(listener);
	}
	public void addAndFireOnTransformationChangedListener(OnTransformationChangedListener listener) {
		addOnTransformationChangedListener(listener);
		listener.onRotationChanged(rotation);
		listener.onCommonScalingChanged(commonScaling);
		listener.onUseCommonScalingChanged(useCommonScaling);
		listener.onScalingXChanged(scalingX);
		listener.onScalingYChanged(scalingY);
		listener.onTranslationXChanged(translationX);
		listener.onTranslationYChanged(translationY);
	}
	public void removeOnTransformationChangedListener(OnTransformationChangedListener listener) {
		onTransformationChangedListeners.remove(listener);
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

	public void setToTransformation(MoireeTransformation transformation) {
		setCommonScaling(transformation.getCommonScaling());
		setScalingX(transformation.getScalingX());
		setScalingY(transformation.getScalingY());
		setUseCommonScaling(transformation.isUseCommonScaling());
		setRotation(transformation.getRotation());
		setTranslationX(transformation.getTranslationX());
		setTranslationY(transformation.getTranslationY());
	}

	public void setRotationToIdentity() {
		setRotation(ID_ROTATION);
	}
	public void setScalingXToIdentity() {
		setScalingX(ID_SCALING_X);
	}
	public void setScalingYToIdentity() {
		setScalingY(ID_SCALING_Y);
	}
	public void setCommonScalingToIdentity() {
		setCommonScaling(ID_COMMON_SCALING);
	}
	public void setUseCommonScalingToIdentity() {
		setUseCommonScaling(ID_USE_COMMON_SCALING);
	}
	public void setTranslationXToIdentity() {
		setTranslationX(ID_TRANSLATION_X);
	}
	public void setTranslationYToIdentity() {
		setTranslationY(ID_TRANSLATION_Y);
	}


	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		setRotation(preferences.getFloat(KEY_ROTATION, rotation));
		setCommonScaling(preferences.getFloat(KEY_COMMON_SCALING, commonScaling));
		setScalingX(preferences.getFloat(KEY_SCALING_X, scalingX));
		setScalingY(preferences.getFloat(KEY_SCALING_Y, scalingY));
		setTranslationX(preferences.getFloat(KEY_TRANSLATION_X, translationX));
		setTranslationY(preferences.getFloat(KEY_TRANSLATION_Y, translationY));
		setUseCommonScaling(preferences.getBoolean(KEY_USE_COMMON_SCALING, useCommonScaling));
	}

	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putFloat(KEY_ROTATION, rotation);
		preferencesEditor.putFloat(KEY_TRANSLATION_X, translationX);
		preferencesEditor.putFloat(KEY_TRANSLATION_Y, translationY);
		preferencesEditor.putFloat(KEY_COMMON_SCALING, commonScaling);
		preferencesEditor.putFloat(KEY_SCALING_X, scalingX);
		preferencesEditor.putFloat(KEY_SCALING_Y, scalingY);
		preferencesEditor.putBoolean(KEY_USE_COMMON_SCALING, useCommonScaling);
	}


	public interface OnTransformationChangedListener {
		void onRotationChanged(float newRotation);

		void onCommonScalingChanged(float newCommonScaling);
		void onScalingXChanged(float newScalingX);
		void onScalingYChanged(float newScalingY);
		void onUseCommonScalingChanged(boolean newUseCommonScaling);

		void onTranslationXChanged(float newTranslationX);
		void onTranslationYChanged(float newTranslationY);


		class Adapter implements OnTransformationChangedListener {
			@Override
			public void onRotationChanged(float newRotation) {
			}

			@Override
			public void onCommonScalingChanged(float newCommonScaling) {
			}

			@Override
			public void onScalingXChanged(float newScalingX) {
			}

			@Override
			public void onScalingYChanged(float newScalingY) {
			}

			@Override
			public void onUseCommonScalingChanged(boolean newUseCommonScaling) {
			}

			@Override
			public void onTranslationXChanged(float newTranslationX) {
			}

			@Override
			public void onTranslationYChanged(float newTranslationY) {
			}
		}
	}

	public static MoireeTransformation loadDefaultTransformationFromResources(Resources resources) {
		MoireeTransformation transformation = new MoireeTransformation();

		float defaultRotation = 1f * resources.getInteger(R.integer.default_transformation_rotation_degrees);
		transformation.setRotation(defaultRotation);

		int defaultCommonScalingPercents = resources.getInteger(R.integer.default_transformation_common_scaling_percents);
		int defaultScalingXPercents = resources.getInteger(R.integer.default_transformation_scaling_x_percents);
		int defaultScalingYPercents = resources.getInteger(R.integer.default_transformation_scaling_y_percents);
		float defaultCommonScaling = defaultCommonScalingPercents / 100f;
		float defaultScalingX = defaultScalingXPercents / 100f;
		float defaultScalingY = defaultScalingYPercents / 100f;
		transformation.setCommonScaling(defaultCommonScaling);
		transformation.setScalingX(defaultScalingX);
		transformation.setScalingY(defaultScalingY);

		float defaultTranslationX = resources.getInteger(R.integer.default_transformation_translation_x_pixels);
		float defaultTranslationY = resources.getInteger(R.integer.default_transformation_translation_y_pixels);
		transformation.setTranslationX(defaultTranslationX);
		transformation.setTranslationY(defaultTranslationY);

		boolean defaultUseCommonScaling = resources.getBoolean(R.bool.default_transformation_use_common_scaling);
		transformation.setUseCommonScaling(defaultUseCommonScaling);

		return transformation;
	}
}
