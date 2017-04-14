package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.RandomPixelsImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RescaledDrawable;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class RandomPixelsImageSetupFragment extends BaseImageCreatorSetupFragment<RandomPixelsImageCreator> {

    private ProgressBar calculatingPreviewProgressBar;
    private ExpandableView expandableView;
    private IntValueSetup densitySetup;
    private IntValueSetup squareSizeSetup;
    private ImageView previewHolder;
    private Button resetButton;

    private RescaledDrawable previewDrawable;
    private Bitmap previewBitmap;

    private PreviewImageCreator<?> calculatingCreator;
    private PreviewImageCreator<?> nextCalculatorToExecute;

    private int defaultSquareSize;
    private int defaultDensityPercents;

    @Override
    RandomPixelsImageCreator initializeImageCreator() {
        defaultSquareSize = getResources().getDimensionPixelSize(R.dimen.random_pixels_image_default_square_size);
        defaultDensityPercents = getResources().getInteger(R.integer.random_pixels_density_default_percents);
        float defaultDensity = valueToDensity(defaultDensityPercents);

        return new RandomPixelsImageCreator(defaultSquareSize, defaultDensity);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_random_pixels_image_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);

        initializeHeaderView();
        initializeContentView(view);
    }

    private void initializeHeaderView() {
        View headerView = expandableView.findHeaderView();
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                expandableView.toggleExpanded();
            }
        });

        TextView titleView = (TextView) headerView.findViewById(R.id.header_title);
        titleView.setText(R.string.random_pixels_title);

        previewHolder = (ImageView) headerView.findViewById(R.id.header_preview);
        previewHolder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                previewHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                calculateImageFirstTime();
            }
        });

        calculatingPreviewProgressBar = (ProgressBar) headerView.findViewById(R.id.header_progress);
    }

    private void initializeContentView(View rootView) {
        View contentView = expandableView.findContentView();

        squareSizeSetup = (IntValueSetup) contentView.findViewById(R.id.pixel_size_value_setup);
        densitySetup = (IntValueSetup) contentView.findViewById(R.id.density_value_setup);
        resetButton = (Button) rootView.findViewById(R.id.reset_button);
        View createImageButton = rootView.findViewById(R.id.create_button);

        squareSizeSetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                imageCreator.setSquareSizeInPixels(value);
                if (previewDrawable != null) {
                    previewDrawable.setScaleX(value);
                    previewDrawable.setScaleY(value);
                }
                updateResetButtonEnabledState();
            }
        });
        squareSizeSetup.setValue(imageCreator.getSquareSizeInPixels());

        densitySetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                float density = valueToDensity(value);
                imageCreator.setDensity(density);
                calculateImageFollowUp();
                updateResetButtonEnabledState();
            }
        });
        densitySetup.setValue(densityToValue(imageCreator.getDensity()));

        createImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewImageClicked();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareSizeSetup.setValue(defaultSquareSize);
                densitySetup.setValue(defaultDensityPercents);
            }
        });

        updateResetButtonEnabledState();
    }

    private void updateResetButtonEnabledState() {
        boolean isDefaultSquareSize = squareSizeSetup.getValue() == defaultSquareSize;
        boolean isDefaultDensity = densitySetup.getValue() == defaultDensityPercents;
        resetButton.setEnabled(! isDefaultSquareSize || ! isDefaultDensity);
    }

    private int densityToValue(float density) {
        return Math.round(density * 100f);
    }

    private float valueToDensity(int value) {
        return value / 100f;
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

    public ExpandableView getExpandableView() {
        return expandableView;
    }

    private abstract class PreviewImageCreator<Result> extends AsyncTask<Void, Void, Result> {

        float density;

        @CallSuper
        @Override
        protected void onPreExecute() {
            density = valueToDensity(densitySetup.getValue());
            calculatingCreator = this;
            nextCalculatorToExecute = null;
            calculatingPreviewProgressBar.setVisibility(View.VISIBLE);
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
            width = previewHolder.getWidth();
            height = previewHolder.getHeight();
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
                previewDrawable = imageCreator.createDrawableFromBitmap(getResources(), bitmap);
                ViewCompat.setBackground(previewHolder, previewDrawable);

                if (nextCalculatorToExecute != null)
                    nextCalculatorToExecute.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    calculatingPreviewProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class FollowUpPreviewImageCreator extends PreviewImageCreator<Void> {

        @Override
        protected Void doInBackground(Void... params) {
            RandomPixelsImageCreator.drawRandomPixelsToImage(previewBitmap, density);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            calculatingPreviewProgressBar.setVisibility(View.INVISIBLE);
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
