package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
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
@InverseBindingMethods(
        @InverseBindingMethod(type = HsbColorPicker.class, attribute = "selectedColor")
)
public class HsbColorPicker extends RelativeLayout {

    private final float[] tmpHsv = new float[3];

    private SeekPane satValSelection;

    private SeekPane hueSelection;

    private ColorDrawable backgroundColorDrawable;

    private int selectedColor;

    private OnColorSelectionChangedListener onColorSelectionChangedListener;


    public HsbColorPicker(Context context) {
        super(context);

        selectedColor = getInitialColor(context);
        inflateColorPicker(context);
    }

    public HsbColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        loadColorFromAttributes(context, attrs);
        inflateColorPicker(context);
    }

    public HsbColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loadColorFromAttributes(context, attrs);
        inflateColorPicker(context);
    }

    private static int getInitialColor(Context context) {
        return ContextCompat.getColor(context, R.color.color_picker_default);
    }

    private void loadColorFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HsbColorPicker, 0, 0);
        try {
            selectedColor = a.getColor(R.styleable.HsbColorPicker_selectedColor, Color.BLACK);
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
            }

            @Override
            public void onStopTrackingTouch(SeekPane seekPane) {
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
            }

            @Override
            public void onStopTrackingTouch(SeekPane seekPane) {
            }
        });
    }

    private void updateUiFromColor() {
        Color.colorToHSV(selectedColor, tmpHsv);

        final float hue = tmpHsv[0],
                    sat = tmpHsv[1],
                    val = tmpHsv[2];

        setHueProgressFromValue(hue);
        setSatValPositionFromValues(sat, val);

        updateSatValSelectionBackground(hue);
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
            if (onColorSelectionChangedListener != null)
                onColorSelectionChangedListener.onColorSelectionChanged(this, color, fromUser);
        }
    }

    public void setSelectedColor(int color) {
        if(! isUserInputActive()) {
            setColorInternal(color, false);
            updateUiFromColor();
        }
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setOnColorSelectionChangedListener(OnColorSelectionChangedListener onColorSelectionChangedListener) {
        this.onColorSelectionChangedListener = onColorSelectionChangedListener;
    }

    public boolean isUserInputActive() {
        return hueSelection.isInputActive() || satValSelection.isInputActive();
    }


    public interface OnColorSelectionChangedListener {
        void onColorSelectionChanged(HsbColorPicker colorPicker, int color, boolean fromUser);
    }

    @BindingAdapter("selectedColorAttrChanged")
    public static void setColorSelectionListener(HsbColorPicker colorPicker, final InverseBindingListener bindingListener) {
        if(bindingListener == null)
            colorPicker.setOnColorSelectionChangedListener(null);
        else
            colorPicker.setOnColorSelectionChangedListener(new OnColorSelectionChangedListener() {
                @Override
                public void onColorSelectionChanged(HsbColorPicker colorPicker, int color, boolean fromUser) {
                    if(fromUser)
                        bindingListener.onChange();
                }
            });
    }
}
