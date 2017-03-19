package de.lennartmeinhardt.android.moiree.menu.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import de.lennartmeinhardt.android.moiree.MenuTransparencyConfig;
import de.lennartmeinhardt.android.moiree.MenuTransparencyConfigHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.transition.ChangeAlpha;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class MenuTransparencySetupFragment extends MenuFragment {

    private MenuTransparencyConfig menuTransparencyConfig;

    private ExpandableView expandableView;
    private Switch menuTransparencySwitch;
    private IntValueSetup opacityValueSetup;

    private MenuTransparencyConfig.OnMenuTransparencyChangedListener uiUpdater = new MenuTransparencyConfig.OnMenuTransparencyChangedListener() {
        @Override
        public void onMenuTransparencyChanged(boolean transparencyEnabled, float menuAlpha) {
            menuTransparencySwitch.setChecked(transparencyEnabled);
            expandableView.setExpanded(transparencyEnabled);
            if(! opacityValueSetup.isUserInputActive()) {
                int value = alphaToValue(menuAlpha);
                opacityValueSetup.setValue(value);
            }
        }
    };

    private final IntValueSetup.IntStringConverter textConverter = new IntValueSetup.IntStringConverter() {
        @Override
        public String intToText(int value) {
            return getString(R.string.percentage_formatter, value);
        }
    };

    private final IntValueSetup.OnIntValueUpdatedListener transparencySetter = new IntValueSetup.OnIntValueUpdatedListener.Adapter() {
        @Override
        public void onIntValueSelected(IntValueSetup intValueSetup, int value) {
            float alpha = valueToAlpha(value);
            menuTransparencyConfig.setMenuAlpha(alpha);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu_transparency_setup, container, false);

        expandableView = (ExpandableView) rootView.findViewById(R.id.expandable_view);

        menuTransparencySwitch = (Switch) rootView.findViewById(R.id.menu_transparency_setup_switch);
        menuTransparencySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beginMenuBoundsTransition(createTransition());
                menuTransparencyConfig.setTransparencyEnabled(isChecked);
            }
        });

        opacityValueSetup = (IntValueSetup) rootView.findViewById(R.id.opacity_value_setup);
        opacityValueSetup.setOnIntValueSelectedListener(transparencySetter);
        opacityValueSetup.setIntStringConverter(textConverter);

        Button resetButton = (Button) rootView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition(createTransition());
                menuTransparencyConfig.setMenuAlphaToDefault();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menuTransparencyConfig = ((MenuTransparencyConfigHolder) getActivity()).getMenuTransparencyConfig();
        menuTransparencyConfig.addAndFireMenuTransparencyListener(uiUpdater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        menuTransparencyConfig.removeMenuTransparencyListener(uiUpdater);
    }

    private int alphaToValue(float alpha) {
        return (int) (alpha * 100);
    }

    private float valueToAlpha(int value) {
        return value / 100f;
    }

    private static Transition createTransition() {
        return new TransitionSet()
                .addTransition(createMenuBoundsTransition())
                .addTransition(createMenuTransparencyTransition())
                .setOrdering(TransitionSet.ORDERING_TOGETHER);
    }

    private static Transition createMenuTransparencyTransition() {
        return new ChangeAlpha().addTarget(R.id.menu_holder);
    }
}
