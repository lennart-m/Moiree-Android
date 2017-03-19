package de.lennartmeinhardt.android.moiree;

import android.content.SharedPreferences;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

// TODO die colors etc haben defaultValues welche aus resources kommen. auch die moireeImageCreators haben defaults welche sie aus resources (oder context) auslesen
public class MoireeColors {

	// TODO ohne prefix
	private static final String KEY_BACKGROUND_COLOR = "moireeColors:backgroundColor";
	private static final String KEY_FOREGROUND_COLOR = "moireeColors:foregroundColor";

	public static final int DEF_BACKGROUND_COLOR = Color.rgb(15, 90, 135); // Color.BLACK;
	public static final int DEF_FOREGROUND_COLOR = Color.rgb(200, 200, 150); // Color.LTGRAY;
	
	private final List<MoireeColorsListener> listeners = new ArrayList<>();

	private int backgroundColor;
	private int foregroundColor;

	public MoireeColors() {
		this(DEF_FOREGROUND_COLOR, DEF_BACKGROUND_COLOR);
	}

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

	public void loadFromPreferences(SharedPreferences preferences) {
		setBackgroundColor(preferences.getInt(KEY_BACKGROUND_COLOR, DEF_BACKGROUND_COLOR));
		setForegroundColor(preferences.getInt(KEY_FOREGROUND_COLOR, DEF_FOREGROUND_COLOR));
	}

	public void storeToPreferences(SharedPreferences.Editor preferencesEditor) {
		preferencesEditor.putInt(KEY_BACKGROUND_COLOR, backgroundColor);
		preferencesEditor.putInt(KEY_FOREGROUND_COLOR, foregroundColor);
	}

	public void resetToDefault() {
		setBackgroundColor(DEF_BACKGROUND_COLOR);
		setForegroundColor(DEF_FOREGROUND_COLOR);
	}
}
