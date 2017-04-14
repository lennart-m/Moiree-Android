package de.lennartmeinhardt.android.moiree.menu.imagesetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.R;
import de.lennartmeinhardt.android.moiree.imaging.HorizontalLinesImageCreator;
import de.lennartmeinhardt.android.moiree.imaging.RescaledDrawable;
import de.lennartmeinhardt.android.moiree.util.ExpandableView;
import de.lennartmeinhardt.android.moiree.util.IntValueSetup;

public class HorizontalLinesSetupFragment extends BaseImageCreatorSetupFragment<HorizontalLinesImageCreator> {

    private ExpandableView expandableView;

    private ImageView preview;

    private int defLineThickness;

    private RescaledDrawable previewDrawable;

    private IntValueSetup lineThicknessSetup;
    private Button resetButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_horizontal_lines_image_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        expandableView = (ExpandableView) view.findViewById(R.id.expandable_view);

        initializeHeaderView();
        initializeContentView();
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
        titleView.setText(R.string.horizontal_lines_title);

        preview = (ImageView) headerView.findViewById(R.id.header_preview);
        preview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                preview.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                initializePreviewDrawable();
            }
        });
    }

    private void initializeContentView() {
        View contentView = expandableView.findContentView();

        resetButton = (Button) contentView.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineThicknessSetup.setValue(defLineThickness);
            }
        });

        lineThicknessSetup = (IntValueSetup) contentView.findViewById(R.id.line_thickness_value_setup);
        lineThicknessSetup.setOnValueChangedListener(new IntValueSetup.OnValueChangedListener() {
            @Override
            public void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser) {
                imageCreator.setLineThicknessInPixels(value);
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
        HorizontalLinesImageCreator previewImageCreator = new HorizontalLinesImageCreator(1);
        previewDrawable = previewImageCreator.createMoireeImageForDimensions(getResources(), preview.getWidth(), preview.getHeight());
        previewDrawable.setScaleX(imageCreator.getLineThicknessInPixels());
        previewDrawable.setScaleY(imageCreator.getLineThicknessInPixels());
        ViewCompat.setBackground(preview, previewDrawable);
    }

    private void updateUI(boolean fromValueSetup) {
        int lineThickness = imageCreator.getLineThicknessInPixels();

        resetButton.setEnabled(lineThickness != defLineThickness);

        if(! fromValueSetup)
            lineThicknessSetup.setValue(lineThickness);
    }

    @Override
    HorizontalLinesImageCreator initializeImageCreator() {
        defLineThickness = getResources().getDimensionPixelSize(R.dimen.horizontal_lines_image_default_line_thickness);
        return new HorizontalLinesImageCreator(defLineThickness);
    }

    public ExpandableView getExpandableView() {
        return expandableView;
    }
}
