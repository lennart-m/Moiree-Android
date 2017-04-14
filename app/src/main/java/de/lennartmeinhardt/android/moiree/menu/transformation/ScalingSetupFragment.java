package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.Expandable;
import de.lennartmeinhardt.android.moiree.MoireeTransformation;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class ScalingSetupFragment extends BaseTransformationSetupFragment {

    private ExpandableView expandableView;

    private IntValueSetup commonScalingValueSetup;
    private IntValueSetup scalingXValueSetup;
    private IntValueSetup scalingYValueSetup;

    private ImageButton resetScalingXButton;
    private ImageButton resetScalingYButton;
    private ImageButton resetBothScalingsButton;

    private Switch useCommonScalingSwitch;


    private final IntValueSetup.OnValueChangedListener scalingSetter = new IntValueSetup.OnValueChangedListener() {
        @Override
        public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
            if (fromUser) {
                float scaling = valueToScaling(value);
                if (intValueSetup == commonScalingValueSetup)
                    moireeTransformation.setCommonScaling(scaling);
                else if (intValueSetup == scalingXValueSetup)
                    moireeTransformation.setScalingX(scaling);
                else if (intValueSetup == scalingYValueSetup)
                    moireeTransformation.setScalingY(scaling);
            }
            updateResetButtonsEnabledStates();
        }
    };

    private final MoireeTransformation.OnTransformationChangedListener uiUpdater = new MoireeTransformation.OnTransformationChangedListener.Adapter() {
        @Override
        public void onCommonScalingChanged(float newCommonScaling) {
            if (!commonScalingValueSetup.isUserInputActive()) {
                int value = scalingToValue(newCommonScaling);
                commonScalingValueSetup.setValue(value);
            }
        }

        @Override
        public void onScalingXChanged(float newScalingX) {
            if (!scalingXValueSetup.isUserInputActive()) {
                int value = scalingToValue(newScalingX);
                scalingXValueSetup.setValue(value);
            }
        }

        @Override
        public void onScalingYChanged(float newScalingY) {
            if (!scalingYValueSetup.isUserInputActive()) {
                int value = scalingToValue(newScalingY);
                scalingYValueSetup.setValue(value);
            }
        }

        @Override
        public void onUseCommonScalingChanged(boolean newUseCommonScaling) {
            useCommonScalingSwitch.setChecked(newUseCommonScaling);
        }
    };

    private void updateValueSetupVisibility() {
        boolean newUseCommonScaling = moireeTransformation.isUseCommonScaling();

        if (newUseCommonScaling) {
            commonScalingValueSetup.setVisibility(View.VISIBLE);
            scalingXValueSetup.setVisibility(View.GONE);
            scalingYValueSetup.setVisibility(View.GONE);
            resetScalingXButton.setVisibility(View.GONE);
            resetScalingYButton.setVisibility(View.GONE);

        } else {
            commonScalingValueSetup.setVisibility(View.GONE);
            scalingXValueSetup.setVisibility(View.VISIBLE);
            scalingYValueSetup.setVisibility(View.VISIBLE);
            resetScalingXButton.setVisibility(View.VISIBLE);
            resetScalingYButton.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scaling_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);

        initializeHeaderView();
        initializeContentView();
    }

    private void initializeHeaderView() {
        View header = expandableView.findHeaderView();
        TextView title = (TextView) header.findViewById(R.id.header_title);
        final View expandedIndicator = header.findViewById(R.id.header_expanded_indicator);

        title.setText(R.string.scalings);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuTransition(createMenuBoundsAndHeaderIndicatorTransition());
                expandableView.toggleExpanded();
            }
        });
        final int arrowCollapsedDegrees = getResources().getInteger(R.integer.indicator_arrow_collapsed_rotation);
        expandableView.addOnExpandedStateChangedListener(new Expandable.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(Expandable expandable, boolean expanded) {
                expandedIndicator.setRotation(expanded ? 0 : arrowCollapsedDegrees);
            }
        });
    }

    private void initializeContentView() {
        View contentView = expandableView.findContentView();

        useCommonScalingSwitch = (Switch) contentView.findViewById(R.id.common_scaling_switch);
        useCommonScalingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beginMoireeTransformationTransitionIfWanted();
                beginMenuBoundsTransition();
                moireeTransformation.setUseCommonScaling(useCommonScalingSwitch.isChecked());
                updateValueSetupVisibility();
                updateResetButtonsEnabledStates();
            }
        });

        commonScalingValueSetup = (IntValueSetup) contentView.findViewById(R.id.common_scaling_value_setup);
        scalingXValueSetup = (IntValueSetup) contentView.findViewById(R.id.scaling_x_value_setup);
        scalingYValueSetup = (IntValueSetup) contentView.findViewById(R.id.scaling_y_value_setup);

        commonScalingValueSetup.setOnValueChangedListener(scalingSetter);
        scalingXValueSetup.setOnValueChangedListener(scalingSetter);
        scalingYValueSetup.setOnValueChangedListener(scalingSetter);

        initializeResetButtons(contentView);
    }

    private void initializeResetButtons(View rootView) {
        resetScalingXButton = (ImageButton) rootView.findViewById(R.id.reset_scaling_x);
        resetScalingXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setScalingXToIdentity();
            }
        });

        resetScalingYButton = (ImageButton) rootView.findViewById(R.id.reset_scaling_y);
        resetScalingYButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setScalingYToIdentity();
            }
        });

        resetBothScalingsButton = (ImageButton) rootView.findViewById(R.id.reset_scaling_both);
        resetBothScalingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                if (moireeTransformation.isUseCommonScaling()) {
                    moireeTransformation.setCommonScalingToIdentity();
                } else {
                    moireeTransformation.setScalingXToIdentity();
                    moireeTransformation.setScalingYToIdentity();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moireeTransformation.addAndFireOnTransformationChangedListener(uiUpdater);
        updateValueSetupVisibility();
        updateResetButtonsEnabledStates();
    }

    private void updateResetButtonsEnabledStates() {
        boolean isDefaultScalingX = scalingXValueSetup.getValue() == scalingToValue(MoireeTransformation.ID_SCALING_X);
        boolean isDefaultScalingY = scalingYValueSetup.getValue() == scalingToValue(MoireeTransformation.ID_SCALING_Y);
        boolean isDefaultCommonScaling = commonScalingValueSetup.getValue() == scalingToValue(MoireeTransformation.ID_COMMON_SCALING);

        resetScalingXButton.setEnabled(!isDefaultScalingX);
        resetScalingYButton.setEnabled(!isDefaultScalingY);

        if (moireeTransformation.isUseCommonScaling())
            resetBothScalingsButton.setEnabled(!isDefaultCommonScaling);
        else
            resetBothScalingsButton.setEnabled(!isDefaultScalingX || !isDefaultScalingY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        moireeTransformation.removeOnTransformationChangedListener(uiUpdater);
    }

    private float valueToScaling(int value) {
//        float percents = minPercent + ((float) value) / stepsPerPercent;
//        return percents / 100f;
        return value / 100f;
        /*// relative progress is in [0, 1]
        double relativeProgress = (1. * value) / scalingSliderRange;
        double scaling = Math.pow(2, relativeProgress * 2 - 1);
        // round scaling to nearest 1/stepsPerPercent
        return (float) scaling;
//        return Math.round(100f * scaling * stepsPerPercent) / (100f * stepsPerPercent);*/
    }

    private int scalingToValue(float scaling) {
//        float percents = scaling * 100f;
//        return Math.round((percents - minPercent) * stepsPerPercent);
        return Math.round(scaling * 100f);
        /*double log2 = Math.log(scaling) / Math.log(2);
        double relativeProgress = (log2 + 1) / 2;
        return (int) (relativeProgress * scalingSliderRange);*/
    }

    public ExpandableView getExpandableView() {
        return expandableView;
    }
}
