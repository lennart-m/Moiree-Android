package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public class MenuTransparencyConfig {

    private static final String KEY_TRANSPARENCY_ENABLED = "menuTransparency:transparencyEnabled";
    private static final String KEY_MENU_ALPHA = "menuTransparency:menuAlpha";

    private final List<OnMenuTransparencyChangedListener> onMenuTransparencyChangedListeners = new ArrayList<>();

    private boolean transparencyEnabled;
    private final float defaultMenuAlpha;
    private float menuAlpha;


    public MenuTransparencyConfig(Resources resources) {
        this(resources.getBoolean(R.bool.menu_transparency_default_enabled),
                resources.getInteger(R.integer.menu_opacity_default_percents) / 100f);
    }

    public MenuTransparencyConfig(boolean transparencyEnabled, float defaultMenuAlpha) {
        this.transparencyEnabled = transparencyEnabled;
        this.defaultMenuAlpha = defaultMenuAlpha;
        this.menuAlpha = defaultMenuAlpha;
    }


    public void loadFromPreferences(SharedPreferences preferences) {
        setTransparencyEnabled(preferences.getBoolean(KEY_TRANSPARENCY_ENABLED, transparencyEnabled));
        setMenuAlpha(preferences.getFloat(KEY_MENU_ALPHA, menuAlpha));
    }

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

    public void setMenuAlphaToDefault() {
        setMenuAlpha(defaultMenuAlpha);
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
