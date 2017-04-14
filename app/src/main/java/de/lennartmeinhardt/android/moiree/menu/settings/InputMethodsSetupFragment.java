package de.lennartmeinhardt.android.moiree.menu.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.Expandable;
import de.lennartmeinhardt.android.moiree.MoireeInputMethods;
import de.lennartmeinhardt.android.moiree.MoireeInputMethodsHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class InputMethodsSetupFragment extends MenuFragment {

    private MoireeInputMethods moireeInputMethods;

    private ExpandableView expandableView;

    private IntValueSetup rotationSensitivitySetup;
    private IntValueSetup scalingSensitivitySetup;
    private IntValueSetup translationSensitivitySetup;

    private ExpandableView rotationSetupExpandable;
    private ExpandableView scalingSetupExpandable;
    private ExpandableView translationSetupExpandable;

    private Switch rotationInputSwitch;
    private Switch scalingInputSwitch;
    private Switch translationInputSwitch;

    private Button rotationSensitivityResetButton;
    private Button scalingSensitivityResetButton;
    private Button translationSensitivityResetButton;

    private int defaultSensitivityPercents;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defaultSensitivityPercents = getResources().getInteger(R.integer.sensitivity_default_percents);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_methods_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);
        final View indicatorView = view.findViewById(R.id.header_expanded_indicator);
        expandableView.findHeaderView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transition transition = createMenuBoundsAndHeaderIndicatorTransition();
                beginMenuTransition(transition);
                expandableView.toggleExpanded();
            }
        });

        final int arrowCollapsedDegrees = getResources().getInteger(R.integer.indicator_arrow_collapsed_rotation);
        expandableView.addOnExpandedStateChangedListener(new Expandable.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(Expandable expandable, boolean expanded) {
                indicatorView.setRotation(expandable.isExpanded() ? 0 : arrowCollapsedDegrees);
            }
        });
        TextView title = (TextView) expandableView.findViewById(R.id.header_title);
        title.setText(R.string.input_methods);

        initializeRotationInputSetupViews(view);
        initializeScalingInputSetupViews(view);
        initializeTranslationInputSetupViews(view);
    }

    private void initializeRotationInputSetupViews(View rootView) {
        rotationSetupExpandable = (ExpandableView) rootView.findViewById(R.id.rotation_input_setup_root);
        rotationInputSwitch = (Switch) rotationSetupExpandable.findHeaderView().findViewById(R.id.input_method_switch);
        rotationSensitivitySetup = (IntValueSetup) rotationSetupExpandable.findViewById(R.id.sensitivity_value_setup);
        rotationSensitivityResetButton = (Button) rotationSetupExpandable.findViewById(R.id.reset_button);

        rotationSensitivitySetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                float sensitivity = valueToSensitivity(value);
                moireeInputMethods.setRotationSensitivity(sensitivity);
                updateRotationUI(true);
            }
        });
        rotationInputSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beginMenuBoundsTransition();
                moireeInputMethods.setRotationInputEnabled(isChecked);
                updateRotationUI(false);
            }
        });

        TextView description = (TextView) rotationSetupExpandable.findViewById(R.id.description_text);
        description.setText(R.string.rotation_input_description);

        rotationSensitivityResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotationSensitivitySetup.setValue(defaultSensitivityPercents);
            }
        });
        rotationInputSwitch.setText(R.string.rotation_input_name);
    }

    private void updateRotationUI(boolean fromValueSetup) {
        boolean inputEnabled = moireeInputMethods.isRotationInputEnabled();
        float sensitivity = moireeInputMethods.getRotationSensitivity();
        int sensitivityPercents = sensitivityToValue(sensitivity);

        rotationInputSwitch.setChecked(inputEnabled);
        rotationSetupExpandable.setExpanded(inputEnabled);
        rotationSensitivityResetButton.setEnabled(sensitivityPercents != defaultSensitivityPercents);

        if(! fromValueSetup)
            rotationSensitivitySetup.setValue(sensitivityPercents);
    }

    private void initializeScalingInputSetupViews(View rootView) {
        scalingSetupExpandable = (ExpandableView) rootView.findViewById(R.id.scaling_input_setup_root);
        scalingInputSwitch = (Switch) scalingSetupExpandable.findHeaderView().findViewById(R.id.input_method_switch);
        scalingSensitivitySetup = (IntValueSetup) scalingSetupExpandable.findViewById(R.id.sensitivity_value_setup);
        scalingSensitivityResetButton = (Button) scalingSetupExpandable.findViewById(R.id.reset_button);

        scalingSensitivitySetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                float sensitivity = valueToSensitivity(value);
                moireeInputMethods.setScalingSensitivity(sensitivity);
                updateScalingUI(true);
            }
        });

        TextView description = (TextView) scalingSetupExpandable.findViewById(R.id.description_text);
        description.setText(R.string.scaling_input_description);

        scalingInputSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beginMenuBoundsTransition();
                moireeInputMethods.setScalingInputEnabled(isChecked);
                updateScalingUI(false);
            }
        });

        scalingSensitivityResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scalingSensitivitySetup.setValue(defaultSensitivityPercents);
            }
        });
        scalingInputSwitch.setText(R.string.scaling_input_name);
    }

    private void updateScalingUI(boolean fromValueSetup) {
        boolean inputEnabled = moireeInputMethods.isScalingInputEnabled();
        float sensitivity = moireeInputMethods.getScalingSensitivity();
        int sensitivityPercents = sensitivityToValue(sensitivity);

        scalingInputSwitch.setChecked(inputEnabled);
        scalingSetupExpandable.setExpanded(inputEnabled);
        scalingSensitivityResetButton.setEnabled(sensitivityPercents != defaultSensitivityPercents);

        if(! fromValueSetup)
            scalingSensitivitySetup.setValue(sensitivityPercents);
    }

    private void initializeTranslationInputSetupViews(View rootView) {
        translationSetupExpandable = (ExpandableView) rootView.findViewById(R.id.translation_input_setup_root);
        translationInputSwitch = (Switch) translationSetupExpandable.findHeaderView().findViewById(R.id.input_method_switch);
        translationSensitivitySetup = (IntValueSetup) translationSetupExpandable.findViewById(R.id.sensitivity_value_setup);
        translationSensitivityResetButton = (Button) translationSetupExpandable.findViewById(R.id.reset_button);

        translationSensitivitySetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                float sensitivity = valueToSensitivity(value);
                moireeInputMethods.setTranslationSensitivity(sensitivity);
                updateTranslationUI(true);
            }
        });

        TextView description = (TextView) translationSetupExpandable.findViewById(R.id.description_text);
        description.setText(R.string.translation_input_description);

        translationInputSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beginMenuBoundsTransition();
                moireeInputMethods.setTranslationInputEnabled(isChecked);
                updateTranslationUI(false);
            }
        });

        translationSensitivityResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translationSensitivitySetup.setValue(defaultSensitivityPercents);
            }
        });
        translationInputSwitch.setText(R.string.translation_input_name);
    }

    private void updateTranslationUI(boolean fromValueSetup) {
        boolean inputEnabled = moireeInputMethods.isTranslationInputEnabled();
        float sensitivity = moireeInputMethods.getTranslationSensitivity();
        int sensitivityPercents = sensitivityToValue(sensitivity);

        translationInputSwitch.setChecked(inputEnabled);
        translationSetupExpandable.setExpanded(inputEnabled);
        translationSensitivityResetButton.setEnabled(sensitivityPercents != defaultSensitivityPercents);

        if(! fromValueSetup)
            translationSensitivitySetup.setValue(sensitivityPercents);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeInputMethods = ((MoireeInputMethodsHolder) getActivity()).getMoireeInputMethods();

        updateRotationUI(false);
        updateScalingUI(false);
        updateTranslationUI(false);
    }

    private int sensitivityToValue(float sensitivity) {
        return Math.round(sensitivity * 100);
    }

    private float valueToSensitivity(int value) {
        return value / 100f;
    }

    public ExpandableView getExpandableView() {
        return expandableView;
    }
}
