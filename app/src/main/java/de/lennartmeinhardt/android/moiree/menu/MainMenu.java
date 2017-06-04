package de.lennartmeinhardt.android.moiree.menu;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.menu.settings.SettingsMenu;
import de.lennartmeinhardt.android.moiree.menu.transformation.TransformationSetupMenu;

public class MainMenu extends MenuFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initializeMoireeImageButton(view);
        initializeColorSetupButton(view);
        initializeTransformationButton(view);
        initializeSettingsButton(view);
        initializeHelpButton(view);
        initializeAboutButton(view);
    }

    private void initializeMoireeImageButton(View root) {
        final CardView moireeImageCard = (CardView) root.findViewById(R.id.main_menu_moiree_image_card);
        final ImageView moireeImageBackgroundView1 = (ImageView) moireeImageCard.findViewById(R.id.main_menu_moiree_image_background1);
        final ImageView moireeImageBackgroundView2 = (ImageView) moireeImageCard.findViewById(R.id.main_menu_image_background2);

        final int tileSize = getResources().getDimensionPixelSize(R.dimen.moiree_image_button_tile_size);

        moireeImageCard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16)
                    moireeImageCard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    moireeImageCard.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                CheckerboardImageCreator imageCreator = new CheckerboardImageCreator(tileSize);
                int width = moireeImageBackgroundView1.getWidth();
                int height = moireeImageBackgroundView1.getHeight();
                // necessary because this fragment may be created while clearing the back stack
                if(width > 0 && height > 0) {
                    Drawable image = imageCreator.createMoireeImageForDimensions(getResources(), width, height);

                    moireeImageBackgroundView1.setImageDrawable(image);
                    moireeImageBackgroundView2.setImageDrawable(image);
                }
            }
        });

        moireeImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuHolderFragment().openMenuFragment(new ImageSetupMenu(), "moireeImageSetup");
            }
        });
    }

    private void initializeColorSetupButton(View root) {
        CardView colorSetupCard = (CardView) root.findViewById(R.id.main_menu_colors_card);
        colorSetupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuHolderFragment().openMenuFragment(new MoireeColorsSetupMenu(), "moireeColorsSetup");
            }
        });
    }

    private void initializeTransformationButton(View root) {
        CardView moireeTransformationSetupButton = (CardView) root.findViewById(R.id.main_menu_transformation_card);
        moireeTransformationSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuHolderFragment().openMenuFragment(new TransformationSetupMenu(), "moireeTransformationSetup");
            }
        });
    }

    private void initializeSettingsButton(View root) {
        root.findViewById(R.id.main_menu_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuHolderFragment().openMenuFragment(new SettingsMenu(), "inputSettings");
            }
        });
    }

    private void initializeHelpButton(View root) {
        root.findViewById(R.id.main_menu_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuHolderFragment().openMenuFragment(new HelpMenu(), "help");
            }
        });
    }

    private void initializeAboutButton(View root) {
        root.findViewById(R.id.main_menu_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuHolderFragment().openMenuFragment(new AboutMenu(), "about");
            }
        });
    }
}
