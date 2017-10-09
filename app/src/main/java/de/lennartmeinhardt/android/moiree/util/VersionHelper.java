package de.lennartmeinhardt.android.moiree.util;

import android.content.SharedPreferences;

import de.lennartmeinhardt.android.moiree.BuildConfig;

public class VersionHelper {

    private static final String KEY_LAST_USED_VERSION = "versionHelper:lastUsedVersion";

    public static int readLastUsedVersion(SharedPreferences preferences) {
        return preferences.getInt(KEY_LAST_USED_VERSION, -1);
    }

    public static void storeCurrentVersionAsLastUsed(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putInt(KEY_LAST_USED_VERSION, BuildConfig.VERSION_CODE);
    }

    public static void storeCurrentVersionAsLastUsed(SharedPreferences preferences) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        storeCurrentVersionAsLastUsed(preferencesEditor);
        preferencesEditor.apply();
    }

    private VersionHelper() {
    }
}
