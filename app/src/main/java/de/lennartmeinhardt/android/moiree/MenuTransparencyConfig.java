package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MenuTransparencyConfig implements PreferenceIO {

    private static final String KEY_TRANSPARENCY_ENABLED = "menuTransparency:transparencyEnabled";
    private static final String KEY_MENU_ALPHA = "menuTransparency:menuAlpha";

    private final List<OnMenuTransparencyChangedListener> onMenuTransparencyChangedListeners = new ArrayList<>();

    private boolean transparencyEnabled;
    private float menuAlpha;


    public MenuTransparencyConfig(boolean transparencyEnabled, float defaultMenuAlpha) {
        this.transparencyEnabled = transparencyEnabled;
        this.menuAlpha = defaultMenuAlpha;
    }


    @Override
    public void loadFromPreferences(SharedPreferences preferences) {
        setTransparencyEnabled(preferences.getBoolean(KEY_TRANSPARENCY_ENABLED, transparencyEnabled));
        setMenuAlpha(preferences.getFloat(KEY_MENU_ALPHA, menuAlpha));
    }

    @Override
    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putBoolean(KEY_TRANSPARENCY_ENABLED, transparencyEnabled);
        preferencesEditor.putFloat(KEY_MENU_ALPHA, menuAlpha);
    }

    public boolean isTransparencyEnabled() {
        return transparencyEnabled;
    }
    public void setTransparencyEnabled(boolean transparencyEnabled) {
        if(this.transparencyEnabled != transparencyEnabled) {
            this.transparencyEnabled = transparencyEnabled;
            for(OnMenuTransparencyChangedListener onMenuTransparencyChangedListener : onMenuTransparencyChangedListeners)
                onMenuTransparencyChangedListener.onMenuTransparencyChanged(transparencyEnabled, menuAlpha);
        }
    }

    public float getMenuAlpha() {
        return menuAlpha;
    }
    public void setMenuAlpha(float menuAlpha) {
        if(this.menuAlpha != menuAlpha) {
            this.menuAlpha = menuAlpha;
            for(OnMenuTransparencyChangedListener onMenuTransparencyChangedListener : onMenuTransparencyChangedListeners)
                onMenuTransparencyChangedListener.onMenuTransparencyChanged(transparencyEnabled, menuAlpha);
        }
    }

    public void addMenuTransparencyListener(OnMenuTransparencyChangedListener onMenuTransparencyChangedListener) {
        onMenuTransparencyChangedListeners.add(onMenuTransparencyChangedListener);
    }
    public void addAndFireMenuTransparencyListener(OnMenuTransparencyChangedListener onMenuTransparencyChangedListener) {
        addMenuTransparencyListener(onMenuTransparencyChangedListener);
        onMenuTransparencyChangedListener.onMenuTransparencyChanged(transparencyEnabled, menuAlpha);
    }

    public void removeMenuTransparencyListener(OnMenuTransparencyChangedListener onMenuTransparencyChangedListener) {
        onMenuTransparencyChangedListeners.remove(onMenuTransparencyChangedListener);
    }


    public interface OnMenuTransparencyChangedListener {

        void onMenuTransparencyChanged(boolean transparencyEnabled, float menuAlpha);
    }
}
