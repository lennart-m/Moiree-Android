package de.lennartmeinhardt.android.moiree.menu.transformation;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.BR;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.FragmentScalingSetupBinding;

public class ScalingSetupFragment extends BaseTransformationSetupFragment {

    private FragmentScalingSetupBinding binding;

    private final Observable.OnPropertyChangedCallback transitionStarter = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            beginMoireeTransformationTransitionIfWanted();
            beginMenuBoundsTransition();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scaling_setup, container, false);
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
        binding.resetScalingX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setScalingXToIdentity();
            }
        });
        binding.resetScalingY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setScalingYToIdentity();
            }
        });
        binding.resetScalingBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                if (moireeTransformation.useCommonScaling.get()) {
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

        binding.setMoireeTransformation(moireeTransformation);

        moireeTransformation.useCommonScaling.addOnPropertyChangedCallback(transitionStarter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        moireeTransformation.useCommonScaling.removeOnPropertyChangedCallback(transitionStarter);
    }

    public boolean isExpanded() {
        return binding.getExpanded();
    }

    public void setExpanded(boolean expanded) {
        binding.setExpanded(expanded);
    }
}
