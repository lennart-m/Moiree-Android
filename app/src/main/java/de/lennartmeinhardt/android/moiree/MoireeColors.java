package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.databinding.ObservableInt;

import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MoireeColors implements PreferenceIO {

	private static final String KEY_BACKGROUND_COLOR = "moireeColors:backgroundColor";
	private static final String KEY_FOREGROUND_COLOR = "moireeColors:foregroundColor";

	public final ObservableInt backgroundColor = new ObservableInt();
	public final ObservableInt foregroundColor = new ObservableInt();

	public MoireeColors(int foregroundColor, int backgroundColor) {
		this.foregroundColor.set(foregroundColor);
		this.backgroundColor.set(backgroundColor);
	}


	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		backgroundColor.set(preferences.getInt(KEY_BACKGROUND_COLOR, backgroundColor.get()));
		foregroundColor.set(preferences.getInt(KEY_FOREGROUND_COLOR, foregroundColor.get()));
	}

	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putInt(KEY_BACKGROUND_COLOR, backgroundColor.get());
		preferencesEditor.putInt(KEY_FOREGROUND_COLOR, foregroundColor.get());
	}

	@Override
	public String toString() {
		return String.format("[fg=#%s, bg=#%s]", Integer.toHexString(foregroundColor.get()), Integer.toHexString(backgroundColor.get()));
	}
}
