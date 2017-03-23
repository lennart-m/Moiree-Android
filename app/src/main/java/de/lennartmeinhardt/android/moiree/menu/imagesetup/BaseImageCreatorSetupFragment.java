package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import de.lennartmeinhardt.android.moiree.MainActivity;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

abstract class BaseImageCreatorSetupFragment <C extends MoireeImageCreator> extends MenuFragment {

    private SharedPreferences preferences;

    C imageCreator;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageCreator = initializeImageCreator();

        if(savedInstanceState != null) {
            if (imageCreator instanceof BundleIO)
                ((BundleIO) imageCreator).loadFromBundle(savedInstanceState);
        } else {
            if (imageCreator instanceof PreferenceIO)
                ((PreferenceIO) imageCreator).loadFromPreferences(preferences);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(imageCreator instanceof BundleIO)
            ((BundleIO) imageCreator).storeToBundle(outState);
    }

    void onCreateNewImageClicked() {
        ((MainActivity) getActivity()).setImageCreatorAndRecreateImage(imageCreator);
        ((MainActivity) getActivity()).hideMenuIfShown();
        storeImageCreatorToPreferences();
    }

    private void storeImageCreatorToPreferences() {
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        if(imageCreator instanceof PreferenceIO)
            ((PreferenceIO) imageCreator).storeToPreferences(preferencesEditor);
        preferencesEditor.apply();
    }

    abstract C initializeImageCreator();
}
