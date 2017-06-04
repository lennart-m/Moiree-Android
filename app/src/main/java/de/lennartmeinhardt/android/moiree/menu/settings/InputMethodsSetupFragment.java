package de.lennartmeinhardt.android.moiree.menu.settings;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.BR;
import de.lennartmeinhardt.android.moiree.MoireeInputMethods;
import de.lennartmeinhardt.android.moiree.MoireeInputMethodsHolder;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.FragmentInputMethodsSetupBinding;
import de.lennartmeinhardt.android.moiree.menu.MenuFragment;

public class InputMethodsSetupFragment extends MenuFragment {

    private MoireeInputMethods moireeInputMethods;

    private FragmentInputMethodsSetupBinding binding;

    private final Observable.OnPropertyChangedCallback menuBoundsTransitionStarter = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            beginMenuBoundsTransition();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input_methods_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                // start the transition only if the expanded property has changed
                if(i == BR.expanded)
                    beginMenuTransition(createMenuBoundsAndHeaderIndicatorTransition());
            }
        });

        initializeResetButtons();
    }

    private void initializeResetButtons() {
        final int defaultSensitivityPercents = getResources().getInteger(R.integer.sensitivity_default_percents);

        binding.rotationInputSetupRoot.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moireeInputMethods.rotationSensitivity.set(defaultSensitivityPercents / 100f);
            }
        });
        binding.scalingInputSetupRoot.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moireeInputMethods.scalingSensitivity.set(defaultSensitivityPercents / 100f);
            }
        });
        binding.translationInputSetupRoot.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moireeInputMethods.translationSensitivity.set(defaultSensitivityPercents / 100f);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeInputMethods = ((MoireeInputMethodsHolder) getActivity()).getMoireeInputMethods();
        binding.setMoireeInputMethods(moireeInputMethods);

        moireeInputMethods.rotationInputEnabled.addOnPropertyChangedCallback(menuBoundsTransitionStarter);
        moireeInputMethods.scalingInputEnabled.addOnPropertyChangedCallback(menuBoundsTransitionStarter);
        moireeInputMethods.translationInputEnabled.addOnPropertyChangedCallback(menuBoundsTransitionStarter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        moireeInputMethods.rotationInputEnabled.removeOnPropertyChangedCallback(menuBoundsTransitionStarter);
        moireeInputMethods.scalingInputEnabled.removeOnPropertyChangedCallback(menuBoundsTransitionStarter);
        moireeInputMethods.translationInputEnabled.removeOnPropertyChangedCallback(menuBoundsTransitionStarter);
    }

    public boolean isExpanded() {
        return binding.getExpanded();
    }

    public void setExpanded(boolean expanded) {
        binding.setExpanded(expanded);
    }
}
