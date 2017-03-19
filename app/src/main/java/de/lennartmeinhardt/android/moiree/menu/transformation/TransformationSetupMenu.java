package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.lennartmeinhardt.android.moiree.MoireeTransformation;
import de.lennartmeinhardt.android.moiree.MoireeTransformationHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.transition.SimpleChangeTransform;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_transformation_setup, container, false);

        Button resetButton = (Button) rootView.findViewById(R.id.transformation_setup_set_to_identity_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setToIdentity();
            }
        });

        rotationSetupFragment = (RotationSetupFragment) getChildFragmentManager().findFragmentByTag("rotationSetup");
        scalingSetupFragment = (ScalingSetupFragment) getChildFragmentManager().findFragmentByTag("scalingSetup");
        translationSetupFragment = (TranslationSetupFragment) getChildFragmentManager().findFragmentByTag("translationSetup");

        boolean rotationExpanded = preferences.getBoolean(KEY_ROTATION_EXPANDED, DEF_EXPANDED);
        boolean scalingExpanded = preferences.getBoolean(KEY_SCALING_EXPANDED, DEF_EXPANDED);
        boolean translationExpanded = preferences.getBoolean(KEY_TRANSLATION_EXPANDED, DEF_EXPANDED);

        rotationSetupFragment.setExpanded(rotationExpanded);
        scalingSetupFragment.setExpanded(scalingExpanded);
        translationSetupFragment.setExpanded(translationExpanded);

        return rootView;
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


    // TODO woanders hin schieben
    public static Transition createExpandTransition() {
        return new TransitionSet()
                .addTransition(createMenuBoundsTransition())
                .addTransition(new SimpleChangeTransform().addTarget(R.id.expandable_view_expanded_indicator))
                .setOrdering(TransitionSet.ORDERING_TOGETHER);
    }
}
