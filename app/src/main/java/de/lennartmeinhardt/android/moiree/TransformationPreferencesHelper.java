package de.lennartmeinhardt.android.moiree;

import android.content.Context;
import android.content.SharedPreferences;

public class TransformationPreferencesHelper {

	private static final String KEY_ROTATION = "rotation";
	private static final String KEY_TRANSLATION_X = "translationX";
	private static final String KEY_TRANSLATION_Y = "translationY";
	private static final String KEY_SCALING_X = "scalingX";
	private static final String KEY_SCALING_Y = "scalingY";
	private static final String KEY_COMMON_SCALING = "commonScaling";
	private static final String KEY_USE_COMMON_SCALING = "useCommonScaling";
	
	private static final float DEF_ROTATION = 5;
	private static final float DEF_TRANSLATION_X = 0;
	private static final float DEF_TRANSLATION_Y = 0;
	private static final float DEF_SCALING_X = 1;
	private static final float DEF_SCALING_Y = 1;
	private static final float DEF_COMMON_SCALING = 1;
	private static final boolean DEF_USE_COMMON_SCALING = true;
	

	private final SharedPreferences preferences;
	
	private TransformationPreferencesHelper(SharedPreferences preferences) {
		this.preferences = preferences;
	}
	
	public TransformationPreferencesHelper(Context context) {
		this(context.getSharedPreferences("transformation", Context.MODE_PRIVATE));
	}

	
	public float getRotation() {
		return preferences.getFloat(KEY_ROTATION, DEF_ROTATION);
	}
	
	public float getTranslationX() {
		return preferences.getFloat(KEY_TRANSLATION_X, DEF_TRANSLATION_X);
	}
	
	public float getTranslationY() {
		return preferences.getFloat(KEY_TRANSLATION_Y, DEF_TRANSLATION_Y);
	}
	
	public float getScalingX() {
		return preferences.getFloat(KEY_SCALING_X, DEF_SCALING_X);
	}
	
	public float getScalingY() {
		return preferences.getFloat(KEY_SCALING_Y, DEF_SCALING_Y);
	}
	
	public float getCommonScaling() {
		return preferences.getFloat(KEY_COMMON_SCALING, DEF_COMMON_SCALING);
	}
	
	public boolean isUseCommonScaling() {
		return preferences.getBoolean(KEY_USE_COMMON_SCALING, DEF_USE_COMMON_SCALING);
	}
	
	
	public void storeTransformation(MoireeTransformation transformation) {
		SharedPreferences.Editor editor = preferences.edit();

		editor.putFloat(KEY_ROTATION, transformation.getRotation());

		editor.putFloat(KEY_TRANSLATION_X, transformation.getTranslationX());
		editor.putFloat(KEY_TRANSLATION_Y, transformation.getTranslationY());

		editor.putFloat(KEY_SCALING_X, transformation.getScalingX());
		editor.putFloat(KEY_SCALING_Y, transformation.getScalingY());
		
		editor.putBoolean(KEY_USE_COMMON_SCALING, transformation.isUseCommonScaling());
		editor.putFloat(KEY_COMMON_SCALING, transformation.getCommonScaling());
		
		editor.apply();
	}
}
