package de.lennartmeinhardt.android.moiree.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.MoireeColors;
import de.lennartmeinhardt.android.moiree.MoireeColorsHolder;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarter;
import de.lennartmeinhardt.android.moiree.MoireeTransitionStarterHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.MenuMoireeColorsSetupBinding;
import de.lennartmeinhardt.android.moiree.util.HsbColorPicker;

public class MoireeColorsSetupMenu extends MenuFragment {

    private static final String KEY_FOREGROUND_SETUP_EXPANDED = "moireeColorsSetup:foregroundSetupExpanded";
    private static final String KEY_BACKGROUND_SETUP_EXPANDED = "moireeColorsSetup:backgroundSetupExpanded";

    private MoireeColors moireeColors;
    private MoireeTransitionStarter moireeTransitionStarter;

    private SharedPreferences preferences;

    private int defaultForegroundColor;
    private int defaultBackgroundColor;

    private boolean userExpandable;

    private MenuMoireeColorsSetupBinding binding;


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
        binding = DataBindingUtil.inflate(inflater, R.layout.menu_moiree_colors_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initializeForegroundColorSetup();
        initializeBackgroundColorSetup();

        boolean foregroundSetupExpanded;
        boolean backgroundSetupExpanded;
        if(userExpandable) {
            foregroundSetupExpanded = preferences.getBoolean(KEY_FOREGROUND_SETUP_EXPANDED, false);
            backgroundSetupExpanded = preferences.getBoolean(KEY_BACKGROUND_SETUP_EXPANDED, false);
        } else {
            foregroundSetupExpanded = true;
            backgroundSetupExpanded = true;
        }

        binding.foregroundColorSetupCard.expandableView.setExpanded(foregroundSetupExpanded);
        binding.backgroundColorSetupCard.expandableView.setExpanded(backgroundSetupExpanded);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
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
        binding.setMoireeColors(moireeColors);

        bindColorPickersToModelManually();

        if(getActivity() instanceof MoireeTransitionStarterHolder) {
            MoireeTransitionStarterHolder moireeTransitionStarterHolder = (MoireeTransitionStarterHolder) getActivity();
            moireeTransitionStarter = moireeTransitionStarterHolder.getMoireeTransitionStarter();
        }
    }

    /**
     * Colors have to be bound to color pickers manually. It could also be done via xml data binding (see the commented line in color_picker in color_setup_card.xml.
     * But then sometimes a bug occurs when selecting black (for example). Black has HSV values 0, 0, 0 and so any currently selected hue will be overriden by red.
     * This also happens if in ColorPicker's setSelectedColor method there is a check for ! isUserInputActive.
     * The reason for this is that the system executes the bindings later than when the change happens (see binding.executePendingBindings()).
     * So a user change in color will update the model, and the model will update the colorpicker again; but at this time the color picker cannot know whether the change event came from the user or not, because isUserInputActive() always returns false when the binding is executed.
     *
     * Here the color picker's selected color is bound to the {@link MoireeColors}' values, and first initialization is done as well.
     */
    private void bindColorPickersToModelManually() {
        moireeColors.foregroundColor.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(! binding.foregroundColorSetupCard.colorPicker.isUserInputActive()) {
                    binding.foregroundColorSetupCard.colorPicker.setSelectedColor(moireeColors.foregroundColor.get());
                }
            }
        });
        binding.foregroundColorSetupCard.colorPicker.setSelectedColor(moireeColors.foregroundColor.get());

        moireeColors.backgroundColor.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(! binding.backgroundColorSetupCard.colorPicker.isUserInputActive()) {
                    binding.backgroundColorSetupCard.colorPicker.setSelectedColor(moireeColors.backgroundColor.get());
                }
            }
        });
        binding.backgroundColorSetupCard.colorPicker.setSelectedColor(moireeColors.backgroundColor.get());
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

    private void initializeForegroundColorSetup() {
        if(userExpandable)
            binding.foregroundColorSetupCard.colorPreviewHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginMenuBoundsTransition();
                    binding.foregroundColorSetupCard.expandableView.toggleExpanded();
                }
            });

        binding.foregroundColorSetupCard.colorPicker.setOnColorSelectionChangedListener(new HsbColorPicker.OnColorSelectionChangedListener() {
            @Override
            public void onColorSelectionChanged(HsbColorPicker colorPicker, int color, boolean fromUser) {
                if(fromUser) {
                    binding.getMoireeColors().foregroundColor.set(color);
                }
            }
        });
    }

    private void initializeBackgroundColorSetup() {
        if(userExpandable) {
            binding.backgroundColorSetupCard.colorPreviewHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginMenuBoundsTransition();
                    binding.backgroundColorSetupCard.expandableView.toggleExpanded();
                }
            });
        }

        binding.backgroundColorSetupCard.colorPicker.setOnColorSelectionChangedListener(new HsbColorPicker.OnColorSelectionChangedListener() {
            @Override
            public void onColorSelectionChanged(HsbColorPicker colorPicker, int color, boolean fromUser) {
                if(fromUser) {
                    binding.getMoireeColors().backgroundColor.set(color);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(userExpandable) {
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putBoolean(KEY_FOREGROUND_SETUP_EXPANDED, binding.foregroundColorSetupCard.expandableView.isExpanded());
            preferencesEditor.putBoolean(KEY_BACKGROUND_SETUP_EXPANDED, binding.backgroundColorSetupCard.expandableView.isExpanded());
            preferencesEditor.apply();
        }
    }

    private void swapColors() {
        setColorsWithTransition(moireeColors.backgroundColor.get(), moireeColors.foregroundColor.get());
    }

    private void setColorsWithTransition(int foregroundColor, int backgroundColor) {
        if(moireeTransitionStarter != null)
            moireeTransitionStarter.beginColorTransitionIfWanted();

        moireeColors.backgroundColor.set(backgroundColor);
        moireeColors.foregroundColor.set(foregroundColor);
    }

    private void resetColorsToDefault() {
        setColorsWithTransition(defaultForegroundColor, defaultBackgroundColor);
    }
}
