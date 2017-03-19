package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public class MoireeInputMethods {

    private static final String KEY_INPUT_ROTATION_ENABLED = "moireeInputMethods:rotationInputEnabled";
    private static final String KEY_INPUT_SCALING_ENABLED = "moireeInputMethods:scalingInputEnabled";
    private static final String KEY_INPUT_TRANSLATION_ENABLED = "mmoireeInputMethods:translationInputEnabled";

    private static final String KEY_ROTATION_SENSITIVITY = "mmoireeInputMethods:rotationSensitivity";
    private static final String KEY_SCALING_SENSITIVITY = "moireeInputMethods:scalingSensitivity";
    private static final String KEY_TRANSLATION_SENSITIVITY = "moireeInputMethods:translationSensitivity";

    private final List<MoireeInputMethodListener> moireeInputMethodListeners = new ArrayList<>();

    private boolean rotationInputEnabled;
    private boolean translationInputEnabled;
    private boolean scalingInputEnabled;

    private float rotationSensitivity;
    private float scalingSensitivity;
    private float translationSensitivity;

    private final float defaultRotationSensitivity;
    private final float defaultScalingSensitivity;
    private final float defaultTranslationSensitivity;


    public MoireeInputMethods(Resources resources) {
        this(resources.getInteger(R.integer.sensitivity_default_value_percents) / 100f);
    }

    public MoireeInputMethods(float sensitivity) {
        this(sensitivity, sensitivity, sensitivity);
    }

    public MoireeInputMethods(float rotationSensitivity, float scalingSensitivity, float translationSensitivity) {
        this(rotationSensitivity, scalingSensitivity, translationSensitivity, true, true, true);
    }

    public MoireeInputMethods(float rotationSensitivity, float scalingSensitivity, float translationSensitivity, boolean rotationInputEnabled, boolean scalingInputEnabled, boolean translationInputEnabled) {
        this.defaultRotationSensitivity = rotationSensitivity;
        this.defaultScalingSensitivity = scalingSensitivity;
        this.defaultTranslationSensitivity = translationSensitivity;
        this.rotationSensitivity = rotationSensitivity;
        this.scalingSensitivity = scalingSensitivity;
        this.translationSensitivity = translationSensitivity;
        this.rotationInputEnabled = rotationInputEnabled;
        this.scalingInputEnabled = scalingInputEnabled;
        this.translationInputEnabled = translationInputEnabled;
    }


    public void setRotationInputEnabled(boolean enabled) {
        this.rotationInputEnabled = enabled;
        fireRotationListeners();
    }
    public void setScalingInputEnabled(boolean enabled) {
        this.scalingInputEnabled = enabled;
        fireScalingListeners();
    }
    public void setTranslationInputEnabled(boolean enabled) {
        this.translationInputEnabled = enabled;
        fireTranslationListeners();
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

        fireRotationListeners();
        fireScalingListeners();
        fireTranslationListeners();
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
        fireRotationListeners();
    }
    public void setRotationSensitivityToDefault() {
        setRotationSensitivity(defaultRotationSensitivity);
    }

    public float getScalingSensitivity() {
        return scalingSensitivity;
    }
    public void setScalingSensitivity(float scalingSensitivity) {
        this.scalingSensitivity = scalingSensitivity;
        fireScalingListeners();
    }
    public void setScalingSensitivityToDefault() {
        setScalingSensitivity(defaultScalingSensitivity);
    }

    public float getTranslationSensitivity() {
        return translationSensitivity;
    }
    public void setTranslationSensitivity(float translationSensitivity) {
        this.translationSensitivity = translationSensitivity;
        fireTranslationListeners();
    }
    public void setTranslationSensitivityToDefault() {
        setTranslationSensitivity(defaultTranslationSensitivity);
    }

    public void addMoireeInputMethodListener(MoireeInputMethodListener moireeInputMethodListener) {
        moireeInputMethodListeners.add(moireeInputMethodListener);
    }

    public void addAndFireMoireeInputMethodListener(MoireeInputMethodListener moireeInputMethodListener) {
        addMoireeInputMethodListener(moireeInputMethodListener);
        moireeInputMethodListener.onRotationInputMethodChanged(rotationInputEnabled, rotationSensitivity);
        moireeInputMethodListener.onScalingInputMethodChanged(scalingInputEnabled, scalingSensitivity);
        moireeInputMethodListener.onTranslationInputMethodChanged(translationInputEnabled, translationSensitivity);
    }

    public void removeMoireeInputMethodListener(MoireeInputMethodListener moireeInputMethodListener) {
        moireeInputMethodListeners.remove(moireeInputMethodListener);
    }

    private void fireRotationListeners() {
        for(MoireeInputMethodListener moireeInputMethodListener : moireeInputMethodListeners)
            moireeInputMethodListener.onRotationInputMethodChanged(rotationInputEnabled, rotationSensitivity);
    }

    private void fireScalingListeners() {
        for(MoireeInputMethodListener moireeInputMethodListener : moireeInputMethodListeners)
            moireeInputMethodListener.onScalingInputMethodChanged(scalingInputEnabled, scalingSensitivity);
    }

    private void fireTranslationListeners() {
        for(MoireeInputMethodListener moireeInputMethodListener : moireeInputMethodListeners)
            moireeInputMethodListener.onTranslationInputMethodChanged(translationInputEnabled, translationSensitivity);
    }


    // TODO listener können weg. die braucht wirklich kein mensch (wenn IVS seinen eigenen wert verwaltet)

    public interface MoireeInputMethodListener {

        void onRotationInputMethodChanged(boolean enabled, float sensitivity);

        void onScalingInputMethodChanged(boolean enabled, float sensitivity);

        void onTranslationInputMethodChanged(boolean enabled, float sensitivity);


        class Adapter implements MoireeInputMethodListener {
            @Override
            public void onRotationInputMethodChanged(boolean enabled, float sensitivity) {
            }

            @Override
            public void onScalingInputMethodChanged(boolean enabled, float sensitivity) {
            }

            @Override
            public void onTranslationInputMethodChanged(boolean enabled, float sensitivity) {
            }
        }
    }
}
