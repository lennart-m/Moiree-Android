package de.lennartmeinhardt.android.moiree.menu.settings;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.MenuTransparencyConfig;
import de.lennartmeinhardt.android.moiree.MenuTransparencyConfigHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.FragmentMenuTransparencySetupBinding;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;
import de.lennartmeinhardt.android.moiree.transition.ChangeAlpha;

public class MenuTransparencySetupFragment extends MenuFragment {

    private MenuTransparencyConfig menuTransparencyConfig;

    private FragmentMenuTransparencySetupBinding binding;

    private final Observable.OnPropertyChangedCallback menuTransitionStarter = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            beginMenuTransition(createBoundsAndAlphaTransition());
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu_transparency_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final int defaultMenuTransparencyPercents = getResources().getInteger(R.integer.menu_opacity_default_percents);
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuTransition(createMenuTransparencyTransition());
                menuTransparencyConfig.menuAlpha.set(defaultMenuTransparencyPercents / 100f);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menuTransparencyConfig = ((MenuTransparencyConfigHolder) getActivity()).getMenuTransparencyConfig();
        menuTransparencyConfig.transparencyEnabled.addOnPropertyChangedCallback(menuTransitionStarter);

        binding.setMenuTransparencyConfig(menuTransparencyConfig);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        menuTransparencyConfig.transparencyEnabled.removeOnPropertyChangedCallback(menuTransitionStarter);
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
