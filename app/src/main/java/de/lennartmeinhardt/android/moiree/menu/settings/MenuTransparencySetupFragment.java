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

    private Switch menuTransparencySwitch;
    private IntValueSetup opacityValueSetup;
    private ExpandableView expandableView;
    private Button resetButton;

    private int defaultMenuTransparencyPercents;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defaultMenuTransparencyPercents = getResources().getInteger(R.integer.menu_opacity_default_percents);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_transparency_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);

        View header = expandableView.getHeaderView();
        menuTransparencySwitch = (Switch) header;
        menuTransparencySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                beginMenuTransition(createBoundsAndAlphaTransition());
                menuTransparencyConfig.setTransparencyEnabled(isChecked);
                updateUI(false);
            }
        });

        View content = expandableView.getContentView();

        resetButton = (Button) content.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuTransition(createMenuTransparencyTransition());
                opacityValueSetup.setValue(defaultMenuTransparencyPercents);
            }
        });

        opacityValueSetup = (IntValueSetup) content.findViewById(R.id.opacity_value_setup);
        opacityValueSetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                float alpha = valueToAlpha(value);
                menuTransparencyConfig.setMenuAlpha(alpha);
                updateUI(true);
            }
        });
    }

    private void updateUI(boolean fromValueSetup) {
        boolean transparencyEnabled = menuTransparencyConfig.isTransparencyEnabled();
        float menuAlpha = menuTransparencyConfig.getMenuAlpha();
        int opacityPercents = alphaToValue(menuAlpha);

        menuTransparencySwitch.setChecked(transparencyEnabled);
        expandableView.setExpanded(transparencyEnabled);
        resetButton.setEnabled(opacityPercents != defaultMenuTransparencyPercents);

        if(! fromValueSetup)
            opacityValueSetup.setValue(opacityPercents);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menuTransparencyConfig = ((MenuTransparencyConfigHolder) getActivity()).getMenuTransparencyConfig();

        updateUI(false);
        int value = alphaToValue(menuTransparencyConfig.getMenuAlpha());
        opacityValueSetup.setValue(value);
    }

    private int alphaToValue(float alpha) {
        return (int) (alpha * 100);
    }

    private float valueToAlpha(int value) {
        return value / 100f;
    }

    private static Transition createBoundsAndAlphaTransition() {
        return new TransitionSet()
                .addTransition(createMenuBoundsTransition())
                .addTransition(createMenuTransparencyTransition())
                .setOrdering(TransitionSet.ORDERING_TOGETHER);
    }

    private static Transition createMenuTransparencyTransition() {
        return new ChangeAlpha().addTarget(R.id.menu_holder);
    }
}
