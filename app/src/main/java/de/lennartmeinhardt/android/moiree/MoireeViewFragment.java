package de.lennartmeinhardt.android.moiree;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.lennartmeinhardt.android.moiree.transition.ColorFilterHelper;

public class MoireeViewFragment extends Fragment {

    private ImageView fixedImageView;
    private ImageView transformedImageView;
    private View backgroundView;

    private MoireeTransformation moireeTransformation;
    private MoireeColors moireeColors;

    private final MoireeColors.MoireeColorsListener moireeColorsUpdater = new MoireeColors.MoireeColorsListener() {
        @Override
        public void onBackgroundColorChanged(int newColor) {
            backgroundView.setBackgroundColor(newColor);
        }
        @Override
        public void onForegroundColorChanged(int newColor) {
            ColorFilterHelper.setColorFilterAndTag(fixedImageView, newColor);
            ColorFilterHelper.setColorFilterAndTag(transformedImageView, newColor);
        }
    };

    MoireeTransformation.TransformationListener transformedViewUpdater = new MoireeTransformation.TransformationListener() {
        @Override
        public void onTransformationChanged() {
            transformedImageView.setScaleX(moireeTransformation.getEffectiveScalingX());
            transformedImageView.setScaleY(moireeTransformation.getEffectiveScalingY());
            transformedImageView.setRotation(moireeTransformation.getRotation());
            transformedImageView.setTranslationX(moireeTransformation.getTranslationX());
            // reverse y translation because bottom > top in android's coordinate system
            transformedImageView.setTranslationY(- moireeTransformation.getTranslationY());
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_moiree_view, container, false);
        backgroundView = root.findViewById(R.id.moiree_background);
        fixedImageView = (ImageView) root.findViewById(R.id.moiree_image_fixed);
        transformedImageView = (ImageView) root.findViewById(R.id.moiree_image_transformed);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.moireeColors = ((MoireeColorsHolder) getActivity()).getMoireeColors();
        this.moireeTransformation = ((MoireeTransformationHolder) getActivity()).getMoireeTransformation();

        moireeColors.addAndFireMoireeColorsListener(moireeColorsUpdater);
        moireeTransformation.addTransformationListener(transformedViewUpdater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        moireeColors.removeMoireeColorsListener(moireeColorsUpdater);
        moireeTransformation.removeTransformationListener(transformedViewUpdater);
    }


    public void setMoireeImage(Drawable drawable, boolean shouldRecycleOld) {
        Drawable oldDrawable = fixedImageView.getDrawable();
        if(drawable != oldDrawable) {
            if (shouldRecycleOld)
                recycleRecursivelyIfPossible(oldDrawable);

            fixedImageView.setImageDrawable(drawable);
            transformedImageView.setImageDrawable(drawable);
        }
    }

    private void recycleRecursivelyIfPossible(Drawable drawable) {
        while(drawable != null) {
            if(drawable instanceof BitmapDrawable) {
                ((BitmapDrawable) drawable).getBitmap().recycle();
                break;
            }
            if(drawable instanceof DrawableWrapper) {
                drawable = ((DrawableWrapper) drawable).getWrappedDrawable();
                continue;
            }
        }
    }

    public int getMoireeImageWidth() {
        return fixedImageView.getWidth();
    }

    public int getMoireeImageHeight() {
        return fixedImageView.getHeight();
    }

    public void setMoireeViewsVisible(boolean visible) {
        fixedImageView.setVisibility(View.GONE);
        transformedImageView.setVisibility(View.GONE);
    }
}
