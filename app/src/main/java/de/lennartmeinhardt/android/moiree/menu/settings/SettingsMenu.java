package de.lennartmeinhardt.android.moiree.menu.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;

public class SettingsMenu extends MenuFragment {

    private static final String KEY_INPUT_METHODS_EXPANDED = "settingsFragment:inputMethodsExpanded";

    private SharedPreferences preferences;

    private InputMethodsSetupFragment inputMethodsSetupFragment;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        inputMethodsSetupFragment = (InputMethodsSetupFragment) getChildFragmentManager().findFragmentById(R.id.fragment_input_methods_setup);

        if(savedInstanceState == null)
            inputMethodsSetupFragment.setExpanded(preferences.getBoolean(KEY_INPUT_METHODS_EXPANDED, false));

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        preferences.edit().putBoolean(KEY_INPUT_METHODS_EXPANDED, inputMethodsSetupFragment.isExpanded()).apply();
    }
}
