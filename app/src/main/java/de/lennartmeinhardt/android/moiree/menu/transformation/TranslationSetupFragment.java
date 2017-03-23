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

public class TranslationSetupFragment extends BaseTransformationSetupFragment implements Expandable {

    private ExpandableView expandableView;

    private IntValueSetup translationXValueSetup;
    private IntValueSetup translationYValueSetup;

    private ImageButton resetTranslationXButton;
    private ImageButton resetTranslationYButton;
    private ImageButton resetBothTranslationsButton;

    private final MoireeTransformation.OnTransformationChangedListener uiUpdater = new MoireeTransformation.OnTransformationChangedListener.Adapter() {
        @Override
        public void onTranslationXChanged(float newTranslationX) {
            if (!translationXValueSetup.isUserInputActive()) {
                int value = translationToValue(newTranslationX);
                translationXValueSetup.setValue(value);
            }
        }

        @Override
        public void onTranslationYChanged(float newTranslationY) {
            if (!translationYValueSetup.isUserInputActive()) {
                int value = translationToValue(newTranslationY);
                translationYValueSetup.setValue(value);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_translation_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);

        initializeHeaderView();
        initializeContentView();
    }

    private void initializeContentView() {
        View contentView = expandableView.getContentView();

        translationXValueSetup = (IntValueSetup) contentView.findViewById(R.id.translation_x_value_setup);
        translationYValueSetup = (IntValueSetup) contentView.findViewById(R.id.translation_y_value_setup);

        translationXValueSetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                if (fromUser) {
                    float translation = valueToTranslation(value);
                    moireeTransformation.setTranslationX(translation);
                }
                updateResetButtonsEnabledStates();
            }
        });
        translationYValueSetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                if (fromUser) {
                    float translation = valueToTranslation(value);
                    moireeTransformation.setTranslationY(translation);
                }
                updateResetButtonsEnabledStates();
            }
        });

        initializeResetButtons(expandableView);
    }

    private void initializeHeaderView() {
        View header = expandableView.findViewById(R.id.expandable_view_header);
        final View expandedIndicator = header.findViewById(R.id.header_expanded_indicator);

        TextView title = (TextView) header.findViewById(R.id.header_title);
        title.setText(R.string.translations);

        final int arrowCollapsedDegrees = getResources().getInteger(R.integer.indicator_arrow_collapsed_rotation);
        expandableView.setOnExpandedStateChangedListener(new ExpandableView.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(ExpandableView expandableView, boolean expanded) {
                expandedIndicator.setRotation(expanded ? 0 : arrowCollapsedDegrees);
            }
        });
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuTransition(TransformationSetupMenu.createExpandTransition());
                expandableView.toggleExpanded();
            }
        });
    }

    private void initializeResetButtons(View rootView) {
        resetTranslationXButton = (ImageButton) rootView.findViewById(R.id.reset_translation_x);
        resetTranslationXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationXToIdentity();
            }
        });

        resetTranslationYButton = (ImageButton) rootView.findViewById(R.id.reset_translation_y);
        resetTranslationYButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationYToIdentity();
            }
        });

        resetBothTranslationsButton = (ImageButton) rootView.findViewById(R.id.reset_translation_both);
        resetBothTranslationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationXToIdentity();
                moireeTransformation.setTranslationYToIdentity();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moireeTransformation.addAndFireOnTransformationChangedListener(uiUpdater);
        updateResetButtonsEnabledStates();
    }

    private void updateResetButtonsEnabledStates() {
        boolean isDefaultTranslationX = translationXValueSetup.getValue() == translationToValue(MoireeTransformation.ID_TRANSLATION_X);
        boolean isDefaultTranslationY = translationYValueSetup.getValue() == translationToValue(MoireeTransformation.ID_TRANSLATION_Y);

        resetTranslationXButton.setEnabled(!isDefaultTranslationX);
        resetTranslationYButton.setEnabled(!isDefaultTranslationY);

        resetBothTranslationsButton.setEnabled(!(isDefaultTranslationX && isDefaultTranslationY));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        moireeTransformation.removeOnTransformationChangedListener(uiUpdater);
    }

    private float valueToTranslation(int value) {
        return value;
    }

    private int translationToValue(float translation) {
        return Math.round(translation);
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
