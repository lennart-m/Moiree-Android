package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;

public class MoireeInputMethods {

    private static final String KEY_INPUT_ROTATION_ENABLED = "moireeInputMethods:rotationInputEnabled";
    private static final String KEY_INPUT_SCALING_ENABLED = "moireeInputMethods:scalingInputEnabled";
    private static final String KEY_INPUT_TRANSLATION_ENABLED = "moireeInputMethods:translationInputEnabled";

    private static final String KEY_ROTATION_SENSITIVITY = "moireeInputMethods:rotationSensitivity";
    private static final String KEY_SCALING_SENSITIVITY = "moireeInputMethods:scalingSensitivity";
    private static final String KEY_TRANSLATION_SENSITIVITY = "moireeInputMethods:translationSensitivity";

    private boolean rotationInputEnabled;
    private boolean translationInputEnabled;
    private boolean scalingInputEnabled;

    private float rotationSensitivity;
    private float scalingSensitivity;
    private float translationSensitivity;


    public MoireeInputMethods(boolean allInputMethodsEnabled, float sensitivity) {
        this(allInputMethodsEnabled, sensitivity, allInputMethodsEnabled, sensitivity, allInputMethodsEnabled, sensitivity);
    }

    public MoireeInputMethods(boolean rotationInputEnabled, float rotationSensitivity, boolean scalingInputEnabled, float scalingSensitivity, boolean translationInputEnabled, float translationSensitivity) {
        this.rotationSensitivity = rotationSensitivity;
        this.scalingSensitivity = scalingSensitivity;
        this.translationSensitivity = translationSensitivity;
        this.rotationInputEnabled = rotationInputEnabled;
        this.scalingInputEnabled = scalingInputEnabled;
        this.translationInputEnabled = translationInputEnabled;
    }


    public void setRotationInputEnabled(boolean enabled) {
        this.rotationInputEnabled = enabled;
    }
    public void setScalingInputEnabled(boolean enabled) {
        this.scalingInputEnabled = enabled;
    }
    public void setTranslationInputEnabled(boolean enabled) {
        this.translationInputEnabled = enabled;
    }

    public boolean isRotationInputEnabled() {
        return this.rotationInputEnabled;
    }
    public boolean isScalingInputEnabled() {
        return this.scalingInputEnabled;
    }
    public boolean isTranslationInputEnabled() {
        return this.translationInputEnabled;
    }

    public void loadFromPreferences(SharedPreferences preferences) {
        this.rotationInputEnabled = preferences.getBoolean(KEY_INPUT_ROTATION_ENABLED, rotationInputEnabled);
        this.scalingInputEnabled = preferences.getBoolean(KEY_INPUT_SCALING_ENABLED, scalingInputEnabled);
        this.translationInputEnabled = preferences.getBoolean(KEY_INPUT_TRANSLATION_ENABLED, translationInputEnabled);

        this.rotationSensitivity = preferences.getFloat(KEY_ROTATION_SENSITIVITY, rotationSensitivity);
        this.scalingSensitivity = preferences.getFloat(KEY_SCALING_SENSITIVITY, scalingSensitivity);
        this.translationSensitivity = preferences.getFloat(KEY_TRANSLATION_SENSITIVITY, translationSensitivity);
    }

    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putBoolean(KEY_INPUT_ROTATION_ENABLED, rotationInputEnabled);
        preferencesEditor.putBoolean(KEY_INPUT_SCALING_ENABLED, scalingInputEnabled);
        preferencesEditor.putBoolean(KEY_INPUT_TRANSLATION_ENABLED, translationInputEnabled);

        preferencesEditor.putFloat(KEY_ROTATION_SENSITIVITY, rotationSensitivity);
        preferencesEditor.putFloat(KEY_SCALING_SENSITIVITY, scalingSensitivity);
        preferencesEditor.putFloat(KEY_TRANSLATION_SENSITIVITY, translationSensitivity);
    }



    public float getRotationSensitivity() {
        return rotationSensitivity;
    }
    public void setRotationSensitivity(float rotationSensitivity) {
        this.rotationSensitivity = rotationSensitivity;
    }

    public float getScalingSensitivity() {
        return scalingSensitivity;
    }
    public void setScalingSensitivity(float scalingSensitivity) {
        this.scalingSensitivity = scalingSensitivity;
    }

    public float getTranslationSensitivity() {
        return translationSensitivity;
    }
    public void setTranslationSensitivity(float translationSensitivity) {
        this.translationSensitivity = translationSensitivity;
    }
}
