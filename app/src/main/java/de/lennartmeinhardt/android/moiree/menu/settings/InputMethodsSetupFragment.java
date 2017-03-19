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

import de.lennartmeinhardt.android.moiree.MoireeInputMethods;
import de.lennartmeinhardt.android.moiree.MoireeInputMethodsHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.menu.transformation.TransformationSetupMenu;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class InputMethodsSetupFragment extends MenuFragment {

    private MoireeInputMethods moireeInputMethods;

    private IntValueSetup rotationSensitivitySetup;
    private IntValueSetup scalingSensitivitySetup;
    private IntValueSetup translationSensitivitySetup;

    private ExpandableView rotationSetupRoot;
    private ExpandableView scalingSetupRoot;
    private ExpandableView translationSetupRoot;

    private Button rotationSensitivityResetButton;
    private Button scalingSensitivityResetButton;
    private Button translationSensitivityResetButton;

    private Switch rotationInputSwitch;
    private Switch scalingInputSwitch;
    private Switch translationInputSwitch;


    private final IntValueSetup.OnIntValueUpdatedListener sensitivitySetter = new IntValueSetup.OnIntValueUpdatedListener.Adapter() {
        @Override
        public void onIntValueSelected(IntValueSetup intValueSetup, int value) {
            float sensitivity = valueToSensitivity(value);
            if(intValueSetup == rotationSensitivitySetup)
                moireeInputMethods.setRotationSensitivity(sensitivity);
            else if(intValueSetup == scalingSensitivitySetup)
                moireeInputMethods.setScalingSensitivity(sensitivity);
            else if(intValueSetup == translationSensitivitySetup)
                moireeInputMethods.setTranslationSensitivity(sensitivity);
        }
    };

    private final MoireeInputMethods.MoireeInputMethodListener uiUpdater = new MoireeInputMethods.MoireeInputMethodListener.Adapter() {
        @Override
        public void onRotationInputMethodChanged(boolean enabled, float sensitivity) {
            int value = sensitivityToValue(sensitivity);
            if(! rotationSensitivitySetup.isUserInputActive())
                rotationSensitivitySetup.setValue(value);

            rotationInputSwitch.setChecked(enabled);
            rotationSetupRoot.setExpanded(enabled);
        }

        @Override
        public void onScalingInputMethodChanged(boolean enabled, float sensitivity) {
            int value = sensitivityToValue(sensitivity);
            if(! scalingSensitivitySetup.isUserInputActive())
                scalingSensitivitySetup.setValue(value);

            scalingInputSwitch.setChecked(enabled);
            scalingSetupRoot.setExpanded(enabled);
        }

        @Override
        public void onTranslationInputMethodChanged(boolean enabled, float sensitivity) {
            int value = sensitivityToValue(sensitivity);
            if(! translationSensitivitySetup.isUserInputActive())
                translationSensitivitySetup.setValue(value);

            translationInputSwitch.setChecked(enabled);
            translationSetupRoot.setExpanded(enabled);
        }
    };

    private final View.OnClickListener sensitivityResetter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            beginMenuBoundsTransition();
            if(v == rotationSensitivityResetButton)
                moireeInputMethods.setRotationSensitivityToDefault();
            else if(v == scalingSensitivityResetButton)
                moireeInputMethods.setScalingSensitivityToDefault();
            else if(v == translationSensitivityResetButton)
                moireeInputMethods.setTranslationSensitivityToDefault();
        }
    };

    private final CompoundButton.OnCheckedChangeListener inputMethodEnabler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            beginMenuBoundsTransition();
            if(buttonView == rotationInputSwitch)
                moireeInputMethods.setRotationInputEnabled(isChecked);
            else if(buttonView == scalingInputSwitch)
                moireeInputMethods.setScalingInputEnabled(isChecked);
            else if(buttonView == translationInputSwitch)
                moireeInputMethods.setTranslationInputEnabled(isChecked);
        }
    };

    private final IntValueSetup.IntStringConverter textConverter = new IntValueSetup.IntStringConverter() {
        @Override
        public String intToText(int value) {
            return getResources().getString(R.string.percentage_formatter, value);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_input_methods_setup, container, false);
        final ExpandableView expandableView = (ExpandableView) rootView.findViewById(R.id.expandable_view);
        final View indicatorView = rootView.findViewById(R.id.expandable_view_expanded_indicator);
        expandableView.findViewById(R.id.expandable_view_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transition transition = TransformationSetupMenu.createExpandTransition();
                beginMenuBoundsTransition(transition);
                expandableView.toggleExpanded();
            }
        });
        expandableView.setOnExpandedStateChangedListener(new ExpandableView.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(ExpandableView expandableView, boolean expanded) {
                indicatorView.setRotation(expandableView.isExpanded() ? 0 : -90);
            }
        });
        TextView title = (TextView) expandableView.findViewById(R.id.expandable_view_header_title);
        title.setText(R.string.input_methods);

        initializeRotationInputSetupViews(rootView);
        initializeScalingInputSetupViews(rootView);
        initializeTranslationInputSetupViews(rootView);

        return rootView;
    }

    private void initializeRotationInputSetupViews(View rootView) {
        rotationSetupRoot = (ExpandableView) rootView.findViewById(R.id.rotation_input_setup_root);
        rotationInputSwitch = (Switch) rotationSetupRoot.findViewById(R.id.input_method_switch);
        rotationSensitivitySetup = (IntValueSetup) rotationSetupRoot.findViewById(R.id.sensitivity_value_setup);
        rotationSensitivityResetButton = (Button) rotationSetupRoot.findViewById(R.id.reset_button);

        rotationSensitivitySetup.setIntStringConverter(textConverter);
        rotationSensitivitySetup.setOnIntValueSelectedListener(sensitivitySetter);

        rotationInputSwitch.setOnCheckedChangeListener(inputMethodEnabler);

        TextView description = (TextView) rotationSetupRoot.findViewById(R.id.description_text);
        description.setText(R.string.rotation_input_description);

        rotationSensitivityResetButton.setOnClickListener(sensitivityResetter);
        rotationInputSwitch.setText(R.string.rotation_input_name);
    }

    private void initializeScalingInputSetupViews(View rootView) {
        scalingSetupRoot = (ExpandableView) rootView.findViewById(R.id.scaling_input_setup_root);
        scalingInputSwitch = (Switch) scalingSetupRoot.findViewById(R.id.input_method_switch);
        scalingSensitivitySetup = (IntValueSetup) scalingSetupRoot.findViewById(R.id.sensitivity_value_setup);
        scalingSensitivityResetButton = (Button) scalingSetupRoot.findViewById(R.id.reset_button);

        scalingSensitivitySetup.setIntStringConverter(textConverter);
        scalingSensitivitySetup.setOnIntValueSelectedListener(sensitivitySetter);

        TextView description = (TextView) scalingSetupRoot.findViewById(R.id.description_text);
        description.setText(R.string.scaling_input_description);

        scalingInputSwitch.setOnCheckedChangeListener(inputMethodEnabler);

        scalingSensitivityResetButton.setOnClickListener(sensitivityResetter);
        scalingInputSwitch.setText(R.string.scaling_input_name);
    }

    private void initializeTranslationInputSetupViews(View rootView) {
        translationSetupRoot = (ExpandableView) rootView.findViewById(R.id.translation_input_setup_root);
        translationInputSwitch = (Switch) translationSetupRoot.findViewById(R.id.input_method_switch);
        translationSensitivitySetup = (IntValueSetup) translationSetupRoot.findViewById(R.id.sensitivity_value_setup);
        translationSensitivityResetButton = (Button) translationSetupRoot.findViewById(R.id.reset_button);

        translationSensitivitySetup.setIntStringConverter(textConverter);
        translationSensitivitySetup.setOnIntValueSelectedListener(sensitivitySetter);

        TextView description = (TextView) translationSetupRoot.findViewById(R.id.description_text);
        description.setText(R.string.translation_input_description);

        translationInputSwitch.setOnCheckedChangeListener(inputMethodEnabler);

        translationSensitivityResetButton.setOnClickListener(sensitivityResetter);
        translationInputSwitch.setText(R.string.translation_input_name);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeInputMethods = ((MoireeInputMethodsHolder) getActivity()).getMoireeInputMethods();
        moireeInputMethods.addAndFireMoireeInputMethodListener(uiUpdater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        moireeInputMethods.removeMoireeInputMethodListener(uiUpdater);
    }

    private int sensitivityToValue(float sensitivity) {
        return Math.round(sensitivity * 100);
    }

    private float valueToSensitivity(int value) {
        return value / 100f;
    }
}
