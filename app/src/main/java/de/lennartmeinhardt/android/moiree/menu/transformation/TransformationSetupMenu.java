package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.MoireeTransformation;
import de.lennartmeinhardt.android.moiree.MoireeTransformationHolder;
import de.lennartmeinhardt.android.moiree.R;

public class TransformationSetupMenu extends BaseTransformationSetupFragment {

    private static final String KEY_ROTATION_EXPANDED = "transformationSetup:rotationSetupExpanded";
    private static final String KEY_SCALING_EXPANDED = "transformationSetup:scalingSetupExpanded";
    private static final String KEY_TRANSLATION_EXPANDED = "transformationSetup:translationSetupExpanded";
    private static final boolean DEF_EXPANDED = false;

    private MoireeTransformation moireeTransformation;

    private SharedPreferences preferences;

    private RotationSetupFragment rotationSetupFragment;
    private ScalingSetupFragment scalingSetupFragment;
    private TranslationSetupFragment translationSetupFragment;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_transformation_setup, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.reset_to_identity:
                resetTransformationToIdentity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rotationSetupFragment = (RotationSetupFragment) getChildFragmentManager().findFragmentById(R.id.fragment_rotation_setup);
        scalingSetupFragment = (ScalingSetupFragment) getChildFragmentManager().findFragmentById(R.id.fragment_scaling_setup);
        translationSetupFragment = (TranslationSetupFragment) getChildFragmentManager().findFragmentById(R.id.fragment_translation_setup);

        boolean rotationExpanded = preferences.getBoolean(KEY_ROTATION_EXPANDED, DEF_EXPANDED);
        boolean scalingExpanded = preferences.getBoolean(KEY_SCALING_EXPANDED, DEF_EXPANDED);
        boolean translationExpanded = preferences.getBoolean(KEY_TRANSLATION_EXPANDED, DEF_EXPANDED);

        rotationSetupFragment.setExpanded(rotationExpanded);
        scalingSetupFragment.setExpanded(scalingExpanded);
        translationSetupFragment.setExpanded(translationExpanded);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moireeTransformation = ((MoireeTransformationHolder) getActivity()).getMoireeTransformation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
}
