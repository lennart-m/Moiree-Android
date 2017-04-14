package de.lennartmeinhardt.android.moiree.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.MoireeColors;
import de.lennartmeinhardt.android.moiree.MoireeColorsHolder;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarter;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarterHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.HsbColorPicker;

public class MoireeColorsSetupMenu extends MenuFragment {

    private static final String KEY_FOREGROUND_SETUP_EXPANDED = "moireeColorsSetup:foregroundSetupExpanded";
    private static final String KEY_BACKGROUND_SETUP_EXPANDED = "moireeColorsSetup:backgroundSetupExpanded";

    private MoireeColors moireeColors;
    private MoireeTransitionStarter moireeTransitionStarter;

    private SharedPreferences preferences;

    private ExpandableView foregroundSetupExpandable;
    private ExpandableView backgroundSetupExpandable;

    private ImageView foregroundPreview;
    private HsbColorPicker foregroundColorPicker;

    private ImageView backgroundPreview;
    private HsbColorPicker backgroundColorPicker;

    private int defaultForegroundColor;
    private int defaultBackgroundColor;

    private boolean userExpandable;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        defaultBackgroundColor = ResourcesCompat.getColor(getResources(), R.color.moiree_background_default, getActivity().getTheme());
        defaultForegroundColor = ResourcesCompat.getColor(getResources(), R.color.moiree_foreground_default, getActivity().getTheme());

        userExpandable = getResources().getBoolean(R.bool.color_setup_user_expandable);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moiree_colors_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initializeForegroundColorSetup(view);
        initializeBackgroundColorSetup(view);

        boolean foregroundSetupExpanded;
        boolean backgroundSetupExpanded;
        if(userExpandable) {
            foregroundSetupExpanded = preferences.getBoolean(KEY_FOREGROUND_SETUP_EXPANDED, false);
            backgroundSetupExpanded = preferences.getBoolean(KEY_BACKGROUND_SETUP_EXPANDED, false);
        } else {
            foregroundSetupExpanded = true;
            backgroundSetupExpanded = true;
        }

        foregroundSetupExpandable.setExpanded(foregroundSetupExpanded);
        backgroundSetupExpandable.setExpanded(backgroundSetupExpanded);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_colors_setup, menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.swap_colors:
                swapColors();
                return true;
            case R.id.reset_colors:
                resetColorsToDefault();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void initializeForegroundColorSetup(View rootView) {
        View foregroundCard = rootView.findViewById(R.id.foreground_color_setup_card);
        foregroundSetupExpandable = (ExpandableView) foregroundCard.findViewById(R.id.expandable_view);

        View header = foregroundSetupExpandable.findHeaderView();
        header.setFocusable(userExpandable);
        header.setClickable(userExpandable);
        if(userExpandable)
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
        foregroundColorPicker = (HsbColorPicker) foregroundSetupExpandable.findContentView().findViewById(R.id.color_picker);
        foregroundColorPicker.setOnColorSelectionChangedListener(new HsbColorPicker.OnColorSelectionChangedListener() {
            @Override
            public void onColorSelectionChanged(HsbColorPicker colorPicker, int color, boolean fromUser) {
                if(fromUser)
                    moireeColors.setForegroundColor(color);
            }
        });
    }

    private void initializeBackgroundColorSetup(View rootView) {
        View backgroundCard = rootView.findViewById(R.id.background_color_setup_card);
        backgroundSetupExpandable = (ExpandableView) backgroundCard.findViewById(R.id.expandable_view);

        View header = backgroundSetupExpandable.findHeaderView();
        header.setFocusable(userExpandable);
        header.setClickable(userExpandable);
        if(userExpandable) {
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginMenuBoundsTransition();
                    backgroundSetupExpandable.toggleExpanded();
                }
            });
        }

        TextView title = (TextView) header.findViewById(R.id.header_title);
        title.setText(R.string.background);
        backgroundPreview = (ImageView) header.findViewById(R.id.header_preview);
        backgroundColorPicker = (HsbColorPicker) backgroundSetupExpandable.findContentView().findViewById(R.id.color_picker);
        backgroundColorPicker.setOnColorSelectionChangedListener(new HsbColorPicker.OnColorSelectionChangedListener() {
            @Override
            public void onColorSelectionChanged(HsbColorPicker colorPicker, int color, boolean fromUser) {
                if(fromUser)
                    moireeColors.setBackgroundColor(color);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(userExpandable) {
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putBoolean(KEY_FOREGROUND_SETUP_EXPANDED, foregroundSetupExpandable.isExpanded());
            preferencesEditor.putBoolean(KEY_BACKGROUND_SETUP_EXPANDED, backgroundSetupExpandable.isExpanded());
            preferencesEditor.apply();
        }

        moireeColors.removeMoireeColorsListener(previewColorsUpdater);

//        android.util.Log.d("Moiree", String.format("colors: fg=#%s, bg=#%s", Integer.toHexString(moireeColors.getForegroundColor()), Integer.toHexString(moireeColors.getBackgroundColor())));
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

    private void resetColorsToDefault() {
        setColorsWithTransition(defaultForegroundColor, defaultBackgroundColor);
    }
}
