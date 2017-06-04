package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableFloat;

import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MoireeInputMethods implements PreferenceIO {

    private static final String KEY_INPUT_ROTATION_ENABLED = "moireeInputMethods:rotationInputEnabled";
    private static final String KEY_INPUT_SCALING_ENABLED = "moireeInputMethods:scalingInputEnabled";
    private static final String KEY_INPUT_TRANSLATION_ENABLED = "moireeInputMethods:translationInputEnabled";

    private static final String KEY_ROTATION_SENSITIVITY = "moireeInputMethods:rotationSensitivity";
    private static final String KEY_SCALING_SENSITIVITY = "moireeInputMethods:scalingSensitivity";
    private static final String KEY_TRANSLATION_SENSITIVITY = "moireeInputMethods:translationSensitivity";

    public final ObservableBoolean rotationInputEnabled = new ObservableBoolean();
    public final ObservableBoolean translationInputEnabled = new ObservableBoolean();
    public final ObservableBoolean scalingInputEnabled = new ObservableBoolean();

    public final ObservableFloat rotationSensitivity = new ObservableFloat();
    public final ObservableFloat scalingSensitivity = new ObservableFloat();
    public final ObservableFloat translationSensitivity = new ObservableFloat();


    public MoireeInputMethods(boolean allInputMethodsEnabled, float sensitivity) {
        this(allInputMethodsEnabled, sensitivity, allInputMethodsEnabled, sensitivity, allInputMethodsEnabled, sensitivity);
    }

    public MoireeInputMethods(boolean rotationInputEnabled, float rotationSensitivity, boolean scalingInputEnabled, float scalingSensitivity, boolean translationInputEnabled, float translationSensitivity) {
        this.rotationSensitivity.set(rotationSensitivity);
        this.scalingSensitivity.set(scalingSensitivity);
        this.translationSensitivity.set(translationSensitivity);
        this.rotationInputEnabled.set(rotationInputEnabled);
        this.scalingInputEnabled.set(scalingInputEnabled);
        this.translationInputEnabled.set(translationInputEnabled);
    }


    @Override
    public void loadFromPreferences(SharedPreferences preferences) {
        rotationInputEnabled.set(preferences.getBoolean(KEY_INPUT_ROTATION_ENABLED, rotationInputEnabled.get()));
        scalingInputEnabled.set(preferences.getBoolean(KEY_INPUT_SCALING_ENABLED, scalingInputEnabled.get()));
        translationInputEnabled.set(preferences.getBoolean(KEY_INPUT_TRANSLATION_ENABLED, translationInputEnabled.get()));

        rotationSensitivity.set(preferences.getFloat(KEY_ROTATION_SENSITIVITY, rotationSensitivity.get()));
        scalingSensitivity.set(preferences.getFloat(KEY_SCALING_SENSITIVITY, scalingSensitivity.get()));
        translationSensitivity.set(preferences.getFloat(KEY_TRANSLATION_SENSITIVITY, translationSensitivity.get()));
    }

    @Override
    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putBoolean(KEY_INPUT_ROTATION_ENABLED, rotationInputEnabled.get());
        preferencesEditor.putBoolean(KEY_INPUT_SCALING_ENABLED, scalingInputEnabled.get());
        preferencesEditor.putBoolean(KEY_INPUT_TRANSLATION_ENABLED, translationInputEnabled.get());

        preferencesEditor.putFloat(KEY_ROTATION_SENSITIVITY, rotationSensitivity.get());
        preferencesEditor.putFloat(KEY_SCALING_SENSITIVITY, scalingSensitivity.get());
        preferencesEditor.putFloat(KEY_TRANSLATION_SENSITIVITY, translationSensitivity.get());
    }
}
