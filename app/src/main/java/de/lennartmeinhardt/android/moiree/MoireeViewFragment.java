package de.lennartmeinhardt.android.moiree;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lennartmeinhardt.android.moiree.databinding.FragmentMoireeViewBinding;

public class MoireeViewFragment extends Fragment {

    private FragmentMoireeViewBinding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_moiree_view, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MoireeColors moireeColors = ((MoireeColorsHolder) getActivity()).getMoireeColors();
        MoireeTransformation moireeTransformation = ((MoireeTransformationHolder) getActivity()).getMoireeTransformation();
        binding.setMoireeTransformation(moireeTransformation);
        binding.setMoireeColors(moireeColors);
    }

    public void setMoireeImage(Drawable drawable, boolean shouldRecycleOld) {
        Drawable oldDrawable = binding.moireeImageFixed.getDrawable();
        if(drawable != oldDrawable) {
            if (shouldRecycleOld)
                recycleRecursivelyIfPossible(oldDrawable);

            binding.moireeImageFixed.setImageDrawable(drawable);
            binding.moireeImageTransformed.setImageDrawable(drawable);
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
            }
        }
    }

    public int getMoireeImageWidth() {
        return binding.moireeImageFixed.getWidth();
    }

    public int getMoireeImageHeight() {
        return binding.moireeImageFixed.getHeight();
    }

    public void setMoireeViewsVisible(boolean visible) {
        /*
         * This could has been tested using binding as well; however it's buggy.
         * In a previous version the binding contained boolean moireeViewsVisible and the image views' visibility was bound like this
         * android:visibility="@{moireeViewsVisible ? View.VISIBLE : View.INVISIBLE}"
         * However, when using transitions (Fade.OUT followed by Fade.IN) the visibility was still set to invisible, after both transitions ran.
         *
         * By the way, this solution is the easiest and least verbose (code-wise).
         */
        float targetAlpha = visible ? 1 : 0;
        binding.imageViewsHolder.animate().alpha(targetAlpha);
    }

    public void setMoireeTransformation(MoireeTransformation moireeTransformation) {
        binding.setMoireeTransformation(moireeTransformation);
        // update the UI now
        binding.executePendingBindings();
    }
}
