package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import de.lennartmeinhardt.android.moiree.MoireeImaging;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.util.BundleIO;
import de.lennartmeinhardt.android.moiree.util.PreferenceIO;

/**
 * Base class for image creator setup fragments.
 * Handles loading and storing image creator config from preferences or instance state.
 */
abstract class BaseImageCreatorSetupFragment extends MenuFragment {

    private SharedPreferences preferences;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MoireeImageCreator imageCreator = getMoireeImageCreator();

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

        MoireeImageCreator imageCreator = getMoireeImageCreator();
        if(imageCreator instanceof BundleIO)
            ((BundleIO) imageCreator).storeToBundle(outState);
    }

    void onCreateNewImageClicked() {
        storeImageCreatorToPreferences();
        ((MoireeImaging) getActivity()).setImageCreatorAndRecreateImage(getMoireeImageCreator());
    }

    protected void storeImageCreatorToPreferences() {
        MoireeImageCreator imageCreator = getMoireeImageCreator();

        if(imageCreator instanceof PreferenceIO) {
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            ((PreferenceIO) imageCreator).storeToPreferences(preferencesEditor);
            preferencesEditor.apply();
        }
    }

    protected abstract MoireeImageCreator getMoireeImageCreator();
}
