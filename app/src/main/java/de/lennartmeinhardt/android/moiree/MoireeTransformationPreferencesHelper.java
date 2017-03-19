package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;

// TODO: ist vom design her eigentlich besser als das in die MoireeTransformation klasse zu schreiben, aber das wäre trotzdem besser. könnte PreferenceIO beerben
public class MoireeTransformationPreferencesHelper {

    private static final String KEY_ROTATION = "moireeTransformation:rotation";
    private static final String KEY_TRANSLATION_X = "moireeTransformation:translationX";
    private static final String KEY_TRANSLATION_Y = "moireeTransformation:translationY";
    private static final String KEY_COMMON_SCALING = "moireeTransformation:commonScaling";
    private static final String KEY_SCALING_X = "moireeTransformation:scalingX";
    private static final String KEY_SCALING_Y = "moireeTransformation:scalingY";
    private static final String KEY_USE_COMMON_SCALING = "moireeTransformation:useCommonScaling";

    // TODO das muss als trafo irgendwie public sein und eher von MainActivity gemacht werden. z.B. loadMoireeTransformationFromPreferences(Prefs, Transformation defaultTransformation)
    private static final float DEF_ROTATION = 3;
    private static final float DEF_TRANSLATION_X = 0;
    private static final float DEF_TRANSLATION_Y = 0;
    private static final float DEF_COMMON_SCALING = 1.05f;
    private static final float DEF_SCALING_X = 1.05f;
    private static final float DEF_SCALING_Y = 1.05f;
    private static final boolean DEF_USE_COMMON_SCALING = true;


    public static MoireeTransformation loadMoireeTransformationFromPreferences(SharedPreferences preferences) {
        MoireeTransformation transformation = new MoireeTransformation();
        transformation.setRotation(preferences.getFloat(KEY_ROTATION, DEF_ROTATION));
        transformation.setTranslationX(preferences.getFloat(KEY_TRANSLATION_X, DEF_TRANSLATION_X));
        transformation.setTranslationY(preferences.getFloat(KEY_TRANSLATION_Y, DEF_TRANSLATION_Y));
        transformation.setCommonScaling(preferences.getFloat(KEY_COMMON_SCALING, DEF_COMMON_SCALING));
        transformation.setScalingX(preferences.getFloat(KEY_SCALING_X, DEF_SCALING_X));
        transformation.setScalingY(preferences.getFloat(KEY_SCALING_Y, DEF_SCALING_Y));
        transformation.setUseCommonScaling(preferences.getBoolean(KEY_USE_COMMON_SCALING, DEF_USE_COMMON_SCALING));
        return transformation;
    }

    public static void storeMoireeTransformationToPreferences(SharedPreferences.Editor preferencesEditor, MoireeTransformation transformation) {
        preferencesEditor.putFloat(KEY_ROTATION, transformation.getRotation());
        preferencesEditor.putFloat(KEY_TRANSLATION_X, transformation.getTranslationX());
        preferencesEditor.putFloat(KEY_TRANSLATION_Y, transformation.getTranslationY());
        preferencesEditor.putFloat(KEY_COMMON_SCALING, transformation.getCommonScaling());
        preferencesEditor.putFloat(KEY_SCALING_X, transformation.getScalingX());
        preferencesEditor.putFloat(KEY_SCALING_Y, transformation.getScalingY());
        preferencesEditor.putBoolean(KEY_USE_COMMON_SCALING, transformation.isUseCommonScaling());
    }
}
