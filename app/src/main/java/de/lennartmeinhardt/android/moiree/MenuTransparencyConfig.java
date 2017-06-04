package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableFloat;

import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MenuTransparencyConfig extends BaseObservable implements PreferenceIO {

    private static final String KEY_TRANSPARENCY_ENABLED = "menuTransparency:transparencyEnabled";
    private static final String KEY_MENU_ALPHA = "menuTransparency:menuAlpha";

    public final ObservableBoolean transparencyEnabled = new ObservableBoolean();
    public final ObservableFloat menuAlpha = new ObservableFloat();
    {
        OnPropertyChangedCallback effectiveAlphaUpdater = new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                notifyPropertyChanged(BR.effectiveAlpha);
            }
        };
        transparencyEnabled.addOnPropertyChangedCallback(effectiveAlphaUpdater);
        menuAlpha.addOnPropertyChangedCallback(effectiveAlphaUpdater);
    }


    public MenuTransparencyConfig(boolean transparencyEnabled, float defaultMenuAlpha) {
        this.transparencyEnabled.set(transparencyEnabled);
        this.menuAlpha.set(defaultMenuAlpha);
    }


    @Override
    public void loadFromPreferences(SharedPreferences preferences) {
        transparencyEnabled.set(preferences.getBoolean(KEY_TRANSPARENCY_ENABLED, transparencyEnabled.get()));
        menuAlpha.set(preferences.getFloat(KEY_MENU_ALPHA, menuAlpha.get()));
    }

    @Override
    public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putBoolean(KEY_TRANSPARENCY_ENABLED, transparencyEnabled.get());
        preferencesEditor.putFloat(KEY_MENU_ALPHA, menuAlpha.get());
    }

    @Bindable
    public float getEffectiveAlpha() {
        return transparencyEnabled.get() ? menuAlpha.get() : 1f;
    }
}
