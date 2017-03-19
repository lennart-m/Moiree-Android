package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import de.lennartmeinhardt.android.moiree.MainActivity;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;

abstract class BaseImageCreatorSetupFragment extends MenuFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
            getMoireeImageCreator().loadFromBundle(savedInstanceState, "");
        else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            getMoireeImageCreator().loadFromPreferences(preferences);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getMoireeImageCreator().storeToBundle(outState, "");
    }


    abstract MoireeImageCreator getMoireeImageCreator();

    void onCreateNewImageClicked() {
        ((MainActivity) getActivity()).setImageCreatorAndRecreateImage(getMoireeImageCreator());
        ((MainActivity) getActivity()).hideMenuIfShown();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        getMoireeImageCreator().storeToPreferences(preferencesEditor);
        preferencesEditor.apply();
    }
}
