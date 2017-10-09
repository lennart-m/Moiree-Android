package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.content.res.Resources;
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

    public final ObservableFloat translationDistanceThreshold = new ObservableFloat();


    public void loadFromResources(Resources resources) {
        rotationSensitivity.set(resources.getInteger(R.integer.rotation_sensitivity_default_percents) / 100f);
        scalingSensitivity.set(resources.getInteger(R.integer.scaling_sensitivity_default_percents) / 100f);
        translationSensitivity.set(resources.getInteger(R.integer.translation_sensitivity_default_percents) / 100f);

        rotationInputEnabled.set(resources.getBoolean(R.bool.default_rotation_input_enabled));
        scalingInputEnabled.set(resources.getBoolean(R.bool.default_scaling_input_enabled));
        translationInputEnabled.set(resources.getBoolean(R.bool.default_translation_input_enabled));

        translationDistanceThreshold.set(resources.getDimensionPixelSize(R.dimen.translation_distance_threshold));
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
