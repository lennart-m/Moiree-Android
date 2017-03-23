package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import de.lennartmeinhardt.android.moiree.R;

// TODO mirror sat-val-selection in RTL mode
// TODO interface colorPicker hat getselected und setselected, außerdem add und remove listener
// TODO dies wird HsbColorPicker
public class ColorPicker extends RelativeLayout {

    private final float[] tmpHsv = new float[3];

    private SeekPane satValSelection;

    private SeekPane hueSelection;

    private ColorDrawable backgroundColorDrawable;

    private int selectedColor;

    private boolean hueUserInputActive, satValUserInputActive;

    private OnColorSelectionChangeListener onColorSelectionChangeListener;


    public ColorPicker(Context context) {
        super(context);

        selectedColor = getInitialColor(context);
        inflateColorPicker(context);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadColorFromAttributes(context, attrs);
        inflateColorPicker(context);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loadColorFromAttributes(context, attrs);
        inflateColorPicker(context);
    }

    private static int getInitialColor(Context context) {
        return ContextCompat.getColor(context, R.color.color_picker_default);
    }

    private void loadColorFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPicker, 0, 0);
        try {
            selectedColor = a.getColor(R.styleable.ColorPicker_selectedColor, Color.BLACK);
        } finally {
            a.recycle();
        }
    }

    private void inflateColorPicker(Context context) {
        inflate(context, R.layout.color_picker, this);

        initializeHueSelection();
        initializeSatValSelection();

        updateUiFromColor();
    }

    private void initializeSatValSelection() {
        satValSelection = (SeekPane) findViewById(R.id.sat_val_selection);

        // create the sat/val preview drawable. it consist of the color layer (displays the color at full hue) and the mask
        backgroundColorDrawable = new ColorDrawable();
        Drawable previewMaskDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.sat_val_mask, getContext().getTheme());
        LayerDrawable satValSelectionPreview = new LayerDrawable(new Drawable[] {backgroundColorDrawable, previewMaskDrawable});
        ViewCompat.setBackground(satValSelection, satValSelectionPreview);

        satValSelection.setOnPosition2DChangeListener(new SeekPane.OnPosition2DChangeListener() {
            @Override
            public void onPositionXChanged(SeekPane seekPane, int positionX, boolean fromUser) {
                if (fromUser)
                    onColorSelectionChanged();
            }

            @Override
            public void onPositionYChanged(SeekPane seekPane, int positionY, boolean fromUser) {
                if (fromUser)
                    onColorSelectionChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekPane seekPane) {
                satValUserInputActive = true;
            }

            @Override
            public void onStopTrackingTouch(SeekPane seekPane) {
                satValUserInputActive = false;
            }
        });
    }

    private void initializeHueSelection() {
        hueSelection = (SeekPane) findViewById(R.id.hue_selection);
        hueSelection.setOnPosition2DChangeListener(new SeekPane.OnPosition2DChangeListener.Adapter() {
            @Override
            public void onPositionXChanged(SeekPane seekPane, int positionX, boolean fromUser) {
                if (fromUser) {
                    updateSatValSelectionBackground(getHueFromInput());
                    onColorSelectionChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekPane seekPane) {
                hueUserInputActive = true;
            }

            @Override
            public void onStopTrackingTouch(SeekPane seekPane) {
                hueUserInputActive = false;
            }
        });
    }

    private void updateUiFromColor() {
        Color.colorToHSV(selectedColor, tmpHsv);
        setHueProgressFromValue(tmpHsv[0]);
        setSatValPositionFromValues(tmpHsv[1], tmpHsv[2]);

        updateSatValSelectionBackground(tmpHsv[0]);
    }
    private void setHueProgressFromValue(float hue) {
        int hueSelectionProgress = (int) (hueSelection.getMaxX() * hue / 360f);
        hueSelection.setPositionX(hueSelectionProgress);
    }
    private void setSatValPositionFromValues(float sat, float val) {
        int positionX = (int) (sat * satValSelection.getMaxX());
        satValSelection.setPositionX(positionX);
        int positionY = (int) (val * satValSelection.getMaxY());
        satValSelection.setPositionY(positionY);
    }

    private void updateSatValSelectionBackground(float hue) {
        tmpHsv[0] = hue;
        tmpHsv[1] = tmpHsv[2] = 1;
        int colorForSatValBackground = Color.HSVToColor(tmpHsv);
        backgroundColorDrawable.setColor(colorForSatValBackground);
    }

    private float getHueFromInput() {
        return hueSelection.getMaxX() > 0 ? 360f * hueSelection.getPositionX() / hueSelection.getMaxX() : 0;
    }
    private float getSatFromInput() {
        return (1f * satValSelection.getPositionX()) / satValSelection.getMaxX();
    }
    private float getValFromInput() {
        return (1f * satValSelection.getPositionY()) / satValSelection.getMaxY();
    }

    private void onColorSelectionChanged() {
        tmpHsv[0] = getHueFromInput();
        tmpHsv[1] = getSatFromInput();
        tmpHsv[2] = getValFromInput();

        int color = Color.HSVToColor(tmpHsv);
        setColorInternal(color, true);
    }


    private void setColorInternal(int color, boolean fromUser) {
        if (selectedColor != color) {
            this.selectedColor = color;
            if (onColorSelectionChangeListener != null)
                onColorSelectionChangeListener.onColorSelectionChanged(this, color, fromUser);
        }
    }

    public void setSelectedColor(int color) {
        setColorInternal(color, false);
        updateUiFromColor();
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setOnColorSelectionChangeListener(OnColorSelectionChangeListener onColorSelectionChangeListener) {
        this.onColorSelectionChangeListener = onColorSelectionChangeListener;
    }

    public boolean isUserInputActive() {
        return hueUserInputActive || satValUserInputActive;
    }


    public interface OnColorSelectionChangeListener {
        void onColorSelectionChanged(ColorPicker colorPicker, int color, boolean fromUser);
    }
}
