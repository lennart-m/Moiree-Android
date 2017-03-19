package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.Expandable;
import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.CheckerboardImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.MoireeImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RescaledDrawable;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class CheckerboardImageSetupFragment extends BaseImageCreatorSetupFragment implements Expandable {

    private CheckerboardImageCreator imageCreator;

    private ExpandableView expandableView;

    private ImageView preview;

    private int defSquareSize;

    private RescaledDrawable previewDrawable;

    private IntValueSetup pixelSizeSetup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        defSquareSize = getResources().getDimensionPixelSize(R.dimen.checkerboard_image_default_square_size);
        imageCreator = new CheckerboardImageCreator(defSquareSize);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_checkerboard_image_setup, container, false);

        expandableView = (ExpandableView) rootView.findViewById(R.id.expandable_view);

        pixelSizeSetup = (IntValueSetup) rootView.findViewById(R.id.pixel_size_value_setup);

        View header = expandableView.findViewById(R.id.expandable_view_header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                expandableView.toggleExpanded();
            }
        });

        ImageButton applyButton = (ImageButton) rootView.findViewById(R.id.create_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewImageClicked();
            }
        });

        TextView titleView = (TextView) header.findViewById(R.id.expandable_view_header_title);
        titleView.setText(R.string.checkerboard_title);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT < 16)
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                initializePreviewDrawable();
            }
        });

        Button resetButton = (Button) rootView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pixelSizeSetup.setValue(defSquareSize);
            }
        });

        preview = (ImageView) rootView.findViewById(R.id.expandable_view_header_preview);

        pixelSizeSetup.setIntStringConverter(new IntValueSetup.IntStringConverter() {
            @Override
            public String intToText(int value) {
                return getResources().getString(R.string.integer_pixel_formatter, value);
            }
        });
        pixelSizeSetup.setOnIntValueSelectedListener(new IntValueSetup.OnIntValueUpdatedListener.Adapter() {
            @Override
            public void onIntValueSelected(IntValueSetup intValueSetup, int value) {
                imageCreator.setSquareSizeInPixels(value);
                if(previewDrawable != null) {
                    previewDrawable.setScaleX(value);
                    previewDrawable.setScaleY(value);
                }
            }
        });
        pixelSizeSetup.setValue(imageCreator.getSquareSizeInPixels());

        return rootView;
    }

    private void initializePreviewDrawable() {
        CheckerboardImageCreator previewImageCreator = new CheckerboardImageCreator(1);
        previewDrawable = previewImageCreator.createMoireeImageForDimensions(getResources(), preview.getWidth(), preview.getHeight());
        previewDrawable.setScaleX(imageCreator.getSquareSizeInPixels());
        previewDrawable.setScaleY(imageCreator.getSquareSizeInPixels());
        preview.setImageDrawable(previewDrawable);
    }

    @Override
    MoireeImageCreator getMoireeImageCreator() {
        return imageCreator;
    }

    @Override
    public boolean isExpanded() {
        return expandableView.isExpanded();
    }

    @Override
    public void setExpanded(boolean expanded) {
        expandableView.setExpanded(expanded);
    }

    @Override
    public void toggleExpanded() {
        expandableView.toggleExpanded();
    }
}
