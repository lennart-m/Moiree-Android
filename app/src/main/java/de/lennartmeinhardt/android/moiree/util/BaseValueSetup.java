package de.lennartmeinhardt.android.moiree.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import de.lennartmeinhardt.android.moiree.R;

public abstract class BaseValueSetup extends RelativeLayout {

    protected TextView valueNameTextView;
    protected TextView valueTextView;

    protected ImageButton decreaseButton;
    protected ImageButton increaseButton;

    protected SeekBar valueSeekbar;


    public BaseValueSetup(Context context) {
        super(context);
        inflateLayout(context);
    }

    public BaseValueSetup(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    public BaseValueSetup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateLayout(context);
    }

    private void inflateLayout(Context context) {
        inflate(context, R.layout.value_setup, this);

        valueNameTextView = (TextView) findViewById(R.id.value_setup_name);
        valueTextView = (TextView) findViewById(R.id.value_setup_value);

        decreaseButton = (ImageButton) findViewById(R.id.value_setup_decrease_button);
        increaseButton = (ImageButton) findViewById(R.id.value_setup_increase_button);

        valueSeekbar = (SeekBar) findViewById(R.id.value_setup_slider);
    }
}
