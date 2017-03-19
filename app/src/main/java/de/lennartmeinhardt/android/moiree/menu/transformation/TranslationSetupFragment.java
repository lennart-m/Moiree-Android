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

    private int translationSliderRange;
    private int minTranslation;
    private int stepsPerPixel;


    private final IntValueSetup.OnIntValueUpdatedListener translationSetter = new IntValueSetup.OnIntValueUpdatedListener.Adapter() {
        @Override
        public void onIntValueSelected(IntValueSetup intValueSetup, int value) {
            float translation = valueToTranslation(value);
            if(intValueSetup == translationXValueSetup)
                moireeTransformation.setTranslationX(translation);
            else if(intValueSetup == translationYValueSetup)
                moireeTransformation.setTranslationY(translation);
        }
    };

    private final MoireeTransformation.OnTransformationChangedListener uiUpdater = new MoireeTransformation.OnTransformationChangedListener.Adapter() {
        @Override
        public void onTranslationXChanged(float newTranslationX) {
            if(! translationXValueSetup.isUserInputActive()) {
                int value = translationToValue(newTranslationX);
                translationXValueSetup.setValue(value);
            }
        }

        @Override
        public void onTranslationYChanged(float newTranslationY) {
            if(! translationYValueSetup.isUserInputActive()) {
                int value = translationToValue(newTranslationY);
                translationYValueSetup.setValue(value);
            }
        }
    };

    private final IntValueSetup.IntStringConverter textConverter = new IntValueSetup.IntStringConverter() {
        @Override
        public String intToText(int value) {
            float translation = valueToTranslation(value);
            return getString(R.string.translation_value_formatter, translation);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stepsPerPixel = getResources().getInteger(R.integer.translation_steps_per_pixel);
        minTranslation = getResources().getInteger(R.integer.translation_min_pixels);
        int maxTranslation = getResources().getInteger(R.integer.translation_max_pixels);
        translationSliderRange = (maxTranslation - minTranslation) * stepsPerPixel;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translation_setup, container, false);

        expandableView = (ExpandableView) rootView.findViewById(R.id.expandable_view);
        View header = expandableView.findViewById(R.id.expandable_view_header);
        final View expandedIndicator = header.findViewById(R.id.expandable_view_expanded_indicator);
        TextView title = (TextView) header.findViewById(R.id.expandable_view_header_title);

        title.setText(R.string.translations);

        expandableView.setOnExpandedStateChangedListener(new ExpandableView.OnExpandedStateChangedListener() {
            @Override
            public void onExpandedStateChanged(ExpandableView expandableView, boolean expanded) {
                expandedIndicator.setRotation(expanded ? 0 : -90);
            }
        });
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition(TransformationSetupMenu.createExpandTransition());
                expandableView.toggleExpanded();
            }
        });

        translationXValueSetup = (IntValueSetup) expandableView.findViewById(R.id.translation_x_value_setup);
        translationYValueSetup = (IntValueSetup) expandableView.findViewById(R.id.translation_y_value_setup);
        initializeTranslationValueSetup(translationXValueSetup);
        initializeTranslationValueSetup(translationYValueSetup);

        initializeResetButtons(expandableView);
        return rootView;
    }

    private void initializeResetButtons(View rootView) {
        ImageButton resetTranslationXButton = (ImageButton) rootView.findViewById(R.id.reset_button_x);
        resetTranslationXButton.setImageResource(R.drawable.ic_reset_translation_horizontal);
        resetTranslationXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationXToIdentity();
            }
        });

        ImageButton resetTranslationYButton = (ImageButton) rootView.findViewById(R.id.reset_button_y);
        resetTranslationYButton.setImageResource(R.drawable.ic_reset_translation_vertical);
        resetTranslationYButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationYToIdentity();
            }
        });

        ImageButton resetBothTranslationsButton = (ImageButton) rootView.findViewById(R.id.reset_button_both);
        resetBothTranslationsButton.setImageResource(R.drawable.ic_reset_translation_both);
        resetBothTranslationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationXToIdentity();
                moireeTransformation.setTranslationYToIdentity();
            }
        });
    }

    private void initializeTranslationValueSetup(IntValueSetup valueSetup) {
        valueSetup.setMaxValue(translationSliderRange);
        valueSetup.setIntStringConverter(textConverter);
        valueSetup.setOnIntValueSelectedListener(translationSetter);
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

    private float valueToTranslation(int value) {
        return (1f * value / stepsPerPixel) + minTranslation;
    }

    private int translationToValue(float translation) {
        return Math.round((translation - minTranslation) * stepsPerPixel);
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
