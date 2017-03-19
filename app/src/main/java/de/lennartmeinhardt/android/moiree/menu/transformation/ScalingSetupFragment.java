package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
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
import de.lennartmeinhardt.android.moiree.transition.UiChangeTransition;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class ScalingSetupFragment extends BaseTransformationSetupFragment implements Expandable {

    private ExpandableView expandableView;

    private IntValueSetup commonScalingValueSetup;
    private IntValueSetup scalingXValueSetup;
    private IntValueSetup scalingYValueSetup;

    private ImageButton resetScalingXButton;
    private ImageButton resetScalingYButton;

    private Switch useCommonScalingSwitch;

    private int scalingSliderRange;
    private int minPercent;
    private int stepsPerPercent;


    private final IntValueSetup.OnIntValueUpdatedListener scalingSetter = new IntValueSetup.OnIntValueUpdatedListener.Adapter() {
        @Override
        public void onIntValueSelected(IntValueSetup intValueSetup, int value) {
            float scaling = valueToScaling(value);
            if(intValueSetup == commonScalingValueSetup)
                moireeTransformation.setCommonScaling(scaling);
            else if(intValueSetup == scalingXValueSetup)
                moireeTransformation.setScalingX(scaling);
            else if(intValueSetup == scalingYValueSetup)
                moireeTransformation.setScalingY(scaling);
        }
    };

    private final MoireeTransformation.OnTransformationChangedListener uiUpdater = new MoireeTransformation.OnTransformationChangedListener.Adapter() {
        @Override
        public void onCommonScalingChanged(float newCommonScaling) {
            if(! commonScalingValueSetup.isUserInputActive()) {
                int value = scalingToValue(newCommonScaling);
                commonScalingValueSetup.setValue(value);
            }
        }

        @Override
        public void onScalingXChanged(float newScalingX) {
            if(! scalingXValueSetup.isUserInputActive()) {
                int value = scalingToValue(newScalingX);
                scalingXValueSetup.setValue(value);
            }
        }

        @Override
        public void onScalingYChanged(float newScalingY) {
            if(! scalingYValueSetup.isUserInputActive()) {
                int value = scalingToValue(newScalingY);
                scalingYValueSetup.setValue(value);
            }
        }

        @Override
        public void onUseCommonScalingChanged(boolean newUseCommonScaling) {
            useCommonScalingSwitch.setChecked(newUseCommonScaling);

            if(newUseCommonScaling) {
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
    };

    private final IntValueSetup.IntStringConverter textConverter = new IntValueSetup.IntStringConverter() {
        @Override
        public String intToText(int value) {
            float scaling = valueToScaling(value);
            return getString(R.string.scaling_value_formatter, scaling * 100);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stepsPerPercent = getResources().getInteger(R.integer.scaling_steps_per_percent);
        minPercent = getResources().getInteger(R.integer.scaling_min_percent);
        int maxPercent = getResources().getInteger(R.integer.scaling_max_percent);
        scalingSliderRange = (maxPercent - minPercent) * stepsPerPercent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scaling_setup, container, false);

        expandableView = (ExpandableView) rootView.findViewById(R.id.expandable_view);
        View header = expandableView.findViewById(R.id.expandable_view_header);
        TextView title = (TextView) header.findViewById(R.id.expandable_view_header_title);
        final View expandedIndicator = header.findViewById(R.id.expandable_view_expanded_indicator);

        title.setText(R.string.scalings);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition(TransformationSetupMenu.createExpandTransition());
                expandableView.toggleExpanded();
            }
        });
        expandableView.setOnExpandedStateChangedListener(new ExpandableView.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(ExpandableView expandableView, boolean expanded) {
                expandedIndicator.setRotation(expanded ? 0 : -90);
            }
        });

        useCommonScalingSwitch = (Switch) expandableView.findViewById(R.id.common_scaling_switch);
        useCommonScalingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beginMoireeTransformationTransitionIfWanted();
                TransitionManager.beginDelayedTransition(((ViewGroup) getActivity().findViewById(R.id.menu_holder)), new UiChangeTransition());
                moireeTransformation.setUseCommonScaling(useCommonScalingSwitch.isChecked());
            }
        });

        commonScalingValueSetup = (IntValueSetup) expandableView.findViewById(R.id.common_scaling_value_setup);
        scalingXValueSetup = (IntValueSetup) expandableView.findViewById(R.id.scaling_x_value_setup);
        scalingYValueSetup = (IntValueSetup) expandableView.findViewById(R.id.scaling_y_value_setup);

        initializeScalingValueSetup(commonScalingValueSetup);
        initializeScalingValueSetup(scalingXValueSetup);
        initializeScalingValueSetup(scalingYValueSetup);

        initializeResetButtons(expandableView);
        return rootView;
    }

    private void initializeResetButtons(View rootView) {
        resetScalingXButton = (ImageButton) rootView.findViewById(R.id.reset_button_x);
        resetScalingXButton.setImageResource(R.drawable.ic_reset_scaling_horizontal);
        resetScalingXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setScalingXToIdentity();
            }
        });

        resetScalingYButton = (ImageButton) rootView.findViewById(R.id.reset_button_y);
        resetScalingYButton.setImageResource(R.drawable.ic_reset_scaling_vertical);
        resetScalingYButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setScalingYToIdentity();
            }
        });

        ImageButton resetBothScalingsButton = (ImageButton) rootView.findViewById(R.id.reset_button_both);
        resetBothScalingsButton.setImageResource(R.drawable.ic_reset_scaling_both);
        resetBothScalingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                if(moireeTransformation.isUseCommonScaling()) {
                    moireeTransformation.setCommonScalingToIdentity();
                } else {
                    moireeTransformation.setScalingXToIdentity();
                    moireeTransformation.setScalingYToIdentity();
                }
            }
        });
    }

    private void initializeScalingValueSetup(IntValueSetup valueSetup) {
        valueSetup.setMaxValue(scalingSliderRange);
        valueSetup.setIntStringConverter(textConverter);
        valueSetup.setOnIntValueSelectedListener(scalingSetter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moireeTransformation.addAndFireOnTransformationChangedListener(uiUpdater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        moireeTransformation.removeOnTransformationChangedListener(uiUpdater);
    }

    private float valueToScaling(int value) {
        float percents = minPercent + ((float) value) / stepsPerPercent;
        return percents / 100f;
        /*// relative progress is in [0, 1]
        double relativeProgress = (1. * value) / scalingSliderRange;
        double scaling = Math.pow(2, relativeProgress * 2 - 1);
        // round scaling to nearest 1/stepsPerPercent
        return (float) scaling;
//        return Math.round(100f * scaling * stepsPerPercent) / (100f * stepsPerPercent);*/
    }

    private int scalingToValue(float scaling) {
        float percents = scaling * 100f;
        return Math.round((percents - minPercent) * stepsPerPercent);
        /*double log2 = Math.log(scaling) / Math.log(2);
        double relativeProgress = (log2 + 1) / 2;
        return (int) (relativeProgress * scalingSliderRange);*/
    }

    @Override
    public boolean isExpanded() {
        return expandableView.isExpanded();
    }

    @Override
    public void setExpanded(boolean expanded) {
        expandableView.setExpanded(expanded);
    }

    @Override
    public void toggleExpanded() {
        expandableView.toggleExpanded();
    }
}
