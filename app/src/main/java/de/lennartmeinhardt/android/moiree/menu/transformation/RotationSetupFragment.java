package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.Expandable;
import de.lennartmeinhardt.android.moiree.MoireeTransformation;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class RotationSetupFragment extends BaseTransformationSetupFragment implements Expandable {

    private ExpandableView expandableView;

    private IntValueSetup rotationValueSetup;

    private int stepsPerDegree;
    private int rotationSliderRange;

    private final IntValueSetup.OnIntValueUpdatedListener rotationSetter = new IntValueSetup.OnIntValueUpdatedListener.Adapter() {
        @Override
        public void onIntValueSelected(IntValueSetup intValueSetup, int value) {
            float rotation = valueToRotation(value);
            moireeTransformation.setRotation(rotation);
        }
    };

    private final MoireeTransformation.OnTransformationChangedListener uiUpdater = new MoireeTransformation.OnTransformationChangedListener.Adapter() {
        @Override
        public void onRotationChanged(float newRotation) {
            if(! rotationValueSetup.isUserInputActive()) {
                int value = rotationToValue(newRotation);
                rotationValueSetup.setValue(value);
            }
        }
    };

    private final IntValueSetup.IntStringConverter textConverter = new IntValueSetup.IntStringConverter() {
        @Override
        public String intToText(int value) {
            float rotation = valueToRotation(value);
            return getString(R.string.rotation_value_formatter, rotation);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stepsPerDegree = getResources().getInteger(R.integer.rotation_steps_per_degree);
        rotationSliderRange = 360 * stepsPerDegree;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rotation_setup, container, false);

        expandableView = (ExpandableView) rootView.findViewById(R.id.expandable_view);
        View header = expandableView.findViewById(R.id.expandable_view_header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition(TransformationSetupMenu.createExpandTransition());
                expandableView.toggleExpanded();
            }
        });
        final View expandedIndicator = header.findViewById(R.id.expandable_view_expanded_indicator);
        expandableView.setOnExpandedStateChangedListener(new ExpandableView.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(ExpandableView expandableView, boolean expanded) {
                expandedIndicator.setRotation(expanded ? 0 : -90);
            }
        });
        TextView rotationSetupTitle = (TextView) header.findViewById(R.id.expandable_view_header_title);
        rotationSetupTitle.setText(R.string.rotation);

        rotationValueSetup = (IntValueSetup) expandableView.findViewById(R.id.rotation_value_setup);
        rotationValueSetup.setMaxValue(rotationSliderRange);
        rotationValueSetup.setIntStringConverter(textConverter);
        rotationValueSetup.setOnIntValueSelectedListener(rotationSetter);

        ImageButton resetButton = (ImageButton) expandableView.findViewById(R.id.reset_button);
        resetButton.setImageResource(R.drawable.ic_reset_rotation);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setRotationToIdentity();
            }
        });
        return rootView;
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

    private float valueToRotation(int value) {
        return (1f * value) / stepsPerDegree - 180;
    }

    private int rotationToValue(float rotation) {
        return Math.round((180 + rotation) * stepsPerDegree);
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
