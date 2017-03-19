package de.lennartmeinhardt.android.moiree.menu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.menu.settings.SettingsMenu;
import de.lennartmeinhardt.android.moiree.menu.transformation.TransformationSetupMenu;

public class MainMenuFragment extends MenuFragment {

    // TODO hier rainbow gradient erzeugen und rotiertes checkerboard
    private Bitmap moireeImageButtonTile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_menu, container, false);

        initializeMoireeImageButton(root);
        initializeColorSetupButton(root);
        initializeTransformationButton(root);
        initializeSettingsButton(root);
        initializeHelpButton(root);
        initializeAboutButton(root);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(0);

        return root;
    }

    private void initializeMoireeImageButton(View root) {
        int tileSize = getResources().getDimensionPixelSize(R.dimen.moiree_image_button_tile_size);
        // TODO das wird natürlich mittels CheckerboardImageCreator gemacht.
        moireeImageButtonTile = createTilingBitmap(tileSize);
        BitmapDrawable tiling = new BitmapDrawable(getResources(), moireeImageButtonTile);
        tiling.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        CardView moireeImageCard = (CardView) root.findViewById(R.id.main_menu_moiree_image_card);
        ImageView moireeImageBackgroundView1 = (ImageView) moireeImageCard.findViewById(R.id.main_menu_moiree_image_background1);
        ImageView moireeImageBackgroundView2 = (ImageView) moireeImageCard.findViewById(R.id.main_menu_image_background2);
        moireeImageBackgroundView1.setImageDrawable(tiling);
        moireeImageBackgroundView2.setImageDrawable(tiling);

        moireeImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoireeImageSetupMenu();
            }
        });
    }

    private static Bitmap createTilingBitmap(int tileSize) {
        Bitmap bitmap = Bitmap.createBitmap(tileSize * 2, tileSize * 2, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(210, 210, 210));
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, tileSize - 1, tileSize - 1, paint);
        canvas.drawRect(tileSize, tileSize, 2 * tileSize - 1, 2 * tileSize - 1, paint);
        return bitmap;
    }

    private void initializeColorSetupButton(View root) {
        CardView colorSetupCard = (CardView) root.findViewById(R.id.main_menu_colors_card);

        int[] colorSteps = {Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED};
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colorSteps);
        rainbow.setAlpha(100);
//        rainbow.setSize(colorSetupCard.getWidth(), colorSetupCard.getHeight());

        ImageView colorSetupBackgroundView = (ImageView) colorSetupCard.findViewById(R.id.main_menu_colors_background);
        colorSetupBackgroundView.setImageDrawable(rainbow);

        colorSetupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorSetupMenu();
            }
        });
    }

    private void initializeTransformationButton(View root) {
        CardView moireeTransformationSetupButton = (CardView) root.findViewById(R.id.main_menu_transformation_card);
        moireeTransformationSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoireeTransformationSetupMenu();
            }
        });
    }

    private void initializeSettingsButton(View root) {
        root.findViewById(R.id.main_menu_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInputSettingsSubMenu();
            }
        });
    }

    private void initializeHelpButton(View root) {
        root.findViewById(R.id.main_menu_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO nur ein test
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=QAja2jp1VjE")));
            }
        });
    }

    private void initializeAboutButton(View root) {
        root.findViewById(R.id.main_menu_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }


    private void openMoireeImageSetupMenu() {
        openSubMenu(new ImageSetupMenu(), "moireeImageSetup");
    }

    private void openColorSetupMenu() {
        openSubMenu(new MoireeColorsSetupMenu(), "moireeColorsSetup");
    }

    private void openMoireeTransformationSetupMenu() {
        openSubMenu(new TransformationSetupMenu(), "moireeTransformationSetup");
    }

    private void openInputSettingsSubMenu() {
        openSubMenu(new SettingsMenu(), "inputSettings");
    }
}
