package de.lennartmeinhardt.android.moiree.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.HorizontalLinesImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RandomPixelsImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.TrianglesImageCreator;
import de.lennartmeinhardt.android.moiree.menu.imagesetup.CheckerboardImageSetupFragment;
import de.lennartmeinhardt.android.moiree.menu.imagesetup.HorizontalLinesImageSetupFragment;
import de.lennartmeinhardt.android.moiree.menu.imagesetup.RandomPixelsImageSetupFragment;
import de.lennartmeinhardt.android.moiree.menu.imagesetup.TrianglesImageSetupFragment;
import de.lennartmeinhardt.android.moiree.util.Expandable;
import de.lennartmeinhardt.android.moiree.util.ImageCreatorHolder;

public class ImageSetupMenu extends MenuFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_image_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MoireeImageCreator imageCreatorInUse = ((ImageCreatorHolder) getActivity()).getImageCreator();

        CheckerboardImageSetupFragment checkerboardImageSetupFragment = (CheckerboardImageSetupFragment) getChildFragmentManager().findFragmentById(R.id.checkerboard_image_setup);
        final RandomPixelsImageSetupFragment randomPixelsImageSetupFragment = (RandomPixelsImageSetupFragment) getChildFragmentManager().findFragmentById(R.id.random_pixels_image_setup);
        HorizontalLinesImageSetupFragment horizontalLinesImageSetupFragment = (HorizontalLinesImageSetupFragment) getChildFragmentManager().findFragmentById(R.id.horizontal_lines_image_setup);
        TrianglesImageSetupFragment trianglesImageSetupFragment = (TrianglesImageSetupFragment) getChildFragmentManager().findFragmentById(R.id.triangles_image_setup);

        checkerboardImageSetupFragment.setExpanded(imageCreatorInUse instanceof CheckerboardImageCreator);
        randomPixelsImageSetupFragment.setExpanded(imageCreatorInUse instanceof RandomPixelsImageCreator);
        horizontalLinesImageSetupFragment.setExpanded(imageCreatorInUse instanceof HorizontalLinesImageCreator);
        trianglesImageSetupFragment.setExpanded(imageCreatorInUse instanceof TrianglesImageCreator);

        final List<Fragment> fragments = getChildFragmentManager().getFragments();
        Expandable.OnExpandedStateChangedListener collapseAllOthers = new Expandable.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(Expandable expandable, boolean expanded) {
                for (Fragment fragment : fragments)
                    if (fragment instanceof Expandable && fragment != expandable && expanded)
                        ((Expandable) fragment).setExpanded(false);
            }
        };
        for (Fragment fragment : fragments)
            if (fragment instanceof Expandable)
                ((Expandable) fragment).setOnExpandedStateChangedListener(collapseAllOthers);
    }
}
