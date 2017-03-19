package de.lennartmeinhardt.android.moiree.util;

import android.content.SharedPreferences;

// TODO raus
public interface PreferenceIO {

    void storeToPreferences(SharedPreferences.Editor preferencesEditor, String prefix);

    void loadFromPreferences(SharedPreferences preferences, String prefix);
}
