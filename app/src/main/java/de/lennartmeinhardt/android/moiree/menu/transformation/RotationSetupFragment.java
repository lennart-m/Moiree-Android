package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.Expandable;
import de.lennartmeinhardt.android.moiree.MoireeTransformation;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class RotationSetupFragment extends BaseTransformationSetupFragment implements Expandable {

    private ExpandableView expandableView;

    private ImageButton resetButton;

    private IntValueSetup rotationValueSetup;

    private final IntValueSetup.OnValueChangedListener rotationSetter = new IntValueSetup.OnValueChangedListener() {
        @Override
        public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
            if(fromUser) {
                float rotation = valueToRotation(value);
                moireeTransformation.setRotation(rotation);
            }
            updateResetButtonEnabledState();
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

    private final View.OnClickListener rotationResetter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            beginMoireeTransformationTransitionIfWanted();
            moireeTransformation.setRotationToIdentity();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rotation_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);

        initializeHeaderView();
        initializeContentView();
    }

    private void initializeHeaderView() {
        View header = expandableView.getHeaderView();
        final View expandedIndicator = header.findViewById(R.id.header_expanded_indicator);
        TextView rotationSetupTitle = (TextView) header.findViewById(R.id.header_title);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuTransition(TransformationSetupMenu.createExpandTransition());
                expandableView.toggleExpanded();
            }
        });

        final int arrowCollapsedDegrees = getResources().getInteger(R.integer.indicator_arrow_collapsed_rotation);
        expandableView.setOnExpandedStateChangedListener(new ExpandableView.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(ExpandableView expandableView, boolean expanded) {
                expandedIndicator.setRotation(expanded ? 0 : arrowCollapsedDegrees);
            }
        });

        rotationSetupTitle.setText(R.string.rotation);
    }

    private void initializeContentView() {
        View content = expandableView.getContentView();

        rotationValueSetup = (IntValueSetup) content.findViewById(R.id.rotation_value_setup);
        resetButton = (ImageButton) content.findViewById(R.id.reset_button);

        rotationValueSetup.setOnValueChangedListener(rotationSetter);

        resetButton.setImageResource(R.drawable.ic_reset_rotation);
        resetButton.setOnClickListener(rotationResetter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moireeTransformation.addAndFireOnTransformationChangedListener(uiUpdater);
        updateResetButtonEnabledState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        moireeTransformation.removeOnTransformationChangedListener(uiUpdater);
    }

    private void updateResetButtonEnabledState() {
        boolean isDefaultRotation = rotationValueSetup.getValue() == rotationToValue(MoireeTransformation.ID_ROTATION);
        resetButton.setEnabled(! isDefaultRotation);
    }

    private float valueToRotation(int value) {
        return value;
    }

    private int rotationToValue(float rotation) {
        return Math.round(rotation);
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
