package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

public class MoireeColors implements PreferenceIO {

	private static final String KEY_BACKGROUND_COLOR = "moireeColors:backgroundColor";
	private static final String KEY_FOREGROUND_COLOR = "moireeColors:foregroundColor";

	private final List<MoireeColorsListener> listeners = new ArrayList<>();

	private int backgroundColor;
	private int foregroundColor;

	public MoireeColors(int foregroundColor, int backgroundColor) {
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
	}

	
	public int getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		for(MoireeColorsListener listener : listeners)
			listener.onBackgroundColorChanged(backgroundColor);
	}
	
	public int getForegroundColor() {
		return foregroundColor;
	}
	public void setForegroundColor(int foregroundColor) {
		this.foregroundColor = foregroundColor;
		for(MoireeColorsListener listener : listeners)
			listener.onForegroundColorChanged(foregroundColor);
	}

	
	public void addMoireeColorsListener(MoireeColorsListener listener) {
		listeners.add(listener);
	}
	public void addAndFireMoireeColorsListener(MoireeColorsListener listener) {
		addMoireeColorsListener(listener);
		listener.onForegroundColorChanged(foregroundColor);
		listener.onBackgroundColorChanged(backgroundColor);
	}

	public void removeMoireeColorsListener(MoireeColorsListener listener) {
		listeners.remove(listener);
	}
	
	public interface MoireeColorsListener {
		void onBackgroundColorChanged(int newColor);
		void onForegroundColorChanged(int newColor);
	}

	@Override
	public void loadFromPreferences(SharedPreferences preferences) {
		setBackgroundColor(preferences.getInt(KEY_BACKGROUND_COLOR, backgroundColor));
		setForegroundColor(preferences.getInt(KEY_FOREGROUND_COLOR, foregroundColor));
	}

	@Override
	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putInt(KEY_BACKGROUND_COLOR, backgroundColor);
		preferencesEditor.putInt(KEY_FOREGROUND_COLOR, foregroundColor);
	}

	@Override
	public String toString() {
		return String.format("[fg=#%s, bg=#%s]", Integer.toHexString(foregroundColor), Integer.toHexString(backgroundColor));
	}
}
