package de.lennartmeinhardt.android.moiree.util;

import android.content.SharedPreferences;

public interface PreferenceIO {

    void storeToPreferences(SharedPreferences.Editor preferencesEditor);

    void loadFromPreferences(SharedPreferences preferences);
}
