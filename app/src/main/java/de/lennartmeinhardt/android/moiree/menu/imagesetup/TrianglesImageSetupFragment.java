package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import de.lennartmeinhardt.android.moiree.databinding.FragmentTriangleImageSetupBinding;
import de.lennartmeinhardt.android.moiree.imaging.TrianglesImageCreator;
import de.lennartmeinhardt.android.moiree.util.Expandable;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.Expandables;

public class TrianglesImageSetupFragment extends BaseImageCreatorSetupFragment implements Expandable {

    private TrianglesImageCreator imageCreator;

    private BitmapDrawable previewDrawable;
    private Bitmap previewBitmap;

    private PreviewImageCreator<?> calculatingCreator;
    private PreviewImageCreator<?> nextCalculatorToExecute;

    private FragmentTriangleImageSetupBinding binding;


    @Override
    protected TrianglesImageCreator getMoireeImageCreator() {
        if(imageCreator == null) {
            int defaultTriangleSize = getResources().getDimensionPixelSize(R.dimen.triangles_image_default_triangle_size);

            imageCreator = new TrianglesImageCreator(defaultTriangleSize);
        }
        return imageCreator;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_triangle_image_setup, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // make sure the lazily initialized image creator exists
        getMoireeImageCreator();

        binding.setTriangleImageCreator(imageCreator);

        int defaultTriangleSize = getResources().getDimensionPixelSize(R.dimen.triangles_image_default_triangle_size);
        binding.setDefaultTriangleSizeInPixels(defaultTriangleSize);

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
        imageCreator.triangleSizeInPixels.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
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
                imageCreator.triangleSizeInPixels.set(binding.getDefaultTriangleSizeInPixels());
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
        return binding.expandableView;
    }

    private abstract class PreviewImageCreator<Result> extends AsyncTask<Void, Void, Result> {

        int triangleSize;

        @CallSuper
        @Override
        protected void onPreExecute() {
            triangleSize = imageCreator.triangleSizeInPixels.get();
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

        private TrianglesImageCreator previewImageCreator;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            width = binding.expandableViewHeader.getPreviewImageView().getWidth();
            height = binding.expandableViewHeader.getPreviewImageView().getHeight();
            previewImageCreator = new TrianglesImageCreator(triangleSize);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return previewImageCreator.createBitmapForDimensions(width, height);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(getActivity() != null) {
                previewBitmap = bitmap;
                previewDrawable = imageCreator.createDrawableFromBitmap(getResources(), bitmap);
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
            TrianglesImageCreator.drawTrianglesToImage(previewBitmap, triangleSize);
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
