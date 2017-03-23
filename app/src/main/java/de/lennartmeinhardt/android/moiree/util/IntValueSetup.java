package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import de.lennartmeinhardt.android.moiree.R;

public class IntValueSetup extends BaseValueSetup {

    private static final String KEY_SUPER_STATE = "de.lennartmeinhardt.android.moiree:intValueSetup:superState";
    private static final String KEY_VALUE = "de.lennartmeinhardt.android.moiree:intValueSetup:value";

    private int minValue = 0;
    private int maxValue = 100;
    private int stepSize = 1;
    private int value = 0;
    private boolean allowStepOutOfBounds = false;

    private TextFormatter textFormatter;

    private OnValueChangedListener onValueChangedListener;

    private boolean isSeekBarInputActive;
    private boolean decreaseButtonInputActive;
    private boolean increaseButtonInputActive;

    public IntValueSetup(Context context) {
        super(context);

        setMaxValue(100);
        setStepSize(1);
        setAllowStepOutOfBounds(true);
        setValueInternal(0, true);

        initializeListeners();
    }

    public IntValueSetup(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeAndLoadValuesFromAttributes(context, attrs);
        initializeListeners();
    }

    public IntValueSetup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initializeAndLoadValuesFromAttributes(context, attrs);
        initializeListeners();
    }

    private void initializeAndLoadValuesFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntValueSetup, 0, 0);
        try {
            setMaxValue(a.getInteger(R.styleable.IntValueSetup_maxValue, this.maxValue));
            setMinValue(a.getInteger(R.styleable.IntValueSetup_minValue, this.minValue));
            setStepSize(a.getInteger(R.styleable.IntValueSetup_stepSize, stepSize));
            setAllowStepOutOfBounds(a.getBoolean(R.styleable.IntValueSetup_allowStepOutOfBounds, allowStepOutOfBounds));
            setValueInternal(a.getInteger(R.styleable.IntValueSetup_value, value), true);

            String name = a.getString(R.styleable.IntValueSetup_name);
            valueNameTextView.setText(name);

            final int formatterId = a.getResourceId(R.styleable.IntValueSetup_textFormatter, 0);
            if(formatterId != 0) {
                setTextFormatter(new TextFormatter() {
                    @Override
                    public String formatInt(int value) {
                        return getResources().getString(formatterId, value);
                    };
                });
            }
        } finally {
            a.recycle();
        }
    }

    private void initializeListeners() {
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDecreaseButtonClicked();
            }
        });
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIncreaseButtonClicked();
            }
        });

        valueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setValueInternal(progress + minValue, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarInputActive = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarInputActive = false;
            }
        });
    }

    public int getStepSize() {
        return stepSize;
    }
    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        setValueInternal(value, false);
    }

    public int getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(int maxValue) {
        if(this.maxValue != maxValue) {
            maxValue = Math.max(minValue, maxValue);
            this.maxValue = maxValue;
            valueSeekbar.setMax(maxValue - minValue);
            setValueInternal(value, true);
        }
    }

    public int getMinValue() {
        return minValue;
    }
    public void setMinValue(int minValue) {
        if(this.minValue != minValue) {
            minValue = Math.min(minValue, maxValue);
            this.minValue = minValue;
            valueSeekbar.setMax(maxValue - minValue);
            setValueInternal(value, true);
        }
    }

    private void setValueInternal(int value, boolean forceUpdate) {
        if(forceUpdate || this.value != value) {
            this.value = value;
            updateUI();
            if(onValueChangedListener != null)
                onValueChangedListener.onValueChanged(this, value, isUserInputActive());
        }
    }

    private void updateUI() {
        updateButtonsState();
        updateValueText();
        updateSeekBarProgress();
    }

    private void updateButtonsState() {
        decreaseButton.setEnabled(allowStepOutOfBounds || value > minValue);
        increaseButton.setEnabled(allowStepOutOfBounds || value < maxValue);
    }

    private void updateValueText() {
        valueTextView.setText(valueToText(value));
    }

    private void updateSeekBarProgress() {
        if(! isSeekBarInputActive)
            valueSeekbar.setProgress(value - minValue);
    }

    protected void onIncreaseButtonClicked() {
        increaseButtonInputActive = true;
        stepValue(stepSize, false);
        increaseButtonInputActive = false;
    }

    protected void onDecreaseButtonClicked() {
        decreaseButtonInputActive = true;
        stepValue(-stepSize, false);
        decreaseButtonInputActive = false;
    }

    private void stepValue(int stepSize, boolean forceUpdate) {
        setValueInternal(value + stepSize, forceUpdate);
    }

    private String valueToText(int value) {
        if(textFormatter != null)
            return textFormatter.formatInt(value);
        else
            return Integer.toString(value);
    }

    public boolean isAllowStepOutOfBounds() {
        return allowStepOutOfBounds;
    }
    public void setAllowStepOutOfBounds(boolean allowStepOutOfBounds) {
        this.allowStepOutOfBounds = allowStepOutOfBounds;
        updateButtonsState();
    }

    public boolean isUserInputActive() {
        return isSeekBarInputActive || increaseButtonInputActive || decreaseButtonInputActive;
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }

    public TextFormatter getTextFormatter() {
        return textFormatter;
    }
    public void setTextFormatter(TextFormatter textFormatter) {
        this.textFormatter = textFormatter;
        updateValueText();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle outState = new Bundle();
        Parcelable superState = super.onSaveInstanceState();
        if(superState != null)
            outState.putParcelable(KEY_SUPER_STATE, superState);
        outState.putInt(KEY_VALUE, value);
        return outState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle instanceState = (Bundle) state;
        Parcelable superState = instanceState.getParcelable(KEY_SUPER_STATE);
        super.onRestoreInstanceState(superState);
        int value = instanceState.getInt(KEY_VALUE);
        setValueInternal(value, true);
    }
    public interface OnValueChangedListener {
        void onValueChanged(IntValueSetup intValueSetup, int value, boolean fromUser);
    }

    public interface TextFormatter {
        String formatInt(int value);
    }
}
