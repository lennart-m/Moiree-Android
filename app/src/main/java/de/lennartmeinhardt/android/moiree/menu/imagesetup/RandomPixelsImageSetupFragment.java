package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RandomPixelsImageCreator;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;

public abstract class RandomPixelsImageSetupFragment extends BaseImageCreatorSetupFragment {

    private RandomPixelsImageCreator imageCreator;

    private ExpandableView expandableView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_random_pixels_image_setup, container, false);

        expandableView = (ExpandableView) rootView.findViewById(R.id.expandable_view);


        return rootView;
    }
}
