package de.lennartmeinhardt.android.moiree.menu.color;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.lennartmeinhardt.android.moiree.MoireeColors;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.DialogSaveColorsBinding;
import de.lennartmeinhardt.android.moiree.databinding.MenuMoireeColorsSetupBinding;
import de.lennartmeinhardt.android.moiree.util.HsbColorPicker;
import de.lennartmeinhardt.android.moiree.util.db.DatabaseInserterTask;
import de.lennartmeinhardt.android.moiree.util.db.Inserter;
import de.lennartmeinhardt.android.moiree.util.db.room.AppDatabase;
import de.lennartmeinhardt.android.moiree.util.db.room.PersistableMoireeColors;

public class MoireeColorsSetupMenu extends BaseMoireeColorsSetupFragment {

    private static final String KEY_FOREGROUND_SETUP_EXPANDED = "moireeColorsSetup:foregroundSetupExpanded";
    private static final String KEY_BACKGROUND_SETUP_EXPANDED = "moireeColorsSetup:backgroundSetupExpanded";

    private boolean userExpandable;

    private MenuMoireeColorsSetupBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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

    private void openColorSetupDialog(final ObservableInt colorToSet, String initialInput) {
        @SuppressLint("InflateParams")
        View dialogContent = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_color, null);
        final EditText editHex = dialogContent.findViewById(R.id.edit_hex);

        if(initialInput != null) {
            editHex.setText(initialInput.toUpperCase());
            editHex.setSelection(initialInput.length());
        }

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.custom_color)
                .setView(dialogContent)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = editHex.getText().toString();
                        onHexInputConfirmed(colorToSet, input);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void onHexInputConfirmed(final ObservableInt colorToSet, final String input) {
        String hexToParse;

        // hex in the form #xyz is converted to #xxyyzz
        if(input.length() == 3)
            hexToParse = "#" + input.charAt(0) + input.charAt(0) + input.charAt(1) + input.charAt(1) + input.charAt(2) + input.charAt(2);
        else
            hexToParse = "#" + input;

        try {
            int argb = Color.parseColor(hexToParse);
            colorToSet.set(argb);
        } catch(IllegalArgumentException e) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.input_error)
                    .setMessage(R.string.message_color_input_error)
                    // re-open the dialog on "ok"
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openColorSetupDialog(colorToSet, input);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_colors_setup, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.setMoireeColors(moireeColors);

        bindColorPickersToModelManually();
    }

    /**
     * Colors have to be bound to color pickers manually. It could also be done via xml data binding (see the commented line in color_picker in card_color_setup.xml.
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
                resetColorsToBackup();
                return true;
            case R.id.save_colors:
                openSaveColorsDialog();
                return true;
            case R.id.load_colors:
                openLoadColorsMenu();
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

        binding.foregroundColorSetupCard.editColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorSetupDialog(moireeColors.foregroundColor, Integer.toHexString(moireeColors.foregroundColor.get()).substring(2));
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

        binding.backgroundColorSetupCard.editColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorSetupDialog(moireeColors.backgroundColor, Integer.toHexString(moireeColors.backgroundColor.get()).substring(2));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if(userExpandable) {
            preferences.edit()
                .putBoolean(KEY_FOREGROUND_SETUP_EXPANDED, binding.foregroundColorSetupCard.expandableView.isExpanded())
                .putBoolean(KEY_BACKGROUND_SETUP_EXPANDED, binding.backgroundColorSetupCard.expandableView.isExpanded())
                .apply();
        }
    }

    private void swapColors() {
        setColorsWithTransition(moireeColors.backgroundColor.get(), moireeColors.foregroundColor.get());
    }

    private void openSaveColorsDialog() {
        final DialogSaveColorsBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_save_colors, null, false);

        binding.setMoireeColors(moireeColors);

        new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setTitle(R.string.save_colors)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PersistableMoireeColors persistableMoireeColors = new PersistableMoireeColors();
                        persistableMoireeColors.name = binding.getName();
                        persistableMoireeColors.readDataFromMoireeColors(binding.getMoireeColors());
                        Inserter<PersistableMoireeColors> databaseInserter = AppDatabase.getInstance(getContext()).persistableMoireeColorsDao();
                        new DatabaseInserterTask<>(databaseInserter).execute(persistableMoireeColors);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openLoadColorsMenu() {
        getMenuHolder().openMenuFragment(new LoadMoireeColorsMenu(), "loadMoireeColors");
    }
}
