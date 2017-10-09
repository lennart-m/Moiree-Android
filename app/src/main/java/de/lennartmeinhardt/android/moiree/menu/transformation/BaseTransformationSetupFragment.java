package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.lennartmeinhardt.android.moiree.MoireeTransformation;
import de.lennartmeinhardt.android.moiree.MoireeTransformationHolder;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarter;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarterHolder;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;

abstract class BaseTransformationSetupFragment extends MenuFragment {

    MoireeTransformation moireeTransformation;
    private MoireeTransformation backupMoireeTransformation;
    private MoireeTransitionStarter moireeTransitionStarter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeTransformation = ((MoireeTransformationHolder) getActivity()).getMoireeTransformation();
        if(getActivity() instanceof MoireeTransitionStarterHolder)
            this.moireeTransitionStarter = ((MoireeTransitionStarterHolder) getActivity()).getMoireeTransitionStarter();

        if(savedInstanceState == null) {
            savedInstanceState = new Bundle();
            moireeTransformation.storeToBundle(savedInstanceState);
        }
        backupMoireeTransformation = new MoireeTransformation();
        backupMoireeTransformation.readFromBundle(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        backupMoireeTransformation.storeToBundle(outState);
    }

    void beginMoireeTransformationTransitionIfWanted() {
        if(moireeTransitionStarter != null)
            moireeTransitionStarter.beginTransformationTransitionIfWanted();
    }

    void resetTransformationToBackup() {
        beginMoireeTransformationTransitionIfWanted();

        Bundle valuesBundle = new Bundle();
        backupMoireeTransformation.storeToBundle(valuesBundle);
        moireeTransformation.readFromBundle(valuesBundle);
    }
}
