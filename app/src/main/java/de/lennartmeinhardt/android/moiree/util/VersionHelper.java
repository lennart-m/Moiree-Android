package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

public class VersionHelper {

    private static final String KEY_LAST_USED_VERSION = "versionHelper:lastUsedVersion";

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch(PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static int readLastUsedVersion(SharedPreferences preferences) {
        return preferences.getInt(KEY_LAST_USED_VERSION, -1);
    }

    public static void storeCurrentVersionAsLastUsed(Context context, SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putInt(KEY_LAST_USED_VERSION, getVersionCode(context));
    }

    public static void storeCurrentVersionAsLastUsed(Context context, SharedPreferences preferences) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        storeCurrentVersionAsLastUsed(context, preferencesEditor);
        preferencesEditor.apply();
    }

    private VersionHelper() {
    }
}
