package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.FragmentRandomPixelsImageSetupBinding;
import de.lennartmeinhardt.android.moiree.imaging.RandomPixelsImageCreator;
import de.lennartmeinhardt.android.moiree.util.Expandable;
import de.lennartmeinhardt.android.moiree.util.Expandables;

public class RandomPixelsImageSetupFragment extends BaseImageCreatorSetupFragment<RandomPixelsImageCreator> implements Expandable {

    private ImageCreatorQueue imageCreatorQueue;

    private FragmentRandomPixelsImageSetupBinding binding;

    private final ImageCreatorQueue.CalculationListener previewCalculationListener = new ImageCreatorQueue.CalculationListener() {
        @Override
        public void onCalculationStarting() {
            binding.expandableViewHeader.setBusy(true);
        }

        @Override
        public void onCalculationSuccessful(@Nullable BitmapDrawable moireeImage, boolean willContinueCalculating) {
            // only operate on the ui if this fragment is attached
            if (getActivity() != null) {
                binding.expandableViewHeader.setBusy(willContinueCalculating);
                if (moireeImage != null)
                    binding.expandableViewHeader.setDrawable(moireeImage);
            }
        }

        @Override
        public void onCalculationCancelled(boolean willContinueCalculating) {
            binding.expandableViewHeader.setBusy(willContinueCalculating);
        }
    };


    @Override
    RandomPixelsImageCreator initializeImageCreator() {
        int defaultSquareSize = getResources().getDimensionPixelSize(R.dimen.random_pixels_image_default_square_size);
        int defaultDensityPercents = getResources().getInteger(R.integer.random_pixels_density_default_percents);
        float defaultDensity = defaultDensityPercents / 100f;

        RandomPixelsImageCreator imageCreator = new RandomPixelsImageCreator(defaultSquareSize, defaultDensity);
        imageCreatorQueue = new ImageCreatorQueue(imageCreator, previewCalculationListener);
        Observable.OnPropertyChangedCallback calculateTaskStarter = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                recalculatePreviewImage();
            }
        };
        imageCreator.squareSizeInPixels.addOnPropertyChangedCallback(calculateTaskStarter);
        imageCreator.density.addOnPropertyChangedCallback(calculateTaskStarter);
        return imageCreator;
    }

    private void recalculatePreviewImage() {
        if(binding != null) {
            int width = binding.expandableViewHeader.getPreviewImageView().getWidth();
            int height = binding.expandableViewHeader.getPreviewImageView().getHeight();
            imageCreatorQueue.recalculateImageForDimensions(getResources(), width, height);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_random_pixels_image_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.setRandomPixelsImageCreator(getImageCreator());

        int defaultSquareSize = getResources().getDimensionPixelSize(R.dimen.random_pixels_image_default_square_size);
        int defaultDensityPercents = getResources().getInteger(R.integer.random_pixels_density_default_percents);
        binding.setDefaultSquareSizeInPixels(defaultSquareSize);
        binding.setDefaultDensity(defaultDensityPercents / 100f);

        initializeHeaderView();
        initializeContentView();
    }

    private void initializeHeaderView() {
        binding.expandableViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                binding.expandableView.toggleExpanded();
            }
        });

        binding.expandableViewHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16)
                    binding.expandableViewHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    binding.expandableViewHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                recalculatePreviewImage();
            }
        });
    }

    private void initializeContentView() {
        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewImageClicked();
            }
        });

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageCreator().squareSizeInPixels.set(binding.getDefaultSquareSizeInPixels());
                getImageCreator().density.set(binding.getDefaultDensity());
            }
        });
    }

    @Override
    public boolean isExpanded() {
        return binding.expandableView.isExpanded();
    }

    @Override
    public void setExpanded(boolean expanded) {
        binding.expandableView.setExpanded(expanded);
    }

    @Override
    public void toggleExpanded() {
        binding.expandableView.toggleExpanded();
    }

    @Override
    public void setOnExpandedStateChangedListener(OnExpandedStateChangedListener onExpandedStateChangedListener) {
        Expandables.setExpandedListenerForWrappedExpandable(this, binding.expandableView, onExpandedStateChangedListener);
    }
}
