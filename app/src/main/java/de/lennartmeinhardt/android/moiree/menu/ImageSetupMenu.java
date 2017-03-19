package de.lennartmeinhardt.android.moiree.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.lennartmeinhardt.android.moiree.MainActivity;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;

public class ImageSetupMenu extends MenuFragment {

    // TODO beim start gucken welcher imagecreator aktiv ist. das entsprechende fragment expanded, die anderen collapsed

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moiree_image_setup, container, false);
    }
}
