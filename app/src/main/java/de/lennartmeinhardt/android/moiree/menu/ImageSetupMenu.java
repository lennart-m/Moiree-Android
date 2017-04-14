package de.lennartmeinhardt.android.moiree.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.HorizontalLinesImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RandomPixelsImageCreator;
import de.lennartmeinhardt.android.moiree.menu.imagesetup.CheckerboardImageSetupFragment;
import de.lennartmeinhardt.android.moiree.menu.imagesetup.HorizontalLinesSetupFragment;
import de.lennartmeinhardt.android.moiree.menu.imagesetup.RandomPixelsImageSetupFragment;
import de.lennartmeinhardt.android.moiree.util.ImageCreatorHolder;

public class ImageSetupMenu extends MenuFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moiree_image_setup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MoireeImageCreator imageCreatorInUse = ((ImageCreatorHolder) getActivity()).getImageCreator();

        CheckerboardImageSetupFragment checkerboardImageSetupFragment = (CheckerboardImageSetupFragment) getChildFragmentManager().findFragmentById(R.id.checkerboard_image_setup);
        RandomPixelsImageSetupFragment randomPixelsImageSetupFragment = (RandomPixelsImageSetupFragment) getChildFragmentManager().findFragmentById(R.id.random_pixels_image_setup);
        HorizontalLinesSetupFragment horizontalLinesSetupFragment = (HorizontalLinesSetupFragment) getChildFragmentManager().findFragmentById(R.id.horizontal_lines_image_setup);

        checkerboardImageSetupFragment.getExpandableView().setExpanded(imageCreatorInUse instanceof CheckerboardImageCreator);
        randomPixelsImageSetupFragment.getExpandableView().setExpanded(imageCreatorInUse instanceof RandomPixelsImageCreator);
        horizontalLinesSetupFragment.getExpandableView().setExpanded(imageCreatorInUse instanceof HorizontalLinesImageCreator);
    }
}
