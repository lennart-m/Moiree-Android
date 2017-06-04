package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.databinding.FragmentHorizontalLinesImageSetupBinding;
import de.lennartmeinhardt.android.moiree.imaging.HorizontalLinesImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RescaledDrawable;
import de.lennartmeinhardt.android.moiree.util.Expandable;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.Expandables;

public class HorizontalLinesImageSetupFragment extends BaseImageCreatorSetupFragment implements Expandable {

    private HorizontalLinesImageCreator imageCreator;

    private RescaledDrawable previewDrawable;

    private FragmentHorizontalLinesImageSetupBinding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_horizontal_lines_image_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // make sure the lazily initialized image creator exists
        getMoireeImageCreator();

        binding.setHorizontalLinesImageCreator(imageCreator);

        int defLineThickness = getResources().getDimensionPixelSize(R.dimen.horizontal_lines_image_default_line_thickness);
        binding.setDefaultLineThicknessInPixels(defLineThickness);

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

                initializePreviewDrawable();
            }
        });
    }

    private void initializeContentView() {
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCreator.lineThicknessInPixels.set(binding.getDefaultLineThicknessInPixels());
            }
        });

        imageCreator.lineThicknessInPixels.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (previewDrawable != null) {
                    previewDrawable.setScaleX(imageCreator.lineThicknessInPixels.get());
                    previewDrawable.setScaleY(imageCreator.lineThicknessInPixels.get());
                }
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

    private void initializePreviewDrawable() {

        final int width = binding.expandableViewHeader.getPreviewImageView().getWidth();
        final int height = binding.expandableViewHeader.getPreviewImageView().getHeight();
        // create a new image creator with pixel size = 1 just for the image. the image does not have to be re-created if pixel size changes, just scale it.
        final HorizontalLinesImageCreator previewImageCreator = new HorizontalLinesImageCreator(1);

        // create the image in background
        new AsyncTask<Void, Void, BitmapDrawable>() {

            @Override
            protected void onPreExecute() {
                binding.expandableViewHeader.setBusy(true);
            }

            @Override
            protected BitmapDrawable doInBackground(Void... params) {
                return previewImageCreator.createMoireeImageForDimensions(getResources(), width, height);
            }

            @Override
            protected void onPostExecute(BitmapDrawable drawable) {
                // only operate on the ui if this fragment is attached
                if(getActivity() != null) {
                    binding.expandableViewHeader.setBusy(false);
                    previewDrawable = new RescaledDrawable(drawable);
                    previewDrawable.setScaleX(imageCreator.lineThicknessInPixels.get());
                    previewDrawable.setScaleY(imageCreator.lineThicknessInPixels.get());
                    binding.expandableViewHeader.setDrawable(previewDrawable);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected HorizontalLinesImageCreator getMoireeImageCreator() {
        if(imageCreator == null) {
            int defLineThickness = getResources().getDimensionPixelSize(R.dimen.horizontal_lines_image_default_line_thickness);
            imageCreator = new HorizontalLinesImageCreator(defLineThickness);
        }
        return imageCreator;
    }

    public ExpandableView getExpandableView() {
        return binding.expandableView;
    }
}
