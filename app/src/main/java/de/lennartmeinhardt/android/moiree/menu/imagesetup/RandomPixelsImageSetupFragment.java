package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.FragmentRandomPixelsImageSetupBinding;
import de.lennartmeinhardt.android.moiree.imaging.RandomPixelsImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RescaledDrawable;
import de.lennartmeinhardt.android.moiree.util.Expandable;
import de.lennartmeinhardt.android.moiree.util.Expandables;

public class RandomPixelsImageSetupFragment extends BaseImageCreatorSetupFragment implements Expandable {

    private RandomPixelsImageCreator imageCreator;

    private RescaledDrawable previewDrawable;
    private Bitmap previewBitmap;

    private PreviewImageCreator<?> calculatingCreator;
    private PreviewImageCreator<?> nextCalculatorToExecute;

    private FragmentRandomPixelsImageSetupBinding binding;


    @Override
    protected RandomPixelsImageCreator getMoireeImageCreator() {
        if(imageCreator == null) {
            int defaultSquareSize = getResources().getDimensionPixelSize(R.dimen.random_pixels_image_default_square_size);
            int defaultDensityPercents = getResources().getInteger(R.integer.random_pixels_density_default_percents);
            float defaultDensity = defaultDensityPercents / 100f;

            imageCreator = new RandomPixelsImageCreator(defaultSquareSize, defaultDensity);
        }
        return imageCreator;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_random_pixels_image_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // make sure the lazily initialized image creator exists
        getMoireeImageCreator();

        binding.setRandomPixelsImageCreator(imageCreator);

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

                calculateImageFirstTime();
            }
        });
    }

    private void initializeContentView() {
        imageCreator.squareSizeInPixels.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (previewDrawable != null) {
                    previewDrawable.setScaleX(imageCreator.squareSizeInPixels.get());
                    previewDrawable.setScaleY(imageCreator.squareSizeInPixels.get());
                }
            }
        });

        imageCreator.density.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                calculateImageFollowUp();
            }
        });

        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewImageClicked();
            }
        });

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCreator.squareSizeInPixels.set(binding.getDefaultSquareSizeInPixels());
                imageCreator.density.set(binding.getDefaultDensity());
            }
        });
    }

    private void calculateImageFollowUp() {
        if(previewDrawable != null) {
            if (calculatingCreator != null) {
                nextCalculatorToExecute = new FollowUpPreviewImageCreator();
                // cancel the currently operating calculator, but only if it's not running for the first time
                if (calculatingCreator instanceof FollowUpPreviewImageCreator)
                    calculatingCreator.cancel(true);
            } else {
                new FollowUpPreviewImageCreator().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private void calculateImageFirstTime() {
        new FirstTimePreviewImageCreator().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private abstract class PreviewImageCreator<Result> extends AsyncTask<Void, Void, Result> {

        float density;
        int squareSizeInPixels;

        @CallSuper
        @Override
        protected void onPreExecute() {
            squareSizeInPixels = imageCreator.squareSizeInPixels.get();
            density = imageCreator.density.get();
            calculatingCreator = this;
            nextCalculatorToExecute = null;
            binding.expandableViewHeader.setBusy(true);
        }

        @CallSuper
        @Override
        protected void onPostExecute(Result result) {
            calculatingCreator = null;
        }

        @CallSuper
        @Override
        protected void onCancelled(Result result) {
            calculatingCreator = null;
        }
    }

    private class FirstTimePreviewImageCreator extends PreviewImageCreator<Bitmap> {

        private int width, height;

        private RandomPixelsImageCreator squareSizeOne;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            width = binding.expandableViewHeader.getPreviewImageView().getWidth();
            height = binding.expandableViewHeader.getPreviewImageView().getHeight();
            squareSizeOne = new RandomPixelsImageCreator(1, density);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return squareSizeOne.createBitmapForDimensions(width, height);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(getActivity() != null) {
                previewBitmap = bitmap;
                previewDrawable = new RescaledDrawable(imageCreator.createDrawableFromBitmap(getResources(), bitmap));
                previewDrawable.setScaleX(imageCreator.squareSizeInPixels.get());
                previewDrawable.setScaleY(imageCreator.squareSizeInPixels.get());
                binding.expandableViewHeader.setDrawable(previewDrawable);

                if (nextCalculatorToExecute != null)
                    nextCalculatorToExecute.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    binding.expandableViewHeader.setBusy(false);
            }
        }
    }

    private class FollowUpPreviewImageCreator extends PreviewImageCreator<Void> {

        @Override
        protected Void doInBackground(Void... params) {
            RandomPixelsImageCreator.drawRandomPixelsToImage(previewBitmap, 1, density);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            binding.expandableViewHeader.setBusy(false);
            previewDrawable.invalidateSelf();
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            if(nextCalculatorToExecute != null)
                nextCalculatorToExecute.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
