package de.lennartmeinhardt.android.moiree.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.MoireeColors;
import de.lennartmeinhardt.android.moiree.MoireeColorsHolder;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarter;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarterHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.util.ColorPicker;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;

public class MoireeColorsSetupMenu extends MenuFragment {

    private static final String KEY_FOREGROUND_SETUP_EXPANDED = "moireeColorsSetup:foregroundSetupExpanded";
    private static final String KEY_BACKGROUND_SETUP_EXPANDED = "moireeColorsSetup:backgroundSetupExpanded";

    private MoireeColors moireeColors;
    private MoireeTransitionStarter moireeTransitionStarter;

    private SharedPreferences preferences;

    private ExpandableView foregroundSetupExpandable;
    private ExpandableView backgroundSetupExpandable;

    private ImageView foregroundPreview;
    private ColorPicker foregroundColorPicker;

    private ImageView backgroundPreview;
    private ColorPicker backgroundColorPicker;

    private final MoireeColors.MoireeColorsListener previewColorsUpdater = new MoireeColors.MoireeColorsListener() {
        @Override
        public void onBackgroundColorChanged(int newColor) {
            backgroundPreview.setBackgroundColor(newColor);
            if(! backgroundColorPicker.isUserInputActive())
                backgroundColorPicker.setSelectedColor(newColor);
        }

        @Override
        public void onForegroundColorChanged(int newColor) {
            foregroundPreview.setBackgroundColor(newColor);
            if(! foregroundColorPicker.isUserInputActive())
                foregroundColorPicker.setSelectedColor(newColor);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moiree_colors_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button swapColorsButton = (Button) view.findViewById(R.id.color_setup_swap);
        swapColorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapColors();
            }
        });

        Button resetToDefaultButton = (Button) view.findViewById(R.id.color_setup_reset);
        resetToDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColorsWithTransition(MoireeColors.DEF_FOREGROUND_COLOR, MoireeColors.DEF_BACKGROUND_COLOR);
            }
        });

        initializeForegroundColorSetup(view);
        initializeBackgroundColorSetup(view);

        boolean foregroundSetupExpanded = preferences.getBoolean(KEY_FOREGROUND_SETUP_EXPANDED, false);
        boolean backgroundSetupExpanded = preferences.getBoolean(KEY_BACKGROUND_SETUP_EXPANDED, false);

        foregroundSetupExpandable.setExpanded(foregroundSetupExpanded);
        backgroundSetupExpandable.setExpanded(backgroundSetupExpanded);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MoireeColorsHolder colorsHolderActivity = (MoireeColorsHolder) getActivity();
        moireeColors = colorsHolderActivity.getMoireeColors();
        moireeColors.addAndFireMoireeColorsListener(previewColorsUpdater);

        if(getActivity() instanceof MoireeTransitionStarterHolder) {
            MoireeTransitionStarterHolder moireeTransitionStarterHolder = (MoireeTransitionStarterHolder) getActivity();
            moireeTransitionStarter = moireeTransitionStarterHolder.getMoireeTransitionStarter();
        }
    }

    private void initializeForegroundColorSetup(View rootView) {
        View foregroundCard = rootView.findViewById(R.id.foreground_color_setup_card);
        foregroundSetupExpandable = (ExpandableView) foregroundCard.findViewById(R.id.expandable_view);
        View header = foregroundSetupExpandable.getHeaderView();
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                foregroundSetupExpandable.toggleExpanded();
            }
        });
        TextView title = (TextView) header.findViewById(R.id.header_title);
        title.setText(R.string.foreground);
        foregroundPreview = (ImageView) header.findViewById(R.id.header_preview);
        foregroundPreview.setImageResource(R.drawable.color_preview_mask);
        foregroundColorPicker = (ColorPicker) foregroundSetupExpandable.getContentView().findViewById(R.id.color_picker);
        foregroundColorPicker.setOnColorSelectionChangeListener(new ColorPicker.OnColorSelectionChangeListener() {
            @Override
            public void onColorSelectionChanged(ColorPicker colorPicker, int color, boolean fromUser) {
                if(fromUser && foregroundColorPicker.isUserInputActive())
                    moireeColors.setForegroundColor(color);
            }
        });
    }

    private void initializeBackgroundColorSetup(View rootView) {
        View backgroundCard = rootView.findViewById(R.id.background_color_setup_card);
        backgroundSetupExpandable = (ExpandableView) backgroundCard.findViewById(R.id.expandable_view);
        View header = backgroundSetupExpandable.getHeaderView();
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                backgroundSetupExpandable.toggleExpanded();
            }
        });
        TextView title = (TextView) header.findViewById(R.id.header_title);
        title.setText(R.string.background);
        backgroundPreview = (ImageView) header.findViewById(R.id.header_preview);
        backgroundPreview.setImageResource(R.drawable.color_preview_mask);
        backgroundColorPicker = (ColorPicker) backgroundSetupExpandable.getContentView().findViewById(R.id.color_picker);
        backgroundColorPicker.setOnColorSelectionChangeListener(new ColorPicker.OnColorSelectionChangeListener() {
            @Override
            public void onColorSelectionChanged(ColorPicker colorPicker, int color, boolean fromUser) {
                if(fromUser && backgroundColorPicker.isUserInputActive())
                    moireeColors.setBackgroundColor(color);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(KEY_FOREGROUND_SETUP_EXPANDED, foregroundSetupExpandable.isExpanded());
        preferencesEditor.putBoolean(KEY_BACKGROUND_SETUP_EXPANDED, backgroundSetupExpandable.isExpanded());
        preferencesEditor.apply();

        moireeColors.removeMoireeColorsListener(previewColorsUpdater);
    }

    private void swapColors() {
        setColorsWithTransition(moireeColors.getBackgroundColor(), moireeColors.getForegroundColor());
    }

    private void setColorsWithTransition(int foregroundColor, int backgroundColor) {
        if(moireeTransitionStarter != null)
            moireeTransitionStarter.beginColorTransitionIfWanted();

        moireeColors.setBackgroundColor(backgroundColor);
        moireeColors.setForegroundColor(foregroundColor);
    }
}
