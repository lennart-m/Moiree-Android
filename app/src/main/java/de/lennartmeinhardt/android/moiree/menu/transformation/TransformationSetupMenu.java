package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.DialogSaveTransformationBinding;
import de.lennartmeinhardt.android.moiree.util.db.DatabaseInserterTask;
import de.lennartmeinhardt.android.moiree.util.db.Inserter;
import de.lennartmeinhardt.android.moiree.util.db.room.AppDatabase;
import de.lennartmeinhardt.android.moiree.util.db.room.PersistableMoireeTransformation;

public class TransformationSetupMenu extends BaseTransformationSetupFragment {

    private static final String KEY_ROTATION_EXPANDED = "transformationSetup:rotationSetupExpanded";
    private static final String KEY_SCALING_EXPANDED = "transformationSetup:scalingSetupExpanded";
    private static final String KEY_TRANSLATION_EXPANDED = "transformationSetup:translationSetupExpanded";

    private RotationSetupFragment rotationSetupFragment;
    private ScalingSetupFragment scalingSetupFragment;
    private TranslationSetupFragment translationSetupFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_transformation_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rotationSetupFragment = (RotationSetupFragment) getChildFragmentManager().findFragmentById(R.id.fragment_rotation_setup);
        scalingSetupFragment = (ScalingSetupFragment) getChildFragmentManager().findFragmentById(R.id.fragment_scaling_setup);
        translationSetupFragment = (TranslationSetupFragment) getChildFragmentManager().findFragmentById(R.id.fragment_translation_setup);

        boolean defaultExpanded = getResources().getBoolean(R.bool.transformation_setup_card_default_expanded);
        boolean rotationExpanded = preferences.getBoolean(KEY_ROTATION_EXPANDED, defaultExpanded);
        boolean scalingExpanded = preferences.getBoolean(KEY_SCALING_EXPANDED, defaultExpanded);
        boolean translationExpanded = preferences.getBoolean(KEY_TRANSLATION_EXPANDED, defaultExpanded);

        rotationSetupFragment.setExpanded(rotationExpanded);
        scalingSetupFragment.setExpanded(scalingExpanded);
        translationSetupFragment.setExpanded(translationExpanded);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_transformation_setup, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.reset_to_identity:
                resetTransformationToIdentity();
                return true;
            case R.id.save_transformation:
                openSaveTransformationDialog();
                return true;
            case R.id.load_transformation:
                openLoadTransformationMenu();
                return true;
            case R.id.revert_transformation:
                resetTransformationToBackup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(KEY_ROTATION_EXPANDED, rotationSetupFragment.isExpanded());
        preferencesEditor.putBoolean(KEY_SCALING_EXPANDED, scalingSetupFragment.isExpanded());
        preferencesEditor.putBoolean(KEY_TRANSLATION_EXPANDED, translationSetupFragment.isExpanded());
        preferencesEditor.apply();
    }

    private void resetTransformationToIdentity() {
        beginMenuBoundsTransition();
        beginMoireeTransformationTransitionIfWanted();
        moireeTransformation.setToIdentity();
    }

    private void openSaveTransformationDialog() {
        final DialogSaveTransformationBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_save_transformation, null, false);

        binding.setMoireeTransformation(moireeTransformation);

        new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .setTitle(R.string.save_transformation)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PersistableMoireeTransformation persistableMoireeTransformation = new PersistableMoireeTransformation();
                        persistableMoireeTransformation.readDataFromTransformation(moireeTransformation);
                        persistableMoireeTransformation.transformationName = binding.getName();

                        Inserter<PersistableMoireeTransformation> databaseInserter = AppDatabase.getInstance(getContext()).persistableMoireeTransformationDao();
                        new DatabaseInserterTask<>(databaseInserter).execute(persistableMoireeTransformation);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void openLoadTransformationMenu() {
        getMenuHolder().openMenuFragment(new LoadTransformationMenu(), "loadTransformation");
    }
}
