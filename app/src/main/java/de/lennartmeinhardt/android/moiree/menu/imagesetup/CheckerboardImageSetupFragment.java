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
import de.lennartmeinhardt.android.moiree.databinding.FragmentCheckerboardImageSetupBinding;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.util.Expandable;
import de.lennartmeinhardt.android.moiree.util.Expandables;

public class CheckerboardImageSetupFragment extends BaseImageCreatorSetupFragment <CheckerboardImageCreator> implements Expandable {

    private FragmentCheckerboardImageSetupBinding binding;

    private ImageCreatorQueue imageCreatorQueue;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkerboard_image_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.setCheckerboardImageCreator(getImageCreator());

        int defSquareSize = getResources().getDimensionPixelSize(R.dimen.checkerboard_image_default_square_size);
        binding.setDefaultSquareSizeInPixels(defSquareSize);

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

    private void recalculatePreviewImage() {
        if(binding != null) {
            int width = binding.expandableViewHeader.getPreviewImageView().getWidth();
            int height = binding.expandableViewHeader.getPreviewImageView().getHeight();
            imageCreatorQueue.recalculateImageForDimensions(getResources(), width, height);
        }
    }

    private void initializeContentView() {
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageCreator().squareSizeInPixels.set(binding.getDefaultSquareSizeInPixels());
            }
        });

        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewImageClicked();
            }
        });
    }

    @Override
    protected CheckerboardImageCreator initializeImageCreator() {
        int defSquareSize = getResources().getDimensionPixelSize(R.dimen.checkerboard_image_default_square_size);
        CheckerboardImageCreator imageCreator = new CheckerboardImageCreator(defSquareSize);
        imageCreatorQueue = new ImageCreatorQueue(imageCreator, previewCalculationListener);
        imageCreator.squareSizeInPixels.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                recalculatePreviewImage();
            }
        });
        return imageCreator;
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
    public void setOnExpandedStateChangedListener(final OnExpandedStateChangedListener onExpandedStateChangedListener) {
        Expandables.setExpandedListenerForWrappedExpandable(this, binding.expandableView, onExpandedStateChangedListener);
    }
}
