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
import de.lennartmeinhardt.android.moiree.databinding.FragmentTranslationSetupBinding;

public class TranslationSetupFragment extends BaseTransformationSetupFragment {

    private FragmentTranslationSetupBinding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_translation_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                // start the transition only if the expanded property has changed
                if (i == BR.expanded)
                    beginMenuTransition(createMenuBoundsAndHeaderIndicatorTransition());
            }
        });
        initializeResetButtons();
    }

    private void initializeResetButtons() {
        binding.resetTranslationX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationXToIdentity();
            }
        });
        binding.resetTranslationY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMoireeTransformationTransitionIfWanted();
                moireeTransformation.setTranslationYToIdentity();
            }
        });
        binding.resetTranslationBoth.setOnClickListener(new View.OnClickListener() {
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

        binding.setMoireeTransformation(moireeTransformation);
    }

    public boolean isExpanded() {
        return binding.getExpanded();
    }

    public void setExpanded(boolean expanded) {
        binding.setExpanded(expanded);
    }
}
