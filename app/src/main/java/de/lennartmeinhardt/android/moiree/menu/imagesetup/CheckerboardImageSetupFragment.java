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
import de.lennartmeinhardt.android.moiree.imaging.RescaledDrawable;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class CheckerboardImageSetupFragment extends BaseImageCreatorSetupFragment<CheckerboardImageCreator> implements Expandable {

    private ExpandableView expandableView;

    private ImageView preview;

    private int defSquareSize;

    private RescaledDrawable previewDrawable;

    private IntValueSetup squareSizeSetup;
    private Button resetButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkerboard_image_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);

        initializeHeaderView();
        initializeContentView();
    }

    private void initializeHeaderView() {
        View headerView = expandableView.getHeaderView();
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginMenuBoundsTransition();
                expandableView.toggleExpanded();
            }
        });

        TextView titleView = (TextView) headerView.findViewById(R.id.header_title);
        titleView.setText(R.string.checkerboard_title);

        preview = (ImageView) headerView.findViewById(R.id.header_preview);
        preview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT < 16)
                    preview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    preview.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                initializePreviewDrawable();
            }
        });
    }

    private void initializeContentView() {
        View contentView = expandableView.getContentView();

        resetButton = (Button) contentView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                squareSizeSetup.setValue(defSquareSize);
            }
        });

        squareSizeSetup = (IntValueSetup) contentView.findViewById(R.id.pixel_size_value_setup);
        squareSizeSetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                imageCreator.setSquareSizeInPixels(value);
                if (previewDrawable != null) {
                    previewDrawable.setScaleX(value);
                    previewDrawable.setScaleY(value);
                }
                updateUI(true);
            }
        });

        ImageButton applyButton = (ImageButton) contentView.findViewById(R.id.create_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateNewImageClicked();
            }
        });

        updateUI(false);
    }

    private void initializePreviewDrawable() {
        CheckerboardImageCreator previewImageCreator = new CheckerboardImageCreator(1);
        previewDrawable = previewImageCreator.createMoireeImageForDimensions(getResources(), preview.getWidth(), preview.getHeight());
        previewDrawable.setScaleX(imageCreator.getSquareSizeInPixels());
        previewDrawable.setScaleY(imageCreator.getSquareSizeInPixels());
        preview.setImageDrawable(previewDrawable);
    }

    private void updateUI(boolean fromValueSetup) {
        int squareSize = imageCreator.getSquareSizeInPixels();

        resetButton.setEnabled(squareSize != defSquareSize);

        if(! fromValueSetup)
            squareSizeSetup.setValue(squareSize);
    }

    @Override
    CheckerboardImageCreator initializeImageCreator() {
        defSquareSize = getResources().getDimensionPixelSize(R.dimen.checkerboard_image_default_square_size);
        return new CheckerboardImageCreator(defSquareSize);
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
